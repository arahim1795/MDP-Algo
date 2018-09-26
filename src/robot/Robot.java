package robot;

import java.util.List;

import map.Map;

import map.Constants;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;

import map.Tile;

import utility.Utility.Orientation;

/**
 * @author 18/19 S1 G3
 *
 */
public class Robot {
 
	private int robotRow; // Coordinate of centre component
	private int robotCol; // Coordinate of centre component
	private DIRECTION orientation
	private Map memory;
	
	/**
	 * Create an robot 'placed' with reference to the x and y 
	 * coordinates of the centre component, fitted with an internal 
	 * map
	 * @param size Set size of square robot
	 * @param x Centre x-coordinate of Robot
	 * @param y Centre y-coordinate of Robot
	 * @param direction Robot's orientation
	 * @see map
	 */

	public Robot (int x, int y){
		this.memory = new Map();

		
		this.robotRow = x;
		this.robotCol = y;
		
		this.orientation = RobotConstant.DEFAULT_START_DIR;
	}
	public Robot(int size, int x, int y, DIRECTION direction) {
		this.memory = new Map();
		
		this.robotRow = x;
		this.robotCol = y;
		
		this.orientation = direction;
	}
	public int getRobotRow(){
		return this.robotRow;
  }
  
	public int getRobotCol(){
		return this.robotCol;
	}
	
	public DIRECTION getRobotOrientation(){
		return this.orientation;
	}
	
	public void move(MOVEMENT m, boolean sendToAndroid) {
        /*if (!realBot) {
            // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        }*/

        switch (m) {
            case FORWARD:
                switch (this.orientation ) {
                    case UP:
                        robotRow++;
                        break;
                    case RIGHT:
                        robotCol++;
                        break;
                    case DOWN:
                        robotRow--;
                        break;
                    case LEFT:
                        robotCol--;
                        break;
                }
                break;
            case BACKWARD:
                switch (this.orientation) {
                    case UP:
                        robotRow--;
                        break;
                    case RIGHT:
                        robotCol--;
                        break;
                    case DOWN:
                        robotRow++;
                        break;
                    case LEFT:
                        robotCol++;
                        break;
                }
                break;
            case TURNRIGHT:
            case TURNLEFT:
                this.orientation = updateTurnDirection(m);
                break;
            case CALIBRATE:
                break;  
            default:
                System.out.println("Error in Robot.move()!");
                break;
        }

        if (realBot) sendMovement(m, sendToAndroid);
        else System.out.println("Move: " + MOVEMENT.print(m));

        updateTouchedGoal();
    }
	
	private DIRECTION updateTurnDirection(MOVEMENT m){
		if(m == MOVEMENT.TURNLEFT){
			return DIRECTION.getNext(this.orientation);
		}
		else{
			return DIRECTION.getNext(this.orientation);
		this.valid = validate();
		if (!valid) System.err.println("Invalid Robot Location");
	}
	
	/*
	 * Checks if robot is in a valid position on the map
	 */
	public boolean validate() {
		List<int[]> adjCoor = Utility.getAdjCoor(this.coor);
		int validCount = 0;
		
		for (int[] coor : adjCoor) {
			Tile[][] actualField = Map.getMap();
			if (!actualField[coor[1]][coor[2]].isObstacle()) {
				validCount++;
			}
		}
		
		return validCount == 8;
	}

	
}
