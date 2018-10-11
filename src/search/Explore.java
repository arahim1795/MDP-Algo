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

	// Cardinal Reference
	private MOVEMENT forward = MOVEMENT.FORWARD;
	private MOVEMENT backward = MOVEMENT.BACKWARD;
	private MOVEMENT turnLeft = MOVEMENT.TURNLEFT;
	private MOVEMENT turnRight = MOVEMENT.TURNRIGHT;
	private MOVEMENT calibrate = MOVEMENT.CALIBRATE;

	// Robot Tracker
	private Robot robot;
	private int calibrateCount = 0;

	// Map Exploration Tracker
	private int explored = 0; // explore counter
	private double coveragePercent;
	private int maxCoverage;
	private long timeStart, timeEnd;
	private long duration;
	private Map mapExplore;
	private boolean visitedGoal = false;
	private boolean endRun = false;


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
	public void setupExplore() {	
		System.out.println("Setting up...");

		/* Deprecated
		// call turn commands, and calibrate
		List<String> calList = new ArrayList<String>();
		if (robot.isRealBot()) {
			System.out.println("Physical Robot Detected, Calibrating...");
			robot.move(turnLeft, false);
			robot.move(calibrate, false);

			robot.move(turnLeft, false);
			robot.move(calibrate, false);

			robot.move(turnRight, false);
			robot.move(calibrate, false);

			robot.move(turnRight, false);
			robot.move(calibrate, false);
		}
		if (calList.size() == 8) System.out.println("Robot Calibrated!");
		 */

		timeStart = System.currentTimeMillis();
		timeEnd = timeStart + duration;

		int robotRow = robot.getRobotRow();
		int robotCol = robot.getRobotCol();
		int[] startVal = {robotRow,robotCol};
		List<int[]> initialReveal = Map.getAdjCoor(robotRow, robotCol);
		initialReveal.add(startVal);
		for (int[] coor : initialReveal) 
			mapExplore.getTile(coor[0], coor[1]).setExplored(true);

		maxCoverage = (int) (coveragePercent / 100 * 300);

		senseEnv();
		updateExplore();

		// start exploration
		explore();
	}

	/**
	 * 
	 */
	public void explore() {
		if (System.currentTimeMillis() >= timeEnd || explored >= maxCoverage) {
			endRun = true;
			return;
		} else {
			move();
			updateExplore();
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
			str = fp.searchFastestPath(robot.getRobotRow(),robot.getRobotCol(),goalRow, goalCol);
			str += fp.searchFastestPath(goalRow,goalCol,startRow,startCol);
			fp.moveBotfromString(str,Simulator.returnRealRun());
		} else {
			if (!robot.isAtPos(startRow, startCol)) {
				str = fp.searchFastestPath(startRow, startCol);
				System.out.println(str);
				fp.moveBotfromString(str,Simulator.returnRealRun());
			}
		}

		System.out.println("Exploration Complete...");
		updateExplore();

		if (robot.isRealBot()) {
			rotateRobot(DIRECTION.LEFT);
			moveRobot(MOVEMENT.CALIBRATE);
			rotateRobot(DIRECTION.DOWN);
			moveRobot(MOVEMENT.CALIBRATE);
		}
		rotateRobot(DIRECTION.UP);
	}


	// Support Function
	/**
	 * 
	 */
	private void move() {
		if (peekLeft()) {
			moveRobot(turnLeft);
			if (peekUp()) moveRobot(forward);
		} else if (peekUp()) 
			moveRobot(forward);
		else if (peekRight()) {
			moveRobot(turnRight);
			if (peekUp()) moveRobot(forward);
		} else if (peekDown()) {
			moveRobot(turnLeft);
			moveRobot(turnLeft);
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean peekLeft() {
		switch(robot.getRobotDir()) {
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
		switch(robot.getRobotDir()) {
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
		switch(robot.getRobotDir()) {
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
		switch(robot.getRobotDir()) {
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
		int y = robot.getRobotRow(), x = robot.getRobotCol();
		return notObs(y-2, x-1) && notObs(y-2, x+1) && notVir(y-1, x);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isDownFree() {
		int y = robot.getRobotRow(), x = robot.getRobotCol();
		return notObs(y+2, x-1) && notObs(y+2, x+1) && notVir(y+1, x);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isLeftFree() {
		int y = robot.getRobotRow(), x = robot.getRobotCol();
		return notObs(y+1, x-2) && notObs(y-1, x-2) && notVir(y, x-1);
	}

	/**
	 * 
	 * @return
	 */
	private boolean isRightFree() {
		int y = robot.getRobotRow(), x = robot.getRobotCol();
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

	/**
	 * 
	 * @param move
	 */
	private void moveRobot(MOVEMENT move) {
		robot.move(move); // sendToAndroid);
		if (robot.getRobotRow() == MapConstant.GOAL_GRID_ROW && robot.getRobotCol() == MapConstant.GOAL_GRID_COL) visitedGoal = true;

		if (move != calibrate) senseEnv();
		else Comms.receiveMsg();

		/*
		if (robot.isRealBot()) {
			if (canCalibrate(robot.getRobotDir())) {
				calibrateCount = 0;
				moveRobot(calibrate);
			} else {
				calibrateCount++;
				if (calibrateCount >= 5) {
					DIRECTION dir = counterCal();
					if (dir != null) {
						calibrateCount = 0;
						calibrate(dir);
					}
				}
			}
		}
		*/
	}

	/**
	 * 
	 */
	private void senseEnv() {
		// update virtual robot's sensor positions
		robot.moveSensor();

		if (robot.isRealBot()) robot.multiSense(mapExplore);
		else robot.multiSense(mapExplore, mapActual);

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
		DIRECTION robotDir = robot.getRobotDir();
		if (robotDir == dir) return;
		switch (robotDir) {
		case UP:
			switch (dir) {
			case DOWN:
				moveRobot(turnLeft);
				moveRobot(turnLeft);
				break;
			case LEFT:
				moveRobot(turnLeft);
				break;
			default:
				moveRobot(turnRight);
				break;
			}
			break;
		case DOWN:
			switch (dir) {
			case UP:
				moveRobot(turnLeft);
				moveRobot(turnLeft);
				break;
			case LEFT:
				moveRobot(turnRight);
				break;
			default:
				moveRobot(turnLeft);
				break;
			}
			break;
		case LEFT:
			switch (dir) {
			case UP:
				moveRobot(turnRight);
				break;
			case DOWN:
				moveRobot(turnLeft);
				break;
			default:
				moveRobot(turnLeft);
				moveRobot(turnLeft);
				break;
			}
			break;
		default:
			switch (dir) {
			case UP:
				moveRobot(turnLeft);
				break;
			case DOWN:
				moveRobot(turnRight);
				break;
			default:
				moveRobot(turnLeft);
				moveRobot(turnLeft);
				break;
			}
			break;
		}
	}

	/*
	/**
	 * 
	 * @param dir
	 * @return
	 *
	private boolean canCalibrate(DIRECTION dir) {
		int row, col;
		row = robot.getRobotRow();
		col = robot.getRobotCol();

		switch (dir) {
		case UP:
			return isInvalidOrObs(row-2,col-1) && isInvalidOrObs(row-2,col) && isInvalidOrObs(row-2,col+1);
		case DOWN:
			return isInvalidOrObs(row+2,col-1) && isInvalidOrObs(row+2,col) && isInvalidOrObs(row+2,col+1);
		case LEFT:
			return isInvalidOrObs(row+1,col-2) && isInvalidOrObs(row,col-2) && isInvalidOrObs(row-1,col-2);
		default:
			return isInvalidOrObs(row+1,col+2) && isInvalidOrObs(row,col+2) && isInvalidOrObs(row-1,col+2);
		}
	}
	*/

	/*
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 *
	private boolean isInvalidOrObs(int row, int col) {
		if (!Map.isValidTile(row, col)) return true;
		else return mapExplore.getTile(row, col).isObstacle();
	}
	*/

	/*
	/**
	 * 
	 * @return
	 *
	private DIRECTION counterCal() {
		DIRECTION currDir, checkedDir;
		currDir = robot.getRobotDir();

		// Check Left
		checkedDir = DIRECTION.getLeft(currDir);
		if (canCalibrate(checkedDir)) return checkedDir;

		// Check Right
		checkedDir = DIRECTION.getRight(currDir);
		if (canCalibrate(checkedDir)) return checkedDir;

		// Check Reverse
		checkedDir = DIRECTION.getRight(checkedDir);
		if (canCalibrate(checkedDir)) return checkedDir;

		// if all fail, return null
		return null;
	}
	*/

	/*
	/**
	 * 
	 * @param dir
	 *
	private void calibrate(DIRECTION dir) {
		DIRECTION currDir = robot.getRobotDir();
		rotateRobot(dir);
		moveRobot(calibrate);
		rotateRobot(currDir);
	}
	*/

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
