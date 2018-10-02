package robot;

import java.util.List;
import java.util.concurrent.TimeUnit;

import map.Map;
import map.MapConstant;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.Comms;
import map.Tile;

/**
 * @author 18/19 S1 G3
 */
public class Robot {
	
	//SENSORS
    private final Sensor SRFrontLeft;       // north-facing front-left SR
    private final Sensor SRFrontCenter;     // north-facing front-center SR
    private final Sensor SRFrontRight;      // north-facing front-right SR
    private final Sensor SRLeft;            // west-facing left SR
    private final Sensor SRRight;           // east-facing right SR
    private final Sensor LRLeft;            // west-facing left LR
 
    //COORDINATES
	private int robotRow; // Coordinate of centre component
	private int robotCol; // Coordinate of centre component
	private DIRECTION robotDir;
	
	//OTHERS
	private boolean realBot;
	private int speed;

	
	// Constructor(s)
	/**
	 * Instantiate a robot 'placed' at coordinate row(x) and col(y)
	 * facing up (default), with an blank map
	 * @param startRow Centre row(x)-coordinate of Robot
	 * @param startCol Centre row(y)-coordinate of Robot

	 * @param isReal
	 * @see map
	 */
	public Robot (int startRow, int startCol, boolean isReal){
		SRFrontLeft = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol - 1, this.robotDir, "SRFL");
        SRFrontCenter = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol, this.robotDir, "SRFC");
        SRFrontRight = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol + 1, this.robotDir, "SRFR");
        SRLeft = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT), "SRL");
        SRRight = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNRIGHT), "SRR");
        LRLeft = new Sensor(RobotConstant.LONG_IR_MIN, RobotConstant.LONG_IR_MAX, this.robotRow, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT), "LRL");
		
		robotRow = startRow;
		robotCol = startCol;
		robotDir = RobotConstant.DEFAULT_START_DIR;
		speed = RobotConstant.SPEED;
		realBot = isReal;
	}
	
	/**
	 * Instantiate a robot 'placed' at coordinate row(x) and col(y)
	 * facing in set direction, with an blank map
	 * @param startRow Centre row(x)-coordinate of Robot
	 * @param startCol Centre col(y)-coordinate of Robot

	 * @param isReal
	 * @param direction Direction Robot's facing
	 */
	public Robot(int startRow, int startCol, DIRECTION startDir, boolean isReal) {
		SRFrontLeft = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol - 1, this.robotDir, "SRFL");
        SRFrontCenter = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol, this.robotDir, "SRFC");
        SRFrontRight = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol + 1, this.robotDir, "SRFR");
        SRLeft = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT), "SRL");
        SRRight = new Sensor(RobotConstant.SHORT_IR_MIN, RobotConstant.SHORT_IR_MAX, this.robotRow + 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNRIGHT), "SRR");
        LRLeft = new Sensor(RobotConstant.LONG_IR_MIN, RobotConstant.LONG_IR_MAX, this.robotRow, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT), "LRL");

		robotRow = startRow;
		robotCol = startCol;
		robotDir = startDir;
		speed = RobotConstant.SPEED;

		realBot = isReal;
	}
	
	// Getter(s)
	/**
	 * 
	 * @return
	 */
	public int getRobotRow() {
		return robotRow;
	}
  
	public int getRobotCol() {
		return robotCol;
	}
	
	public DIRECTION getRobotDir() {
		return robotDir;
	}
	
	// Setter
	/**
	 * 
	 * @param robotRow
	 */
	public void setRobotRow(int newRow) {
		robotRow = newRow;
	}
	
	/**
	 * 
	 * @param robotCol
	 */
	public void setRobotCol(int newCol) {
		robotCol = newCol;
	}
	
	/**
	 * 
	 * @param newDir
	 */
	public void setRobotDir(DIRECTION newDir) {
		robotDir = newDir;
	}
	// Other Functions
	/**
	 * 
	 * @param m
	 * @param sendToAndroid
	 */
	
	public boolean isRealBot(){
		return this.realBot;
	}
	

	public void move(MOVEMENT m, boolean sendToAndroid) {
         if (!realBot) {
            // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        } 

        switch (m) {
            case FORWARD:
                switch (robotDir) {
                    case UP:
                        robotRow++;
                        break;
                    case RIGHT:
                        robotCol++;
                        break;
                    case DOWN:
                        robotRow--;
                        break;
                    case LEFT:
                        robotCol--;
                        break;
                }
                break;
            case BACKWARD:
                switch (robotDir) {
                    case UP:
                        robotRow--;
                        break;
                    case RIGHT:
                        robotCol--;
                        break;
                    case DOWN:
                        robotRow++;
                        break;
                    case LEFT:
                        robotCol++;
                        break;
                }
                break;
            case TURNRIGHT:
            case TURNLEFT:
                robotDir = updateTurnDirection(m);
                break;
            case CALIBRATE:
                break;  
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }

        if (realBot) sendMovement(m, sendToAndroid);
        else System.out.println("Move: " + MOVEMENT.print(m));

        updateTouchedGoal();
    }
	public void move(MOVEMENT m){
		this.move(m,true);
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	private DIRECTION updateTurnDirection(MOVEMENT m){
		if(m == MOVEMENT.TURNLEFT) {
			return DIRECTION.getRight(robotDir);
		}
		else {
			return DIRECTION.getRight(robotDir);
		}
	}
	
    /**
     * Uses the current direction of the robot and the given movement to find the new direction of the robot.
     */
    private DIRECTION findNewDirection(MOVEMENT m) {
        if (m == MOVEMENT.TURNRIGHT) {
            return DIRECTION.getRight(robotDir);
        } else {
            return DIRECTION.getLeft(robotDir);
        }
    }

    /**
     * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
     *
     * @return [SRFrontLeft, SRFrontCenter, SRFrontRight, SRLeft, SRRight, LRLeft]
     */
    public int[] sense(Map explorationMap, Map realMap) {
        int[] result = new int[6];

        if (!realBot) {
            result[0] = SRFrontLeft.sense(explorationMap, realMap);
            result[1] = SRFrontCenter.sense(explorationMap, realMap);
            result[2] = SRFrontRight.sense(explorationMap, realMap);
            result[3] = SRLeft.sense(explorationMap, realMap);
            result[4] = SRRight.sense(explorationMap, realMap);
            result[5] = LRLeft.sense(explorationMap, realMap);
        } else {
            Comms robotComm = robotComm.getCommMgr();
            String msg = robotComm.recvMsg();
            String[] msgArr = msg.split(";");

            if (msgArr[0].equals(robotComm.SENSOR_DATA)) {
                result[0] = Integer.parseInt(msgArr[1].split("_")[1]);
                result[1] = Integer.parseInt(msgArr[2].split("_")[1]);
                result[2] = Integer.parseInt(msgArr[3].split("_")[1]);
                result[3] = Integer.parseInt(msgArr[4].split("_")[1]);
                result[4] = Integer.parseInt(msgArr[5].split("_")[1]);
                result[5] = Integer.parseInt(msgArr[6].split("_")[1]);
            }

            SRFrontLeft.senseReal(explorationMap, result[0]);
            SRFrontCenter.senseReal(explorationMap, result[1]);
            SRFrontRight.senseReal(explorationMap, result[2]);
            SRLeft.senseReal(explorationMap, result[3]);
            SRRight.senseReal(explorationMap, result[4]);
            LRLeft.senseReal(explorationMap, result[5]);

            String[] mapStrings = MapDescriptor.generateMapDescriptor(explorationMap);
            robotComm.sendMsg(mapStrings[0] + " " + mapStrings[1], robotComm.MAP_STRINGS);
        }

        return result;
    }
	
    public void setSensors() {
        switch (robotDir) {
            case UP:
                SRFrontLeft.setSensor(this.robotRow + 1, this.robotCol - 1, this.robotDir);
                SRFrontCenter.setSensor(this.robotRow + 1, this.robotCol, this.robotDir);
                SRFrontRight.setSensor(this.robotRow + 1, this.robotCol + 1, this.robotDir);
                SRLeft.setSensor(this.robotRow + 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT));
                LRLeft.setSensor(this.robotRow, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT));
                SRRight.setSensor(this.robotRow + 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNRIGHT));
                break;
            case RIGHT:
                SRFrontLeft.setSensor(this.robotRow + 1, this.robotCol + 1, this.robotDir);
                SRFrontCenter.setSensor(this.robotRow, this.robotCol + 1, this.robotDir);
                SRFrontRight.setSensor(this.robotRow - 1, this.robotCol + 1, this.robotDir);
                SRLeft.setSensor(this.robotRow + 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNLEFT));
                LRLeft.setSensor(this.robotRow + 1, this.robotCol, findNewDirection(MOVEMENT.TURNLEFT));
                SRRight.setSensor(this.robotRow - 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNRIGHT));
                break;
            case DOWN:
                SRFrontLeft.setSensor(this.robotRow - 1, this.robotCol + 1, this.robotDir);
                SRFrontCenter.setSensor(this.robotRow - 1, this.robotCol, this.robotDir);
                SRFrontRight.setSensor(this.robotRow - 1, this.robotCol - 1, this.robotDir);
                SRLeft.setSensor(this.robotRow - 1, this.robotCol + 1, findNewDirection(MOVEMENT.TURNLEFT));
                LRLeft.setSensor(this.robotRow, this.robotCol + 1, findNewDirection(MOVEMENT.TURNLEFT));
                SRRight.setSensor(this.robotRow - 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNRIGHT));
                break;
            case LEFT:
                SRFrontLeft.setSensor(this.robotRow - 1, this.robotCol - 1, this.robotDir);
                SRFrontCenter.setSensor(this.robotRow, this.robotCol - 1, this.robotDir);
                SRFrontRight.setSensor(this.robotRow + 1, this.robotCol - 1, this.robotDir);
                SRLeft.setSensor(this.robotRow - 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNLEFT));
                LRLeft.setSensor(this.robotRow - 1, this.robotCol, findNewDirection(MOVEMENT.TURNLEFT));
                SRRight.setSensor(this.robotRow + 1, this.robotCol - 1, findNewDirection(MOVEMENT.TURNRIGHT));
                break;
        }

    }
    private void sendMovement(MOVEMENT m, boolean sendMovetoAndroid){
    	Comms.sendMsg(MOVEMENT.print(m)+""+Comms.INSTRUCTIONS);
    	
    	
    }
    
}
