package robot;

import java.util.concurrent.TimeUnit;

import map.Map;
import map.MapConstant;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.Comms;
import utility.MapDescriptor;
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

		if (realBot) {

			System.out.println("Sending "+m.toString());
			sendInstruction(m, sendToAndroid);
		}
		System.out.println("Move: " + MOVEMENT.print(m));

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

	public void reAlign(){
		if(robotRow==MapConstant.START_GRID_ROW && robotCol==MapConstant.START_GRID_COL){
			//send start pos to android
			int dirInt = DIRECTION.toInt(robotDir);
			System.out.println(Comms.encodeCoor(robotRow, robotCol,dirInt));
			//				Comms.sendMsg(Comms.ANDROID, Comms.POS, Comms.encodeCoor(robotRow, robotCol,dirInt));
		}
		else{
			System.out.println("alignment error");
		}
		return;
	}

	/**
	 * Execute simulated 'sense' function for all 6 sensors
	 * @param mapExplore
	 * @param mapActual
	 */
	public void multiSense(Map mapExplore, Map mapActual) {
		SRFrontLeft.sense(mapExplore, mapActual);
		SRFrontCenter.sense(mapExplore, mapActual);
		SRFrontRight.sense(mapExplore, mapActual);
		SRLeft.sense(mapExplore, mapActual);
		SRRight.sense(mapExplore, mapActual);
		LRRight.sense(mapExplore, mapActual);
	}

	/**
	 * Execute physical 'sense' function for all 6 sensors
	 * @param mapExplore
	 */
	public void multiSense(Map mapExplore) {
		int[] result = new int[6];
		StringBuilder sb = new StringBuilder();
		String str; String[] strArr;
		Comms.sendMsg(Comms.ARDUINO, Comms.INS, Comms.SENSE);
		str = Comms.getArdReceipt(Comms.SENSOR_DATA);
		strArr = str.split(";"); // P;SDATA;<>_<>_<>

		if (strArr[0].equals(Comms.SENSOR_DATA)) {
			str = strArr[1];
			result[0] = (int) rounding(str.split("_")[0]);
			result[1] = (int) rounding(str.split("_")[1]);
			result[2] = (int) rounding(str.split("_")[2]);
			result[3] = (int) rounding(str.split("_")[3]);
			result[4] = (int) rounding(str.split("_")[4]);
			result[5] = (int) rounding(str.split("_")[5]);
		}

		SRFrontLeft.sense(mapExplore, result[0]);
		SRFrontCenter.sense(mapExplore, result[1]);
		SRFrontRight.sense(mapExplore, result[2]);
		SRLeft.sense(mapExplore, result[3]);
		SRRight.sense(mapExplore, result[4]);
		LRRight.sense(mapExplore, result[5]);


		// Send MDF1
		sb.append("1:");
		sb.append(MapDescriptor.generateMDFHex1(mapExplore));
		sb.append("/");
		Comms.sendMsg(Comms.ANDROID, Comms.MAP, sb.toString());
		sb.setLength(0);
		Comms.getAndReceipt(Comms.DONE);

		// Send MDF2
		sb.append("2:");
		sb.append(MapDescriptor.generateMDFHex2(mapExplore));
		sb.append("/");
		Comms.sendMsg(Comms.ANDROID, Comms.MAP, sb.toString());
		sb.setLength(0);
		Comms.getAndReceipt(Comms.DONE);
	}

	private int rounding(String val) {
		double num = Double.parseDouble(val);
		System.out.println("V: " + (Math.round(num / 10.0)));
		return (int) (Math.round(num / 10.0));
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
	private void sendInstruction(MOVEMENT m, boolean sendAndroidBool) {
		try {
			Comms.sendMsg(Comms.ARDUINO, Comms.INS, MOVEMENT.print(m));
			if (m == MOVEMENT.CALIBRATE) Comms.getArdReceipt(Comms.SENSOR_DATA);
			else Comms.getArdReceipt(Comms.DONE);

			if (m != MOVEMENT.CALIBRATE && sendAndroidBool) {
				Comms.sendMsg(Comms.ANDROID, Comms.POS, Comms.encodeCoor(MapDescriptor.getMDFcol(robotCol),MapDescriptor.getMDFrow(robotRow),DIRECTION.toInt(robotDir)));
				Comms.getAndReceipt(Comms.DONE);
				return;
			}
		} catch (Exception e) {
			System.out.println("Error sending instruction");
			//e.printStackTrace();
		}

	}

}
