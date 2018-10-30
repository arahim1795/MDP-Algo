package search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import map.*;
import robot.*;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;

import search.FastestPath;
import utility.Comms;

public class Explore {

	// Explore Run Limit


	// Cardinal Reference
	private MOVEMENT forward = MOVEMENT.FORWARD;
	private MOVEMENT turnLeft = MOVEMENT.TURNLEFT;
	private MOVEMENT turnRight = MOVEMENT.TURNRIGHT;
	private MOVEMENT calibrate = MOVEMENT.CALIBRATE;

	private DIRECTION up = DIRECTION.UP;
	private DIRECTION down = DIRECTION.DOWN;
	private DIRECTION left = DIRECTION.LEFT;
	private DIRECTION right = DIRECTION.RIGHT;

	// Robot Tracker
	private Robot robot;
	private boolean isReal;
	private int calibrateCount = 0;

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


	// Obstacles Tracker
	private ArrayList<ExploreTile> obstacles = new ArrayList<ExploreTile>();

	// Simulation Tracker
	private Map mapActual;

	// Constructor
	/**
	 * 
	 * @param bot
	 * @param explore
	 * @param actual
	 * @param seconds
	 * @param coveragePercent
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
	 * 
	 * @return
	 */
	public Map getMap() {
		return mapExplore;
	}

	// Main Functions
	/**
	 * 
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
	 * 
	 */
	public void setupExplore() {	
		timeStart = System.currentTimeMillis();
		timeEnd = timeStart + duration;
		softEnd = timeStart + TimeUnit.SECONDS.toMillis(250);

		int robotRow = robot.getRow();
		int robotCol = robot.getCol();
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
	 * 
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
			
			// if @ corners
			// if robotdir can calibrate
			DIRECTION dir = robot.getDir();
			if (canCalibrate(1)) { // if front-3 is obs/edge
				if (rightNotExplored()) { // if right-3 is not explored
					calibrate();
					calibrateCount = 0;
					moveRobot(turnRight, isReal, isReal);
					if (canCalibrate(1)) {
						calibrate();
						moveRobot(turnRight, isReal, isReal);
						calibrateCount = 0;
					}
				} else if (canCalibrate(3)) { // if right-3 is obs/edge
					calibrate();
					moveRobot(turnRight, isReal, isReal);
					calibrate();
					calibrateCount = 0;
				} else if (canCalibrate(2)) { // if left-3 is obs/edge
					calibrate();
					moveRobot(turnLeft, isReal, isReal);
					calibrate();
					calibrateCount = 0;
				}
			}
			
			if (calibrateCount >= 3) {
				if (canCalibrate(1)) {
					calibrate();
					calibrateCount = 0;
				} else if (canCalibrate(3)) {
					moveRobot(turnRight, isReal, isReal);
					calibrate();
					moveRobot(turnLeft, isReal, isReal);
					calibrateCount = 0;
				} else if (canCalibrate(2)) {
					moveRobot(turnLeft, isReal, isReal);
					calibrate();
					moveRobot(turnRight, isReal, isReal);
					calibrateCount = 0;
				}
			}
			
		}
	}

	/**
	 * 
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
			fp.moveBotfromString(str,Simulator.returnRealRun());
		} else {
			if (!robot.isAtPos(startRow, startCol)) {
				str = fp.searchFastestPath(startRow, startCol);
				fp.moveBotfromString(str,Simulator.returnRealRun());
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
	 * 
	 */
	private void move() {
		if (peekLeft()) {
			moveRobot(turnLeft, isReal, isReal);
			if (peekUp()) moveRobot(forward, isReal, isReal);
		} else if (peekUp()) 
			moveRobot(forward, isReal, isReal);
		else if (rightNotExplored()) {
			moveRobot(turnRight, isReal, isReal);
			if (peekUp()) moveRobot(forward, isReal, isReal);
		} else if (peekDown()) {
			moveRobot(turnLeft, isReal, isReal);
			moveRobot(turnLeft, isReal, isReal);
		}
	}

	/**
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
	 */
	private boolean isUpFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y-2, x-1) && notObs(y-2, x+1) && notVir(y-1, x);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isDownFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+2, x-1) && notObs(y+2, x+1) && notVir(y+1, x);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isLeftFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+1, x-2) && notObs(y-1, x-2) && notVir(y, x-1);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isRightFree() {
		int y = robot.getRow(), x = robot.getCol();
		return notObs(y+1, x+2) && notObs(y-1, x+2) && notVir(y, x+1);
	}

	/**
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private boolean notObs(int row, int col) {
		if (Map.isValidTile(row, col)) {
			Tile tile = mapExplore.getTile(row, col);
			return tile.isExplored() && !tile.isObstacle();
		}
		return false;
	}

	/**
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private boolean notVir(int row, int col) {
		if (Map.isValidTile(row, col)) {
			Tile tile = mapExplore.getTile(row, col);
			return tile.isExplored() && !tile.isObstacle() && !tile.isVirtualWall();
		}
		return false;
	}
	
	private void moveRobot(MOVEMENT move, boolean sendArd, boolean sendAnd) {
		if (robot.isRealBot()) {
			msg = robot.move(move, sendArd, sendAnd);
			robot.moveSensor();
			robot.multiSense(mapExplore, msg);
		}
		else {
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
	 * 
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
	 * 
	 */
	private void calibrate() {
		robot.move(calibrate, true, false);
	}

	// 1 (current robot dir), 2 (left of robot dir), 3 (right of robot dir)
	/**
	 * 
	 * @param i
	 * @return
	 */
	private boolean canCalibrate(int i) {
		if (i < 1 || i > 3) return false;

		int row, col;
		row = robot.getRow();
		col = robot.getCol();

		DIRECTION robotDir = robot.getDir();

		switch (robotDir) {
		case UP:
			switch (i) {
			case 2:
				return isInvOrObs(row-1,col-2) && isInvOrObs(row,col-2) && isInvOrObs(row+1,col-2);
			case 3:
				return isInvOrObs(row-1,col+2) && isInvOrObs(row,col+2) && isInvOrObs(row+1,col+2);
			default:
				return isInvOrObs(row-2,col-1) && isInvOrObs(row-2,col) && isInvOrObs(row-2,col+1);
			}
		case DOWN:
			switch (i) {
			case 2:
				return isInvOrObs(row-1,col+2) && isInvOrObs(row,col+2) && isInvOrObs(row+1,col+2);
			case 3:
				return isInvOrObs(row-1,col-2) && isInvOrObs(row,col-2) && isInvOrObs(row+1,col-2);
			default:
				return isInvOrObs(row+2,col-1) && isInvOrObs(row+2,col) && isInvOrObs(row+2,col+1);
			}
		case LEFT:
			switch (i) {
			case 2:
				return isInvOrObs(row+2,col-1) && isInvOrObs(row+2,col) && isInvOrObs(row+2,col+1);
			case 3:
				return isInvOrObs(row-2,col-1) && isInvOrObs(row-2,col) && isInvOrObs(row-2,col+1);
			default:
				return isInvOrObs(row-1,col-2) && isInvOrObs(row,col-2) && isInvOrObs(row+1,col-2);
			}
		default:
			switch (i) {
			case 2:
				return isInvOrObs(row-2,col-1) && isInvOrObs(row-2,col) && isInvOrObs(row-2,col+1);
			case 3:
				return isInvOrObs(row+2,col-1) && isInvOrObs(row+2,col) && isInvOrObs(row+2,col+1);
			default:
				return isInvOrObs(row-1,col+2) && isInvOrObs(row,col+2) && isInvOrObs(row+1,col+2);
			}
		}
	}

	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private boolean isInvOrObs(int row, int col) {
		if (!Map.isValidTile(row, col)) return true;
		else return mapExplore.getTile(row, col).isObstacle();
	}
	
	private boolean rightNotExplored() {
		int row, col;
		row = robot.getRow(); col = robot.getCol();
		switch (robot.getDir()) {
		case UP:
			if (Map.isValidTile(row-1,col+1) && Map.isValidTile(row,col+1) && Map.isValidTile(row+1,col+1))
				return mapExplore.getTile(row-1, col+1).isExplored() && mapExplore.getTile(row, col+1).isExplored() && mapExplore.getTile(row+1, col+1).isExplored();
			return false; 
		case DOWN:
			if (Map.isValidTile(row-1,col-1) && Map.isValidTile(row,col-1) && Map.isValidTile(row+1,col-1))
				return mapExplore.getTile(row-1, col-1).isExplored() && mapExplore.getTile(row, col-1).isExplored() && mapExplore.getTile(row+1, col-1).isExplored();
			return false; 
		case LEFT:
			if (Map.isValidTile(row-1,col-1) && Map.isValidTile(row-1,col) && Map.isValidTile(row-1,col+1))
				return mapExplore.getTile(row-1, col-1).isExplored() && mapExplore.getTile(row-1, col).isExplored() && mapExplore.getTile(row-1, col+1).isExplored();
			return false;
		default:
			if (Map.isValidTile(row+1,col-1) && Map.isValidTile(row+1,col) && Map.isValidTile(row+1,col+1))
				return mapExplore.getTile(row+1, col-1).isExplored() && mapExplore.getTile(row+1, col).isExplored() && mapExplore.getTile(row+1, col+1).isExplored();
			return false;
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean runFinished() {
		return endRun;
	}

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

}
