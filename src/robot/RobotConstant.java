package robot;

import java.awt.Color;

import utility.Comms;

public final class RobotConstant {
	//
	public static final int SPEED = 100;		// delay between movements (ms)

	// Robot size
	public static final int ROBOT_SIZE = 3;

	// G values used for A* algorithm
	public static final int MOVE_COST = 1;
	public static final int TURN_COST = 20;
	public static final int INFINITE_COST = 9999;

	public enum MOVEMENT {
		FORWARD, BACKWARD, TURNLEFT, TURNRIGHT, CALIBRATE, ERROR;
		public static String print(MOVEMENT m) {
			switch (m) {
			case FORWARD:
				return "F";
			case BACKWARD:
				return "B";
			case TURNRIGHT:
				return "R";
			case TURNLEFT:
				return "L";
			case CALIBRATE:
				return Comms.arCal;
			default:
				return "E";
			}
		}

		public static MOVEMENT get(char c){
			switch(c){
			case 'F':
				return FORWARD;
			case 'B':
				return BACKWARD;
			case 'R':
				return TURNRIGHT;
			case 'L':
				return TURNLEFT;
			case Comms.charCal:
				return CALIBRATE;
			default:
				return ERROR;
			}
		}
	}

	public enum DIRECTION {
		UP, LEFT, DOWN, RIGHT;

		//clockwise
		public static DIRECTION getLeft(DIRECTION currDirection) {
			return values()[(currDirection.ordinal() + 1) % values().length];
		}

		//anti-clockwise
		public static DIRECTION getRight(DIRECTION currDirection) {
			return values()[(currDirection.ordinal() + values().length - 1)% values().length];
		}

		/**
		 * Use at your own discretion
		 * 
		 * @param direction The direction to be converted into an enum
		 * @return Enum representing specified direction
		 */
		public static DIRECTION fromString(String direction) {
			return valueOf(direction.toUpperCase());
		}
		public static int toInt(DIRECTION d){
			return d.ordinal()+1;
		}

		public static DIRECTION fromInt(int i){
			switch(i){
			case 1:
				return UP;
			case 2:
				return LEFT;
			case 3:
				return DOWN;
			case 4:
			default:
				return RIGHT;
			}
		}
	};

	// Colors for rendering the map
	public static final Color C_BORDER = Color.BLACK;

	public static final Color C_LINE = Color.ORANGE;
	public static final int LINE_WEIGHT = 2;

	public static final Color C_START = Color.BLUE;
	public static final Color C_GOAL = Color.GREEN;

	public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
	public static final Color C_FREE = Color.WHITE;
	public static final Color C_OBSTACLE = Color.BLACK;

	// For rendering the robot in the robot map
	public static final Color C_ROBOT_OUTLINE = new Color(0, 0, 0, 220);
	public static final Color C_ROBOT = new Color(0, 205, 255, 160);
	public static final Color C_ROBOT_FRONT = new Color(0, 46, 155, 220);

	// For rendering the robot path in the robot map
	public static final Color C_EXPLORE_PATH = Color.RED;
	public static final Color C_SHORTEST_PATH = Color.ORANGE;
	public static final int PATH_THICKNESS = 4;

	public static final Color C_SENSOR = Color.DARK_GRAY;
	public static final Color C_SENSOR_BEAM_OUTER = new Color(220, 0, 0, 160);
	public static final Color C_SENSOR_BEAM_INNER = new Color(255, 0, 0, 190);

	// Robot Default Configuration
	public static final int DEFAULT_START_ROW = 18; // Changed to 1 based on ROBOT_SIZE
	public static final int DEFAULT_START_COL = 1;
	public static final int DEFAULT_GOAL_ROW = 1; 
	public static final int DEFAULT_GOAL_COL = 13;
	public static final DIRECTION DEFAULT_START_DIR = DIRECTION.UP;
	public static final DIRECTION DEFAULT_START_SP_DIR = DIRECTION.UP;

	// Robot Exploration Configuration
	public static final int DEFAULT_STEPS_PER_SECOND = 25;
	public static final int DEFAULT_COVERAGE_LIMIT = 50;
	public static final int DEFAULT_TIME_LIMIT = 360;

	/**
	 * 
	 * @return
	 */
	public static boolean isAtStart(int row, int col) {
		return row == DEFAULT_START_ROW && col == DEFAULT_START_COL;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isAtGoal(int row, int col) {
		return row == DEFAULT_GOAL_ROW && col == DEFAULT_GOAL_COL;
	}

	// Prevent instantiation
	private RobotConstant() {}

}