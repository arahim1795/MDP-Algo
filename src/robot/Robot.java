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
	private DIRECTION orientation;
	private Map memory;
	private boolean realBot; //distinguish simulation or real physical bot object
	
	/**
	 * Create an robot 'placed' with reference to the x and y 
	 * coordinates of the centre component, fitted with an internal 
	 * map
	 * @param x Centre x-coordinate of Robot
	 * @param y Centre y-coordinate of Robot
	 * @param realBot TODO
	 * @param size Set size of square robot
	 * @param direction Robot's orientation
	 * @see map
	 */

	public Robot (int x, int y, boolean realBot){
		this.memory = new Map();

		
		this.robotRow = x;
		this.robotCol = y;
		
		this.realBot = realBot;
		this.orientation = RobotConstant.DEFAULT_START_DIR;
	}
	public Robot(int size, int x, int y, DIRECTION direction, boolean realBot) {
		this.memory = new Map();
		
		this.robotRow = x;
		this.robotCol = y;
		
		this.realBot = realBot;
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
	
	public boolean isRealBot(){
		return this.realBot;
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
		}
	}
	


	
}
