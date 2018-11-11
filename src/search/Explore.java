package search;

import java.util.List;
import java.util.concurrent.TimeUnit;

import map.*;
import robot.*;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;

import search.FastestPath;

public class Explore {

	// Cardinal Reference
	private MOVEMENT forward = MOVEMENT.FORWARD;
	private MOVEMENT turnLeft = MOVEMENT.TURNLEFT;
	private MOVEMENT turnRight = MOVEMENT.TURNRIGHT;
	private MOVEMENT calibrate = MOVEMENT.CALIBRATE;

	private DIRECTION up = DIRECTION.UP;
	private DIRECTION down = DIRECTION.DOWN;
	private DIRECTION left = DIRECTION.LEFT;

	// Robot Tracker
	private Robot robot;
	private boolean isReal;
	private int calibrateCount = 0;
	
	// Safety Catch
	private int leftCount = 0;

	// Map Exploration Tracker
	private int explored = 0; // explore counter
	private double coveragePercent;
	private int maxCoverage;
	private long timeStart, timeEnd, softEnd;
	private long duration;
	private Map mapExplore;
	private boolean visitedGoal = false;
	private boolean endRun = false;
	
	private String msg;

	// Simulation Tracker
	private Map mapActual;

	// Constructor
	/**
	 * Initialise Exploration routine
	 * @param bot Robot reference
	 * @param explore Tracking map reference
	 * @param actual Simulated map reference
	 * @param seconds Exploration Duration (in seconds)
	 * @param coveragePercent Minimum required coverage (in %)
	 */
	public Explore(Robot bot, Map explore, Map actual, long seconds, double coveragePercent) {
		robot = bot;
		isReal = robot.isRealBot();
		mapExplore = explore;
		mapActual = actual;
		duration = TimeUnit.SECONDS.toMillis(seconds);
		this.coveragePercent = coveragePercent;
	}

	// Getter(s)
	/**
	 * Return tracking map
	 * @return tracking map
	 */
	public Map getMap() {
		return mapExplore;
	}

	// Main Functions
	/**
	 * Calibrates robot
	 * (physically integrated)
	 */
	public void initialCalibrate() {
		if (isReal) {
			rotateRobot(down);
			calibrate();

			rotateRobot(left);
			calibrate();

			rotateRobot(up);
		}
	}
	
	/**
	 * Setup robot for exploration sequence
	 */
	public void setupExplore() {	
		timeStart = System.currentTimeMillis();
		timeEnd = timeStart + duration;
		softEnd = timeStart + TimeUnit.SECONDS.toMillis(250);

		int robotRow, robotCol;
		robotRow = robot.getRow();
		robotCol = robot.getCol();
		int[] startVal = {robotRow,robotCol};
		List<int[]> initialReveal = Map.getAdjCoor(robotRow, robotCol);
		initialReveal.add(startVal);
		for (int[] coor : initialReveal) 
			mapExplore.getTile(coor[0], coor[1]).setExplored(true);

		maxCoverage = (int) (coveragePercent / 100 * 300);
		
		if (!isReal) robot.multiSense(mapExplore, mapActual);
		updateExplore();
		// start exploration
		explore();
	}

	/**
	 * Exploration sequence
	 */
	public void explore() {
		if (System.currentTimeMillis() >= timeEnd || explored >= maxCoverage || 
				( (System.currentTimeMillis() >= softEnd) && RobotConstant.isAtStart(robot.getRow(), robot.getCol())) ) {
			endRun = true;
			return;
		} else {
			move();
			calibrateCount++;
			updateExplore();
			
			// Calibrate (w/right blind)
			/*
			boolean calibratedRight = false;
			if (canCalibrate(1, robot, mapExplore) && canCalibrate(2, robot, mapExplore)) {
				calibrate();
				if (rightNotExplored()) {
					moveRobot(turnRight, isReal, isReal);
					if (canCalibrate(1, robot, mapExplore)) {
						calibrate();
						moveRobot(turnLeft, isReal, isReal);	
						calibratedRight = true;
					}
				}
				if (!calibratedRight) {
					moveRobot(turnLeft, isReal, isReal);
					calibrate();
					moveRobot(turnRight, isReal, isReal);
				}
				calibrateCount = 0;
			} */
			
			if (canCalibrate(1, robot, mapExplore) && canCalibrate(3, robot, mapExplore)) {
				moveRobot(turnRight, isReal, isReal);
				calibrate();
				moveRobot(turnLeft, isReal, isReal);
				calibrate();
				calibrateCount = 0;
			} else if (canCalibrate(1, robot, mapExplore) && canCalibrate(2, robot, mapExplore)) {
				moveRobot(turnLeft, isReal, isReal);
				calibrate();
				moveRobot(turnRight, isReal, isReal);
				calibrate();
				calibrateCount = 0;
			}
			
			if (calibrateCount >= 5) {
				if (canCalibrate(1, robot, mapExplore)) {
					calibrate();
					calibrateCount = 0;
				} else if (canCalibrate(2, robot, mapExplore)) {
					moveRobot(turnLeft, isReal, isReal);
					calibrate();
					moveRobot(turnRight, isReal, isReal);
					calibrateCount = 0;
				} else if (canCalibrate(3, robot, mapExplore)) {
					moveRobot(turnRight, isReal, isReal);
					calibrate();
					moveRobot(turnLeft, isReal, isReal);
					calibrateCount = 0;
				}
			}
		}
	}

	/**
	 * Return to Start Zone
	 */
	public void goToStart() {
		int startRow, startCol, goalRow, goalCol;
		goalRow = MapConstant.GOAL_GRID_ROW;
		goalCol = MapConstant.GOAL_GRID_COL;
		startRow = MapConstant.START_GRID_ROW;
		startCol = MapConstant.START_GRID_COL;

		FastestPath fp = new FastestPath(mapExplore, robot);;
		String str;
		if (mapExplore.getTile(goalRow, goalCol).isExplored() && !visitedGoal) {
			str = fp.searchFastestPath(robot.getRow(),robot.getCol(),goalRow, goalCol);
			str += fp.searchFastestPath(goalRow,goalCol,startRow,startCol);
			fp.moveBotfromString(str,Simulator.returnRealRun(),true);
		} else {
			if (!robot.isAtPos(startRow, startCol)) {
				str = fp.searchFastestPath(startRow, startCol);
				fp.moveBotfromString(str,Simulator.returnRealRun(),true);
			}
		}

		System.out.println("Exploration Complete...");
		updateExplore();


		if (isReal) {
			rotateRobot(left);
			calibrate();
			rotateRobot(down);
			calibrate();
		}

		rotateRobot(up);
	}


	// Support Function
	/**
	 * Checks for valid moves robot can take
	 */
	private void move() {
		if (peekLeft() && leftCount < 4) {
			moveRobot(turnLeft, isReal, isReal);
			leftCount++;
			if (peekUp()) {
				moveRobot(forward, isReal, isReal);
			}
		} else if (peekUp()) {
			leftCount = 0;
			moveRobot(forward, isReal, isReal);
		} else if (peekRight()) {
			leftCount = 0;
			moveRobot(turnRight, isReal, isReal);
			if (peekUp()) {
				moveRobot(forward, isReal, isReal);
			}
		} else if (peekDown()) {
			leftCount = 0;
			moveRobot(turnLeft, isReal, isReal);
			moveRobot(turnLeft, isReal, isReal);
		}
	}

	/**
	 * Return true if robot's left is a valid place to move into
	 * @return true if robot's left is a valid place to move into, false otherwise
	 */
	private boolean peekLeft() {
		switch(robot.getDir()) {
			case UP:
				return isLeftFree();
			case DOWN:
				return isRightFree();
			case LEFT:
				return isDownFree();
			default:
				return isUpFree();
		}
	}

	/**
	 * Return true if robot's right is a valid place to move into
	 * @return true if robot's right is a valid place to move into, false otherwise
	 */
	private boolean peekRight() {
		switch(robot.getDir()) {
			case UP:
				return isRightFree();
			case DOWN:
				return isLeftFree();
			case LEFT:
				return isUpFree();
			default:
				return isDownFree();
		}
	}

	/**
	 * Return true if robot's back is a valid place to move into
	 * @return true if robot's back is a valid place to move into, false otherwise
	 */
	private boolean peekDown() {
		switch(robot.getDir()) {
			case UP:
				return isDownFree();
			case DOWN:
				return isUpFree();
			case LEFT:
				return isRightFree();
			default:
				return isDownFree();
		}	
	}

	/**
	 * Return true if robot's front is a valid place to move into
	 * @return true if robot's front is a valid place to move into, false otherwise
	 */
	private boolean peekUp() {
		switch(robot.getDir()) {
			case UP:
				return isUpFree();
			case DOWN:
				return isDownFree();
			case LEFT:
				return isLeftFree();
			default:
				return isRightFree();
		}
	}

	/**
	 * Return true if robot's front is a valid place to move into
	 * @return true if robot's front is a valid place to move into, false otherwise
	 */
	private boolean isUpFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y-2, x-1) && notObs(y-2, x+1) && notVir(y-1, x);
	}

	/**
	 * Return true if robot's back is a valid place to move into
	 * @return true if robot's back is a valid place to move into, false otherwise
	 */
	private boolean isDownFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+2, x-1) && notObs(y+2, x+1) && notVir(y+1, x);
	}

	/**
	 * Return true if robot's left is a valid place to move into
	 * @return true if robot's left is a valid place to move into, false otherwise
	 */
	private boolean isLeftFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+1, x-2) && notObs(y-1, x-2) && notVir(y, x-1);
	}

	/**
	 * Return true if robot's right is a valid place to move into
	 * @return true if robot's right is a valid place to move into, false otherwise
	 */
	private boolean isRightFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+1, x+2) && notObs(y-1, x+2) && notVir(y, x+1);
	}

	/**
	 * Return true if tile at [row (y), col (x)] is explored and not an obstacle
	 * @param row Tile row coordinate
	 * @param col Tile col coordinate
	 * @return true if tile is explored and not an obstacle, false otherwise
	 */
	private boolean notObs(int row, int col) {
		if (Map.isValidTile(row, col)) {
			Tile tile = mapExplore.getTile(row, col);
			return tile.isExplored() && !tile.isObstacle();
		}
		return false;
	}

	/**
	 * Return true if tile at [row (y), col (x)] is explored, not an obstacle and not a virtual wall
	 * @param row Tile row coordinate
	 * @param col Tile col coordinate
	 * @return true if tile is explored, not an obstacle and not a virtual wall, false otherwise
	 */
	private boolean notVir(int row, int col) {
		if (Map.isValidTile(row, col)) {
			Tile tile = mapExplore.getTile(row, col);
			return tile.isExplored() && !tile.isObstacle() && !tile.isVirtualWall();
		}
		return false;
	}
	
	/**
	 * Execute robot's movements
	 * @param move Direction which robot would move to
	 * @param sendArd If set to true, instruction would be transmitted to Arduino (physically integrated)
	 * @param sendAnd If set to true, instruction would be transmitted to Android (physically integrated)
	 */
	private void moveRobot(MOVEMENT move, boolean sendArd, boolean sendAnd) {
		if (robot.isRealBot()) {
			msg = robot.move(move, sendArd, sendAnd);
			robot.moveSensor();
			robot.multiSense(mapExplore, msg);
		} else {
			robot.move(move, false, false);
			robot.moveSensor();
			robot.multiSense(mapExplore, mapActual);
		}
		
		if (robot.getRow() == RobotConstant.DEFAULT_GOAL_ROW && robot.getCol() == RobotConstant.DEFAULT_GOAL_COL) {
			visitedGoal = true;
		}
		
		mapExplore.repaint();
	}

	/**
	 * Updates robot's exploration progress
	 */
	private void updateExplore() {
		int count = 0;
		for (int r = 0; r < Map.row; r++) 
			for (int c = 0; c < Map.col; c++)
				if (mapExplore.getTile(r, c).isExplored()) count++;

		explored = count; 
		System.out.println("Explored: " + count + " Cells");
		System.out.printf("%.2f%% Coverage\n", (count / (double) 300) * 100);
		System.out.println("Duration: " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - timeStart) + " seconds");
	}

	/**
	 * Rotate robot to face intended direction
	 * @param dir Intended direction robot would face
	 */
	private void rotateRobot(DIRECTION dir) {
		DIRECTION robotDir = robot.getDir();
		if (robotDir == dir) return;
		switch (robotDir) {
		case UP:
			switch (dir) {
			case DOWN:
				moveRobot(turnLeft, isReal, isReal);
				moveRobot(turnLeft, isReal, isReal);
				break;
			case LEFT:
				moveRobot(turnLeft, isReal, isReal);
				break;
			default:
				moveRobot(turnRight, isReal, isReal);
				break;
			}
			break;
		case DOWN:
			switch (dir) {
			case UP:
				moveRobot(turnLeft, isReal, isReal);
				moveRobot(turnLeft, isReal, isReal);
				break;
			case LEFT:
				moveRobot(turnRight, isReal, isReal);
				break;
			default:
				moveRobot(turnLeft, isReal, isReal);
				break;
			}
			break;
		case LEFT:
			switch (dir) {
			case UP:
				moveRobot(turnRight, isReal, isReal);
				break;
			case DOWN:
				moveRobot(turnLeft, isReal, isReal);
				break;
			default:
				moveRobot(turnLeft, isReal, isReal);
				moveRobot(turnLeft, isReal, isReal);
				break;
			}
			break;
		default:
			switch (dir) {
			case UP:
				moveRobot(turnLeft, isReal, isReal);
				break;
			case DOWN:
				moveRobot(turnRight, isReal, isReal);
				break;
			default:
				moveRobot(turnLeft, isReal, isReal);
				moveRobot(turnLeft, isReal, isReal);
				break;
			}
			break;
		}
	}

	/**
	 * Execute calibration function (physically integrated)
	 */
	private void calibrate() {
		robot.move(calibrate, true, false);
	}

	/**
	 * Evalutes whether the robot can calibrate at its current position, based on its tracking map
	 * @param i 1 (current robot dir), 2 (left of robot dir), 3 (right of robot dir)
	 * @param bot Robot reference
	 * @param map Tracking map reference
	 * @return true if robot can calibrate at current position, false otherwise 
	 */
	public static boolean canCalibrate(int i, Robot bot, Map map) {
		if (i < 1 || i > 3) return false;

		int row, col;
		row = bot.getRow();
		col = bot.getCol();

		DIRECTION robotDir = bot.getDir();
		if (robotDir == DIRECTION.UP) {
			if (i == 1) {
				return (isCalibrateTile(row-2,col-1,map) && isCalibrateTile(row-2,col,map) && isCalibrateTile(row-2,col+1,map)); 
			} else if (i == 2) {
				return (isCalibrateTile(row-1,col-2,map) && isCalibrateTile(row,col-2,map) && isCalibrateTile(row+1,col-2,map)); 
			} else {
				return (isCalibrateTile(row-1,col+2,map) && isCalibrateTile(row,col+2,map) && isCalibrateTile(row+1,col+2,map)); 
			}
		} else if (robotDir == DIRECTION.LEFT) {
			if (i == 1) {
				return (isCalibrateTile(row-1,col-2,map) && isCalibrateTile(row,col-2,map) && isCalibrateTile(row+1,col-2,map)); 
			} else if (i == 2) {
				return (isCalibrateTile(row+2,col-1,map) && isCalibrateTile(row+2,col,map) && isCalibrateTile(row+2,col+1,map)); 
			} else {
				return (isCalibrateTile(row-2,col-1,map) && isCalibrateTile(row-2,col,map) && isCalibrateTile(row-2,col+1,map)); 
			}
		} else if (robotDir == DIRECTION.RIGHT) {
			if (i == 1) {
				return (isCalibrateTile(row-1,col+2,map) && isCalibrateTile(row,col+2,map) && isCalibrateTile(row+1,col+2,map)); 
			} else if (i == 2) {
				return (isCalibrateTile(row-2,col-1,map) && isCalibrateTile(row-2,col,map) && isCalibrateTile(row-2,col+1,map));  
			} else {
				return (isCalibrateTile(row+2,col-1,map) && isCalibrateTile(row+2,col,map) && isCalibrateTile(row+2,col+1,map));  
			}
		} else {
			if (i == 1) {
				return (isCalibrateTile(row+2,col-1,map) && isCalibrateTile(row+2,col,map) && isCalibrateTile(row+2,col+1,map));  
			} else if (i == 2) {
				return (isCalibrateTile(row-1,col+2,map) && isCalibrateTile(row,col+2,map) && isCalibrateTile(row+1,col+2,map)); 
			} else {
				return (isCalibrateTile(row-1,col-2,map) && isCalibrateTile(row,col-2,map) && isCalibrateTile(row+1,col-2,map)); 
			}
		}
	}
	
	/**
	 * Returns true if tile at [row (y), col (x)] is either invalid (i.e. beyond map boundaries)
	 * or is an obstacle
	 * @param row Tile row coordinate
	 * @param col Tile col coordinate
	 * @param explore Tracking map reference
	 * @return true if tile at [row, col] is either invalid or is an obstacle, false otherwise
	 */
	private static boolean isCalibrateTile(int row, int col, Map explore) {
		return !Map.isValidTile(row, col) || explore.isObstacleTile(row, col);
	}

	/**
	 * Returns true if Exploration is complete
	 * @return true if Exploration is complete, false otherwise
	 */
	public boolean runFinished() {
		return endRun;
	}

	/* Legacy Code - Intended Arrow Detection Camera Code
	 * Did not implement as a result of persisting issues with Arduino
	 */
	/**
	 * 
	 * @param desRow
	 * @param desCol
	 * @return
	 *
	public boolean isCamPosValid(int desRow, int desCol) {
		// check centre tile
		Tile centreTile = mapExplore.getTile(desRow, desCol);
		boolean robCenterValid = !centreTile.isObstacle() && !centreTile.isVirtualWall();
		if (!robCenterValid) return false;

		// check adjacent coordinates
		List<int[]> adjCoor = Map.getAdjCoor(desRow, desCol);
		if (adjCoor.isEmpty() || adjCoor.size() != 8) return false;

		for (int[] coor : adjCoor) 
			if (mapExplore.getTile(coor[0], coor[1]).isObstacle()) return false;

		return true;
	}
	*/

}
