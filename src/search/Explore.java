package search;

import java.util.ArrayList;
import java.util.List;

import map.*;
import robot.*;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;

public class Explore {
	
	// Robot Tracker
	private Robot robot;
	
	// Map Exploration Tracker
	private int coverage;
	private Map map;
	
	// Obstacles Tracker
	private ArrayList<ExploreTile> obstacles = new ArrayList<ExploreTile>();
	
	// Simulation Tracker
	private Map mapActual;
	
	// Constructor
	public Explore(Robot bot, Map actual) {
		robot = bot;
		map = new Map();
		mapActual = actual;
	}
	
	// Getter(s)
	/**
	 * 
	 * @return
	 */
	public Map getMap() {
		return map;
	}
	
	// Other Function(s)
	/**
	 * 
	 * @param map
	 */
	public void explore(Map map, Robot bot) {
		mapActual = map;
		robot = bot;
		
		do {
			move();
		} while (!RobotConstant.isAtStart(bot.getRobotRow(), bot.getRobotCol()));
	}
	
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
	private void updateRobotMap() {
		
	}

}
