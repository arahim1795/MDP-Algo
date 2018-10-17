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
	private MOVEMENT backward = MOVEMENT.BACKWARD;
	private MOVEMENT turnLeft = MOVEMENT.TURNLEFT;
	private MOVEMENT turnRight = MOVEMENT.TURNRIGHT;
	private MOVEMENT calibrate = MOVEMENT.CALIBRATE;

	private DIRECTION up = DIRECTION.UP;
	private DIRECTION down = DIRECTION.DOWN;
	private DIRECTION left = DIRECTION.LEFT;
	private DIRECTION right = DIRECTION.RIGHT;

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

		// call turn commands, and calibrate
		List<String> calList = new ArrayList<String>();
		if (robot.isRealBot()) {
			System.out.println("Physical Robot Detected, Calibrating...");

			rotateRobot(left);
			calibrate();

			rotateRobot(down);
			calibrate();

			rotateRobot(left);
			calibrate();

			rotateRobot(up);
		}
		if (calList.size() == 7) System.out.println("Robot Calibrated!");

		timeStart = System.currentTimeMillis();
		timeEnd = timeStart + duration;

		int robotRow = robot.getRow();
		int robotCol = robot.getCol();
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
			
			// if @ corners
			// if robotdir can calibrate
			DIRECTION dir = robot.getDir();
			if (canCalibrate(1)) {
				// if left can calibrate
				if (canCalibrate(2)) {
					calibrate();
					rotateRobot(robot.findTurnDirection(turnLeft));
					calibrate();
					rotateRobot(dir);
				}
				// if right can calibrate
				else if (canCalibrate(3)) {
					calibrate();
					rotateRobot(robot.findTurnDirection(turnRight));
					calibrate();
					rotateRobot(dir);
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
				System.out.println(str);
				fp.moveBotfromString(str,Simulator.returnRealRun());
			}
		}

		System.out.println("Exploration Complete...");
		updateExplore();


		if (robot.isRealBot()) {
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

	/**
	 * 
	 * @param move
	 */
	private void moveRobot(MOVEMENT move) {
		robot.move(move, true, true);
		if (robot.getRow() == MapConstant.GOAL_GRID_ROW && robot.getCol() == MapConstant.GOAL_GRID_COL) visitedGoal = true;

		if (move != calibrate) senseEnv();
		else Comms.getAndReceipt(Comms.arDone);
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
		DIRECTION robotDir = robot.getDir();
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
				return isInvOrObs(row-2,col-1) && isInvOrObs(row-2,col) && isInvOrObs(row-2,col+1);
			case 3:
				return isInvOrObs(row+2,col-1) && isInvOrObs(row+2,col) && isInvOrObs(row+2,col+1);
			default:
				return isInvOrObs(row-1,col-2) && isInvOrObs(row,col-2) && isInvOrObs(row+1,col-2);
			}
		default:
			switch (i) {
			case 2:
				return isInvOrObs(row+2,col-1) && isInvOrObs(row+2,col) && isInvOrObs(row+2,col+1);
			case 3:
				return isInvOrObs(row-2,col-1) && isInvOrObs(row-2,col) && isInvOrObs(row-2,col+1);
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

	private boolean listenTerminate() {
		String str = Comms.receiveMsg();
		if (str != null) {
			String[] strArr = str.split("_");
			if (strArr[1].equals(Comms.anStop)) return true;
		}
		return false;
	}

}
