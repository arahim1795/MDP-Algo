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
	private Map mapRobot;
	
	// Map Exploration Tracker
	private int coverage;
	
	// Obstacles Tracker
	private ArrayList<ExploreTile> obstacles = new ArrayList<ExploreTile>();
	
	// Simulation Tracker
	private Map mapActual;
	
	/**
	 * 
	 * @param map
	 */
	public void explore(Map map, Robot bot) {
		
		mapRobot = new Map();
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
		return notObs(x-1, y-1) && notObs(x+1, y-1) && notVirtual(x, y-1);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isDownFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x-1, y+1) && notObs(x+1, y+1) && notVirtual(x, y+1);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isLeftFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x-1, y+1) && notObs(x-1, y-1) && notVirtual(x-1, y);
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean isRightFree() {
		int x = robot.getRobotRow(), y = robot.getRobotCol();
		return notObs(x+1, y+1) && notObs(x+1, y-1) && notVirtual(x+1, y);
	}
	
	/**
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	private boolean notObs(int col, int row) {
		if (Map.isValid(col, row)) {
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
	private boolean notVirtual(int col, int row) {
		if (Map.isValid(col, row)) {
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
		mapRobot.update();
	}

}
