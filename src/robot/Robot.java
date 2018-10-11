package robot;

import java.util.concurrent.TimeUnit;

import map.Map;
import map.MapConstant;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.Comms;
import utility.MapDescriptor;

/**
 * @author 18/19 S1 G3
 */
public class Robot {

	// Variables
	private int robotRow, robotCol; // centre component coordinate
	private DIRECTION robotDir; // direction robot face
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
	 */

	private final Sensor SRFrontLeft;		// S2
	private final Sensor SRFrontCenter;		// S3
	private final Sensor SRFrontRight;		// S4
	private final Sensor SRLeft;			// S1
	private final Sensor SRRight;			// S5
	private final Sensor LRRight;			// L1

	// Constructor(s)
	/**
	 * Initialise robot at [robotRow, robotCol], face up
	 * @param startRow Centre component row (y) coordinate 
	 * @param startCol Centre component col (x) coordinate
	 * @param isRealRobot If set to true, robot assumes physically integrated functions, pure virtual simulation otherwise
	 */
	public Robot (int startRow, int startCol, boolean isRealRobot) {
		robotRow = startRow;
		robotCol = startCol;
		robotDir = RobotConstant.DEFAULT_START_DIR;
		speed = RobotConstant.SPEED;
		realBot = isRealRobot;

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
	 * Initialise robot at [robotRow, robotCol], face up
	 * @param startRow Centre component row (y) coordinate 
	 * @param startCol Centre component col (x) coordinate
	 * @param startDir Direction robot faces
	 * @param isReal If set to true, robot assumes physically integrated functions, pure virtual simulation otherwise
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
		SRLeft = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol-1, findTurnDirection(MOVEMENT.TURNLEFT), "S1");
		SRRight = new Sensor(srLowerLimit, srUpperLimit, robotRow-1, robotCol+1, findTurnDirection(MOVEMENT.TURNRIGHT), "S5");
		LRRight = new Sensor(lrLowerLimit, lrUpperLimit, robotRow, robotCol+1, findTurnDirection(MOVEMENT.TURNRIGHT), "L1");
	} 


	// Getter(s)
	/**
	 * Returns centre component row (y) coordinate
	 * @return centre component row coordinate
	 */
	public int getRobotRow() {
		return robotRow;
	}

	/**
	 * Returns centre component col (x) coordinate
	 * @return centre component col coordinate
	 */
	public int getRobotCol() {
		return robotCol;
	}

	/**
	 * Returns direction robot faces
	 * @return direction robot faces
	 */
	public DIRECTION getRobotDir() {
		return robotDir;
	}

	/**
	 * Returns true if robot is initialised with physically integrated functions
	 * @return true if robot is initialised with physically integrated functions, false otherwise
	 */
	public boolean isRealBot() {
		return realBot;
	}

	/**
	 * Returns robot's speed
	 * @return robot's speed
	 */
	public int getRobotSpeed() {
		return speed;
	}

	/**
	 * Returns true if robot is at [row (y), col (y)]
	 * @param row Centre component row (y) coordinate 
	 * @param col Centre component col (x) coordinate
	 * @return true if robot is at [row, col], false otherwise
	 */
	public boolean isAtPos(int row, int col) {
		return (robotRow == row && robotCol == col);
	}


	// Setter
	/**
	 * Set centre component's row (y) coordinate
	 * @param newRow Intended centre component row coordinate
	 */
	public void setRobotRow(int newRow) {
		robotRow = newRow;
	}

	/**
	 * Set centre component's col (x) coordinate
	 * @param robotCol Intended centre component col coordinate
	 */
	public void setRobotCol(int newCol) {
		robotCol = newCol;
	}

	/**
	 * Set direction robot faces
	 * @param newDir Intended direction robot faces
	 */
	public void setRobotDir(DIRECTION newDir) {
		robotDir = newDir;
	}

	/**
	 * Set robot speed
	 * @param newSpeed Intended robot speed
	 */
	public void setRobotSpeed (int newSpeed) {
		speed = newSpeed;
	}

	/**
	 * Set centre component's coordinates at [row (y), col (x)]
	 * @param newRow Intended centre component row coordinate 
	 * @param newCol Intended centre component col coordinate
	 */
	public void setBotPos(int newRow, int newCol) {
		robotRow = newRow;
		robotCol = newCol;
	}


	// Other Function(s)
	/**
	 * Execute robot's move functions
	 * @param m Intended robot's movement
	 * @param sendArduino If set to true, move command is transmitted to Arduino module (physically integrated function)
	 * @param sendAndroid If set to true, move command is transmitted to Android module (physically integrated function)
	 */
	private void move(MOVEMENT m, boolean sendArduino, boolean sendAndroid) {

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
			robotDir = findTurnDirection(m);
			break;
		case CALIBRATE:
			break;  
		default:
			System.out.println("Error in Robot.move()!");
			break;
		}

		if (realBot)
			if (sendArduino) {
				System.out.println("Sending "+ m.toString());
				sendInstruction(m, sendAndroid);
			}

		System.out.println("Move: " + MOVEMENT.print(m));

	}

	/**
	 * Execute robot's move functions
	 * @param m Intended robot's movement
	 */
	public void move(MOVEMENT m) {
		move(m, true, true);
	}

	/**
	 * Execute robot's move functions on simulator only
	 * @param m Intended robot's movement
	 */
	public void moveDigital(MOVEMENT m) {
		move(m, false, false);
	}

	/**
	 * Execute robot's turn function
	 * @param m Intended robot's turn type (either TURNLEFT or TURNRIGHT)
	 * @return Robot's new direction after turn function execution
	 */
	private DIRECTION findTurnDirection(MOVEMENT m) {
		if (m == MOVEMENT.TURNLEFT) return DIRECTION.getLeft(robotDir);
		else return DIRECTION.getRight(robotDir);
	}

	/**
	 * 
	 */
	public void reAlign() {
		if (robotRow == MapConstant.START_GRID_ROW && robotCol == MapConstant.START_GRID_COL) {
			// send start pos to android
			int dirInt = DIRECTION.toInt(robotDir);
			System.out.println(Comms.encodeCoor(robotRow, robotCol,dirInt));
			// Comms.sendMsg(Comms.ANDROID, Comms.POS, Comms.encodeCoor(robotRow, robotCol,dirInt));
		} else System.out.println("alignment error");
	}

	/**
	 * Execute simulated sensor functions
	 * @param mapExplore Map used to track visited tiles and respective types of reference map
	 * @param mapActual Reference map, substitute of physical environment
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
	 * Execute sensor function (physically integrated function)
	 * @param mapExplore Map used to track visited tiles and respective types of physical environment
	 */
	public void multiSense(Map mapExplore) {
		int[] result = new int[6];
		StringBuilder sb = new StringBuilder();
		String str; String[] strArr;

		// Request Sensor Data in P;SDATA;<>_<>_<>_<>_<>_<>
		Comms.sendMsg(Comms.ARDUINO, Comms.INS, Comms.SENSE);

		// Process Sensor Data
		str = Comms.getArdReceipt(Comms.SENSOR_DATA);
		strArr = str.split(";");

		if (strArr[0].equals(Comms.SENSOR_DATA)) {
			str = strArr[1];
			result[0] = (int) rounding(str.split("_")[0]);
			result[1] = (int) rounding(str.split("_")[1]);
			result[2] = (int) rounding(str.split("_")[2]);
			result[3] = (int) rounding(str.split("_")[3]);
			result[4] = (int) rounding(str.split("_")[4]);
			result[5] = (int) rounding(str.split("_")[5]);
		}

		// Use Sensor Values and update mapExplore
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
		Comms.getAndReceipt(Comms.DONE);
		sb.setLength(0);

		// Send MDF2
		sb.append("2:");
		sb.append(MapDescriptor.generateMDFHex2(mapExplore));
		sb.append("/");
		Comms.sendMsg(Comms.ANDROID, Comms.MAP, sb.toString());
		Comms.getAndReceipt(Comms.DONE);
		sb.setLength(0);
	}

	/**
	 * Returns a rounded up or down integer used in determining the presence of an obstacle
	 * @param value String value from sensor
	 * @return a rounded up or down integer
	 */
	private int rounding(String value) {
		double num = Double.parseDouble(value);
		System.out.println("V: " + (Math.round(num / 10.0)));
		return (int) (Math.round(num / 10.0));
	}

	/**
	 * Update sensor's coordinates when a robot executes a turn (either TURNLEFT or TURNRIGHT) function
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
	 * Transmits movement command to Arduino module and optionally, to Android module (physically integrated function)
	 * @param m Intended robot's movement function
	 * @param sendAndroidBool If set to true, movement command is transmitted to Android module
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
			System.err.println("Error sending instruction");
		}

	}

}
