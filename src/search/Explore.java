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
	private int coverage;
	private int explored; // explore counter
	private long timeStart, timeEnd;
	private long duration;
	private Map mapExplore;
	
	// Obstacles Tracker
	private ArrayList<ExploreTile> obstacles = new ArrayList<ExploreTile>();
	
	// Simulation Tracker
	private Map mapActual;
	
	// Constructor
	public Explore(Robot bot, Map actual, long minutes) {
		robot = bot;
		mapExplore = new Map();
		mapActual = actual;
		duration = TimeUnit.MINUTES.toMillis(minutes);
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
		
		senseEnv();
		updateExplore();
		
		// kickstart exploration function
		explore();
		
	}
	
	public void explore() {
		// TODO make termination dependent on another variable
		// explore set to terminate after 3 minutes (only)
		do {
			move();
			updateExplore();
		} while (System.currentTimeMillis() <= timeEnd);
		goToStart();
	}
	
	private void goToStart() {
		
		int row = RobotConstant.DEFAULT_START_ROW;
		int col = RobotConstant.DEFAULT_START_COL;
		
		if (robot.getRobotRow() != row && robot.getRobotCol() != col) {
			FastestPath fp = new FastestPath(mapExplore, robot);
			fp.searchFastestPath(row, col);
		}

	}
	
	// Support Function
	/**
	 * 
	 */
	private void move() {
		if (peekLeft()) {
			moveRobot(MOVEMENT.TURNLEFT);
		} else if (peekRight()) {
			moveRobot(MOVEMENT.TURNRIGHT);
		} else if (peekUp()) {
			moveRobot(MOVEMENT.FORWARD);
		} else {
			moveRobot(MOVEMENT.BACKWARD);
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
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x-1, y-1) && notObs(x+1, y-1) && notVir(x, y-1);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isDownFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x-1, y+1) && notObs(x+1, y+1) && notVir(x, y+1);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isLeftFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x-1, y+1) && notObs(x-1, y-1) && notVir(x-1, y);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isRightFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x+1, y+1) && notObs(x+1, y-1) && notVir(x+1, y);
	}
	
	/**
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private boolean notObs(int row, int col) {
		if (Map.isValidTile(col, row)) {
			Tile tile = mapActual.getTile(col, row);
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
		if (Map.isValidTile(col, row)) {
			Tile tile = mapActual.getTile(col, row);
			return tile.isExplored() && !tile.isObstacle() && !tile.isVirtualWall();
		}
		return false;
	}
	
	/**
	 * 
	 * @param move
	 */
	private void moveRobot(MOVEMENT move) {
		robot.move(move, true); // sendToAndroid);
	}
	
	/**
	 * 
	 */
	private void senseEnv() {
		robot.moveSensor();
		robot.sense(mapExplore, mapActual);
	}
	
	private void updateExplore() {
		int count = 0;
		for (int r = 0; r < Map.row; r++) 
			for (int c = 0; c < Map.col; c++)
				if (mapExplore.getTile(r, c).isExplored()) count++;
		
		explored = count; 
		System.out.println("Explored: " + count);
	}

}
