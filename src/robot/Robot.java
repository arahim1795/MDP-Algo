package robot;

import map.Map;
import map.Constants;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import utility.Utility.Orientation;

/**
 * @author 18/19 S1 G3
 *
 */
public class Robot {

	private int robotRow;
	private int robotCol;
	private DIRECTION orientation;
	private boolean valid;
	private Map memory;
	
	/**
	 * Create an robot 'placed' with reference to the x and y 
	 * coordinates of the down-left most component,
	 * fitted with an internal map
	 * @param size Set size of square robot
	 * @param x Down-left x-coordinate of Robot
	 * @param y Down-left y-coordinate of Robot
	 * @param direction Robot's orientation
	 * @param len Length of map
	 * @param wid Width of map
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
		}
		
	}

	
}
