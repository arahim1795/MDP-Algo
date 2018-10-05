package search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import map.*;
import robot.*;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;

import search.FastestPath;

public class Explore {
	
	// Robot Tracker
	private Robot robot;
	
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
	public void setupExplore() {
		if (robot.isRealBot()) {
			// TODO: incorporate real robot setup if any in future
			System.out.println("Physical Robot Functions are unsupported as of now");
		}
		
		System.out.println("Setting up...");
		
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
		
		// kickstart exploration function
		explore();
		
	}
	public void explore(){
		if(System.currentTimeMillis() >= timeEnd || explored == maxCoverage) {
			endRun = true;
			return;
		}
		else{
			int col, row;
			DIRECTION dir;
			
			move();
			
			//Debug Scripts
			row = robot.getRobotRow();
			col = robot.getRobotCol();
			dir = robot.getRobotDir();
			System.out.println("R: " + row + " C: " + col + " D: " + dir);
			
			updateExplore();
			
		}
		
	}
	
	@Deprecated
	public void explore2() {
		// TODO make termination dependent on another variable
		// explore set to terminate after 3 minutes (only)
		int col, row;
		DIRECTION dir;
		do {
			move();
			updateExplore();
		} while (System.currentTimeMillis() <= timeEnd && explored < maxCoverage);
		
		goToStart();
	}
	
	public void goToStart() {
		
		int goalRow = MapConstant.GOAL_GRID_ROW;
		int goalCol = MapConstant.GOAL_GRID_COL;
		int startRow = MapConstant.START_GRID_ROW;
		int startCol = MapConstant.START_GRID_COL;
		
		FastestPath fp = new FastestPath(mapExplore, robot);;
		String str;
		
		if (!visitedGoal) {
			str = fp.searchFastestPath(robot.getRobotRow(),robot.getRobotCol(),goalRow, goalCol);
			str += fp.searchFastestPath(goalRow,goalCol,startRow, startCol);
			fp.moveBotfromString(str);
		}
		else{
			
			if (!robot.isAtPos(startRow, startCol)) {
				str = fp.searchFastestPath(startRow, startCol);
				System.out.println(str);
				fp.moveBotfromString(str);
			}
		}

	}
	
	// Support Function
	/**
	 * 
	 */
	private void move() {
		if (peekLeft()) {
			moveRobot(MOVEMENT.TURNLEFT);
			if (peekUp()) moveRobot(MOVEMENT.FORWARD);
		} else if (peekUp()) 
			moveRobot(MOVEMENT.FORWARD);
		  else if (peekRight()){
			moveRobot(MOVEMENT.TURNRIGHT);
			if (peekUp()) moveRobot(MOVEMENT.FORWARD);
		} else if (peekDown()) {
			moveRobot(MOVEMENT.TURNLEFT);
			moveRobot(MOVEMENT.TURNLEFT);
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
		// TODO physical robot movement
		robot.move(move, true); // sendToAndroid);
		senseEnv();
		if (robot.getRobotRow() == MapConstant.GOAL_GRID_ROW && robot.getRobotCol() == MapConstant.GOAL_GRID_COL)
			visitedGoal = true;
	}
	
	/**
	 * 
	 */
	private void senseEnv() {
		robot.moveSensor();
		robot.sense(mapExplore, mapActual);
		mapActual.repaint();
		mapExplore.repaint();
	}
	
	private void updateExplore() {
		int count = 0;
		for (int r = 0; r < Map.row; r++) 
			for (int c = 0; c < Map.col; c++)
				if (mapExplore.getTile(r, c).isExplored()) {
					count++;
					mapExplore.repaint();
				}
		
		explored = count; 
		System.out.println("Explored: " + count);
	}

	public boolean runFinished() {
		return endRun;
	}

}
