package robot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import map.Map;
import map.MapConstant;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import robot.SensorConstant.SENSORTYPE;
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
	 * 		 	^S1     ^S2     ^S3
	 * 	<S4 L5>	 X   |	 X	 |	 X
	 * 			 -	 +	 -	 +	 -
	 * 			 X   |	 X	 |	 X
	 * 			 -	 +	 -	 +	 -
	 * 	<S5		 X   |	 X	 |	 X
	 * 
	 * Legend:
	 * 		S = Short-Range
	 * 		L = Long-Range
	 */

	private final Sensor SRFrontLeft;		// 1
	private final Sensor SRFrontCenter;		// 2
	private final Sensor SRFrontRight;		// 3
	private final Sensor SRLeft;			// 4
	private final Sensor SRLeftBack;		// 5
	private final Sensor LRRight;			// 6

	// Constructor(s)
	/**
	 * Initialise robot at [18 (y), 1 (x)], face up, at normal speed
	 * @param isReal If set to true, robot assumes physically integrated functions, pure virtual simulation otherwise
	 */
	public Robot (boolean isReal) {
		robotRow = RobotConstant.DEFAULT_START_ROW;
		robotCol = RobotConstant.DEFAULT_START_COL;
		robotDir = RobotConstant.DEFAULT_START_DIR;
		speed = RobotConstant.SPEED;
		realBot = isReal;

		SRFrontLeft = new Sensor(robotRow-1, robotCol-1, robotDir, SENSORTYPE.SHORT, 1);
		SRFrontCenter = new Sensor(robotRow-1, robotCol, robotDir, SENSORTYPE.SHORT, 2);
		SRFrontRight = new Sensor(robotRow-1, robotCol+1, robotDir, SENSORTYPE.SHORT, 3);
		SRLeft = new Sensor(robotRow-1, robotCol-1, DIRECTION.LEFT, SENSORTYPE.SHORT, 4);
		SRLeftBack = new Sensor(robotRow+1, robotCol-1, DIRECTION.LEFT, SENSORTYPE.SHORT, 5);
		LRRight = new Sensor(robotRow-1, robotCol-1, DIRECTION.RIGHT, SENSORTYPE.LONG, 6);
	}

	/**
	 * Initialise robot at intended [robotRow, robotCol], face intended direction, at intended speed
	 * @param isReal If set to true, robot assumes physically integrated functions, pure virtual simulation otherwise
	 * @param startRow Centre component row (y) coordinate 
	 * @param startCol Centre component col (x) coordinate
	 * @param startDir Direction robot faces
	 * @param setSpeed Robot's speed
	 */
	public Robot(boolean isReal, Integer startRow, Integer startCol, DIRECTION startDir, Integer setSpeed) {
		if (startRow != null) robotRow = startRow;
		else robotRow = RobotConstant.DEFAULT_START_ROW;
		if (startCol != null) robotCol = startCol;
		else robotCol = RobotConstant.DEFAULT_START_COL;
		if (startDir != null) robotDir = startDir;
		else robotDir = RobotConstant.DEFAULT_START_DIR;
		if (setSpeed != null) speed = setSpeed;
		else speed = RobotConstant.SPEED;
		realBot = isReal;

		SRFrontLeft = new Sensor(robotRow-1, robotCol-1, robotDir, SENSORTYPE.SHORT, 1);
		SRFrontCenter = new Sensor(robotRow-1, robotCol, robotDir, SENSORTYPE.SHORT, 2);
		SRFrontRight = new Sensor(robotRow-1, robotCol+1, robotDir, SENSORTYPE.SHORT, 3);
		SRLeft = new Sensor(robotRow-1, robotCol-1, findTurnDirection(MOVEMENT.TURNLEFT), SENSORTYPE.SHORT, 4);
		SRLeftBack = new Sensor(robotRow-1, robotCol+1, findTurnDirection(MOVEMENT.TURNRIGHT), SENSORTYPE.SHORT, 5);
		LRRight = new Sensor(robotRow-1, robotCol-1, findTurnDirection(MOVEMENT.TURNRIGHT), SENSORTYPE.LONG, 6);
	} 


	// Getter(s)
	/**
	 * Returns centre component row (y) coordinate
	 * @return centre component row coordinate
	 */
	public int getRow() {
		return robotRow;
	}

	/**
	 * Returns centre component col (x) coordinate
	 * @return centre component col coordinate
	 */
	public int getCol() {
		return robotCol;
	}

	/**
	 * Returns direction robot faces
	 * @return direction robot faces
	 */
	public DIRECTION getDir() {
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
	public int getSpeed() {
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
	public String move(MOVEMENT m, boolean sendArduino, boolean sendAndroid) {

		String msg = "I should only be seen in Simulator mode";
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
				System.out.println("Sending " + m.toString());
				msg = sendInstruction(m, sendAndroid);
			}
		// System.out.println("Move: " + MOVEMENT.print(m));

		return msg;
	}

	/**
	 * Transmits movement command to Arduino module and optionally, to Android module (physically integrated function)
	 * @param m Intended robot's movement function
	 * @param sendAndroidBool If set to true, movement command is transmitted to Android module
	 */
	
	private String sendInstruction(MOVEMENT m, boolean sendAndroidBool) {
		// Initialise Timer
		String msg = "Invalid";
		Timer timer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Comms.sendMsg(Comms.ar, Comms.arIns, MOVEMENT.print(m));
			}
		};
		
		// Timer Execution
		if (m != MOVEMENT.CALIBRATE) {
			timer.scheduleAtFixedRate(task, 0, 3500);
			do {
				msg = Comms.receiveMsg().toLowerCase();
			} while (!msg.contains("sdat1"));
			timer.cancel();
		} else {
			timer.scheduleAtFixedRate(task, 0, 6000);
			do {
				msg = Comms.receiveMsg().toLowerCase();
			} while (!msg.contains("sdat2"));
			timer.cancel();
		}
		
		
		
		if (sendAndroidBool && m != MOVEMENT.CALIBRATE) {
			Comms.sendMsg(Comms.an, Comms.anPos, Comms.encodeCoor(MapDescriptor.getMDFcol(robotCol),MapDescriptor.getMDFrow(robotRow),DIRECTION.toInt(robotDir)));
			Comms.sleepWait();
		}

		return msg;
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
	public DIRECTION findTurnDirection(MOVEMENT m) {
		if (m == MOVEMENT.TURNLEFT) {
			return DIRECTION.getLeft(robotDir);
		}
		else {
			return DIRECTION.getRight(robotDir);
		}
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
		} else {
			System.out.println("alignment error");
		}
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
		SRLeftBack.sense(mapExplore, mapActual);
		LRRight.sense(mapExplore, mapActual);
	}

	/**
	 * Execute sensor function and send MDF strings to Android (physically integrated function)
	 * @param mapExplore Map used to track visited tiles and respective types of physical environment
	 */
	public void multiSense(Map mapExplore, String msg) {
		int[] result = new int[6];

		String[] msgArr = msg.split(";");
		msgArr = msgArr[2].split("_");

		result[0] = rounding(SENSORTYPE.SHORT, msgArr[0]);
		result[1] = rounding(SENSORTYPE.SHORT, msgArr[1]);
		result[2] = rounding(SENSORTYPE.SHORT, msgArr[2]);
		result[3] = rounding(SENSORTYPE.SHORT, msgArr[3]);
		result[4] = rounding(SENSORTYPE.SHORT, msgArr[4]);
		result[5] = rounding(SENSORTYPE.LONG, msgArr[5]);

		System.out.print("FL: " + result[0] + 
				", FM: " + result[1] + 
				", FR: " + result[2] + 
				", LF: " + result[3] + 
				", LB: " + result[4] + 
				", RL: " + result[5] + 
				"\n");

		// update map explore
		SRFrontLeft.sense(mapExplore, result[0]);
		SRFrontCenter.sense(mapExplore, result[1]);
		SRFrontRight.sense(mapExplore, result[2]);
		SRLeft.sense(mapExplore, result[3]);
		SRLeftBack.sense(mapExplore, result[4]);
		LRRight.sense(mapExplore, result[5]);

		// send MDF strings
		StringBuilder sb = new StringBuilder();
		// MDF1
		sb.append("1:");
		sb.append(MapDescriptor.generateMDFHex1(mapExplore));
		sb.append("/");
		Comms.sendMsg(Comms.an, Comms.anMdf, sb.toString());
		Comms.sleepWait();
		sb.setLength(0);
		// MDF2
		sb.append("2:");
		sb.append(MapDescriptor.generateMDFHex2(mapExplore));
		sb.append("/");
		Comms.sendMsg(Comms.an, Comms.anMdf, sb.toString());
		Comms.sleepWait();
		sb.setLength(0);
	}

	// Legacy Sampling Code
	/*
	private double[] sampling(int numSample) {
		String sample; String[] sampleArr;
		List<String[]> samples = new ArrayList<String[]>();
		for (int i = 0; i < numSample; i++) {
			Comms.sendMsg(Comms.ar, Comms.arIns, Comms.arSense);
			sample = Comms.getArdReceipt(Comms.arData);
			// sample = sample.replace("/", "");
			sampleArr = sample.split(";");
			if (sampleArr[1].equals(Comms.arData)) samples.add(sampleArr[2].split("_"));

			// to vary samples taken
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		double[] max = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		double tmp;
		for (int i = 0; i < 6; i++)
			for (String[] arr : samples) {
				tmp = Double.parseDouble(arr[i]);
				if (tmp > max[i]) max[i] = tmp;
			}

		return max;
	}
	*/

	/**
	 * Returns the number of 10cm squares sensor seen
	 * @param type Sensor type
	 * @param num Sensor's string value
	 * @return number of 10cm squares seen
	 */
	private int rounding(SENSORTYPE type, String str) {
		double num = Double.parseDouble(str);
		
		if (type == SENSORTYPE.SHORT) {
			if (num >= 16.0) { 
				return 2; 
			} else if (num >= 11.5) { 
				return 1; 
			}
		} else {
			// 17 (value [return2])
			if (num >= 11) {
				return 1;
			}
		}
		
		return 0;
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
				SRLeftBack.setSensor(robotRow+1, robotCol-1, DIRECTION.LEFT);
				LRRight.setSensor(robotRow-1, robotCol-1, DIRECTION.RIGHT);
				break;
			case DOWN:
				SRFrontLeft.setSensor(robotRow+1, robotCol+1, robotDir);
				SRFrontCenter.setSensor(robotRow+1, robotCol, robotDir);
				SRFrontRight.setSensor(robotRow+1, robotCol-1, robotDir);
				SRLeft.setSensor(robotRow+1, robotCol+1, DIRECTION.RIGHT);
				SRLeftBack.setSensor(robotRow-1, robotCol+1, DIRECTION.RIGHT);
				LRRight.setSensor(robotRow+1, robotCol+1, DIRECTION.LEFT);
				break;
			case LEFT:
				SRFrontLeft.setSensor(robotRow+1, robotCol-1, robotDir);
				SRFrontCenter.setSensor(robotRow, robotCol-1, robotDir);
				SRFrontRight.setSensor(robotRow-1, robotCol-1, robotDir);
				SRLeft.setSensor(robotRow+1, robotCol-1, DIRECTION.DOWN);
				SRLeftBack.setSensor(robotRow+1, robotCol+1, DIRECTION.DOWN);
				LRRight.setSensor(robotRow+1, robotCol-1, DIRECTION.UP);
				break;
			default:
				SRFrontLeft.setSensor(robotRow-1, robotCol+1, robotDir);
				SRFrontCenter.setSensor(robotRow, robotCol+1, robotDir);
				SRFrontRight.setSensor(robotRow+1, robotCol+1, robotDir);
				SRLeft.setSensor(robotRow-1, robotCol+1, DIRECTION.UP);
				SRLeftBack.setSensor(robotRow-1, robotCol-1, DIRECTION.UP);
				LRRight.setSensor(robotRow-1, robotCol+1, DIRECTION.DOWN);
				break;
		}
	}
	
}
