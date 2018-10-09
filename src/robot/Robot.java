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

	// Robot
	private int robotRow; // Coordinate of centre component
	private int robotCol; // Coordinate of centre component
	private DIRECTION robotDir;
	private boolean realBot;
	private int speed;

	// Sensor(s)
	/*
	 * 		  ^       ^      ^
	 * 		  S2      S3     S4
	 * 	< S1  X   |   X  |   X  S5 >
	 * 		  -   +   -  +   -
	 *		  X   |   X  |   X  L1 > 
	 * 		  -   +   -  +   -
	 * 		  X   |   X  |   X 
	 * 
	 * (Legend) 
	 *  S = Short-Range
	 * 	L = Long-Range
	 *  U = Ultra-Range
	 */

	private final Sensor SRFrontLeft;		// S2
	private final Sensor SRFrontCenter;		// S3
	private final Sensor SRFrontRight;		// S4
	private final Sensor SRLeft;			// S1
	private final Sensor SRRight;			// S5
	private final Sensor LRRight;			// L1

	// Constructor(s)
	/**
	 * Instantiate a robot 'placed' at coordinate [col(y),row(x)]
	 * facing up (default), with 6 sensors
	 * @param startRow Centre row(y)-coordinate of Robot
	 * @param startCol Centre col(x)-coordinate of Robot
	 * @param isReal
	 */
	public Robot (int startRow, int startCol, boolean isReal){
		robotRow = startRow;
		robotCol = startCol;
		robotDir = RobotConstant.DEFAULT_START_DIR;
		speed = RobotConstant.SPEED;
		realBot = isReal;

		int srLowerLimit = SensorConstant.SR_LOWER;
		int srUpperLimit = SensorConstant.SR_UPPER;
		int lrLowerLimit = SensorConstant.LR_LOWER;
		int lrUpperLimit = SensorConstant.LR_UPPER;

		SRFrontLeft = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol-1, robotDir, "S2");
		SRFrontCenter = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol, robotDir, "S3");
		SRFrontRight = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol+1, robotDir, "S4");
		SRLeft = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol-1, DIRECTION.LEFT, "S1");
		SRRight = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol+1, DIRECTION.RIGHT, "S5");
		LRRight = new Sensor(lrLowerLimit, lrUpperLimit, robotRow, robotCol+1, DIRECTION.RIGHT, "L1");
	}


	/**
	 * Instantiate a robot 'placed' at coordinate [row(y),col(x)]
	 * facing in set direction, with an blank map
	 * @param startRow Centre row(y)-coordinate of Robot
	 * @param startCol Centre col(x)-coordinate of Robot
	 * @param isReal
	 * @param direction Direction Robot's facing
	 */
	public Robot(int startRow, int startCol, DIRECTION startDir, boolean isReal) {
		robotRow = startRow;
		robotCol = startCol;
		robotDir = startDir;
		speed = RobotConstant.SPEED;
		realBot = isReal;

		int srLowerLimit = SensorConstant.SR_LOWER;
		int srUpperLimit = SensorConstant.SR_UPPER;
		int lrLowerLimit = SensorConstant.LR_LOWER;
		int lrUpperLimit = SensorConstant.LR_UPPER;

		SRFrontLeft = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol-1, robotDir, "S2");
		SRFrontCenter = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol, robotDir, "S3");
		SRFrontRight = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol+1, robotDir, "S4");
		SRLeft = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol-1, findNewDirection(MOVEMENT.TURNLEFT), "S1");
		SRRight = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol+1, findNewDirection(MOVEMENT.TURNRIGHT), "S5");
		LRRight = new Sensor(lrLowerLimit, lrUpperLimit, robotRow, robotCol+1, findNewDirection(MOVEMENT.TURNRIGHT), "L1");
	} 


	// Getter(s)
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isAtPos(int row, int col){
		return (robotRow == row && robotCol == col);
	}

	/**
	 * 
	 * @return
	 */
	public int getRobotRow() {
		return robotRow;
	}

	/**
	 * 
	 * @return
	 */
	public int getRobotCol() {
		return robotCol;
	}

	/**
	 * 
	 * @return
	 */
	public DIRECTION getRobotDir() {
		return robotDir;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRealBot() {
		return realBot;
	}

	/**
	 * 
	 * @return
	 */
	public int getRobotSpeed() {
		return speed;
	}


	// Setter
	public void setBotPos(int row, int col){
		robotRow = row;
		robotCol = col;
	}
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

	/**
	 * 
	 * @param robotSpeed
	 */
	public void setRobotSpeed (int robotSpeed) {
		speed = robotSpeed;
	}


	// Other Function(s)
	/**
	 * 
	 * @param m
	 * @param sendToAndroid
	 */
	public void move(MOVEMENT m, boolean sendToAndroid) {

		// Simulate Real-Time Movement
		if (!realBot) 
			try { TimeUnit.MILLISECONDS.sleep(speed); } 
		catch ( InterruptedException e) { System.out.println("Something went wrong in Robot.move()!"); }

		switch (m) {
		case FORWARD:
			switch (robotDir) {
			case UP:
				robotRow--;
				break;
			case DOWN:
				robotRow++;
				break;
			case LEFT:
				robotCol--;
				break;
			default:
				robotCol++;
				break;
			}
			break;
		case BACKWARD:
			switch (robotDir) {
			case UP:
				robotRow++;
				break;
			case DOWN:
				robotRow--;
				break;
			case LEFT:
				robotCol++;
				break;
			default:
				robotCol--;
				break;
			}
			break;
		case TURNLEFT:
		case TURNRIGHT:
			robotDir = updateTurnDirection(m);
			break;
		case CALIBRATE:
			break;  
		default:
			System.out.println("Error in Robot.move()!");
			break;
		}

		// TODO incorporate physical robot function
		if (realBot) ;// sendMovement(m, sendToAndroid);
		else System.out.println("Move: " + MOVEMENT.print(m));
	}

	/**
	 * 
	 * @param m
	 */
	public void move(MOVEMENT m){
		move(m,realBot);
	}

	/**
	 * 
	 * @param m
	 * @return
	 */
	private DIRECTION updateTurnDirection(MOVEMENT m){
		if (m == MOVEMENT.TURNLEFT) return DIRECTION.getLeft(robotDir);
		else return DIRECTION.getRight(robotDir);
	}

	/**
	 * Uses the current direction of the robot and the given movement to find the new direction of the robot
	 * @param m
	 * @return
	 */
	private DIRECTION findNewDirection(MOVEMENT m) {
		if (m == MOVEMENT.TURNRIGHT) return DIRECTION.getRight(robotDir);
		else return DIRECTION.getLeft(robotDir);
	}

	/**
	 * Calls the .sense() method of all the attached sensors and stores the received values in an integer array.
	 *
	 * @return [SRFrontLeft, SRFrontCenter, SRFrontRight, SRLeft, SRRight, LRLeft]
	 */
	public void sense(Map explorationMap, Map realMap) {
		if (!realBot) {
			// simulated robot 'sense' function
			SRFrontLeft.senseSim(explorationMap, realMap);
			SRFrontCenter.senseSim(explorationMap, realMap);
			SRFrontRight.senseSim(explorationMap, realMap);
			SRLeft.senseSim(explorationMap, realMap);
			SRRight.senseSim(explorationMap, realMap);
			LRRight.senseSim(explorationMap, realMap);
		} else {
			int[] result = new int[6];
			
			if (!Comms.connectionActive()) Comms.openSocket();
			String msg = Comms.receiveMsg();
			String[] msgArr = msg.split(";");

			if (msgArr[0].equals(Comms.SENSOR_DATA)) {
				result[0] = Integer.parseInt(msgArr[1].split("_")[1]);
				result[1] = Integer.parseInt(msgArr[2].split("_")[1]);
				result[2] = Integer.parseInt(msgArr[3].split("_")[1]);
				result[3] = Integer.parseInt(msgArr[4].split("_")[1]);
				result[4] = Integer.parseInt(msgArr[5].split("_")[1]);
				result[5] = Integer.parseInt(msgArr[6].split("_")[1]);
			}

			SRFrontLeft.sensePhys(explorationMap, result[0]);
			SRFrontCenter.sensePhys(explorationMap, result[1]);
			SRFrontRight.sensePhys(explorationMap, result[2]);
			SRLeft.sensePhys(explorationMap, result[3]);
			SRRight.sensePhys(explorationMap, result[4]);
			LRRight.sensePhys(explorationMap, result[5]);

			// TODO Generate MDF String Integration
			// String[] mapStrings = MapDescriptor.generateMapDescriptor(explorationMap);
			// Send MDF1
			 Comms.sendMsg(Comms.MAP_STRINGS, "1:MDF1/");
			// Send MDF2
			// Comms.sendMsg(Comms.MAP_STRINGS, "2:MDF2/");
		}
	}

	/**
	 * 
	 */
	public void moveSensor() {
		switch (robotDir) {
		case UP:
			SRFrontLeft.setSensor(robotRow-1, robotCol-1, robotDir);
			SRFrontCenter.setSensor(robotRow-1, robotCol, robotDir);
			SRFrontRight.setSensor(robotRow-1, robotCol+1, robotDir);
			SRLeft.setSensor(robotRow-1, robotCol-1, DIRECTION.LEFT);
			SRRight.setSensor(robotRow-1, robotCol+1, DIRECTION.RIGHT);
			LRRight.setSensor(robotRow, robotCol+1, DIRECTION.RIGHT);
			break;
		case DOWN:
			SRFrontLeft.setSensor(robotRow+1, robotCol+1, robotDir);
			SRFrontCenter.setSensor(robotRow+1, robotCol, robotDir);
			SRFrontRight.setSensor(robotRow+1, robotCol-1, robotDir);
			SRLeft.setSensor(robotRow+1, robotCol+1, DIRECTION.RIGHT);
			SRRight.setSensor(robotRow+1, robotCol-1, DIRECTION.LEFT);
			LRRight.setSensor(robotRow, robotCol-1, DIRECTION.LEFT);
			break;
		case LEFT:
			SRFrontLeft.setSensor(robotRow+1, robotCol-1, robotDir);
			SRFrontCenter.setSensor(robotRow, robotCol-1, robotDir);
			SRFrontRight.setSensor(robotRow-1, robotCol-1, robotDir);
			SRLeft.setSensor(robotRow+1, robotCol-1, DIRECTION.DOWN);
			SRRight.setSensor(robotRow-1, robotCol-1, DIRECTION.UP);
			LRRight.setSensor(robotRow-1, robotCol, DIRECTION.UP);
			break;
		default:
			SRFrontLeft.setSensor(robotRow-1, robotCol+1, robotDir);
			SRFrontCenter.setSensor(robotRow, robotCol+1, robotDir);
			SRFrontRight.setSensor(robotRow+1, robotCol+1, robotDir);
			SRLeft.setSensor(robotRow-1, robotCol+1, DIRECTION.UP);
			SRRight.setSensor(robotRow+1, robotCol+1, DIRECTION.DOWN);
			LRRight.setSensor(robotRow+1, robotCol, DIRECTION.DOWN);
			break;
		}
	}

	/**
	 * 
	 * @param m
	 * @param sendMovetoAndroid
	 */
	private void sendInstruction(MOVEMENT m, boolean sendMovetoAndroid) {
		// Comms.sendMsg(Comms.INSTRUCTIONS, );
	}

}
