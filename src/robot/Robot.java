package robot;

import java.util.List;

import map.Map;
import map.Constants;
import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;
import robot.RobotConstant.MOVEMENT;
import map.Tile;

/**
 * @author 18/19 S1 G3
 */
public class Robot {
 
	private int row; // Coordinate of centre component
	private int col; // Coordinate of centre component
	private DIRECTION direction;
	private Map map;
	private boolean realBot;

	
	// Constructor(s)
	/**
	 * Instantiate a robot 'placed' at coordinate row(x) and col(y)
	 * facing up (default), with an blank map
	 * @param startRow Centre row(x)-coordinate of Robot
	 * @param startCol Centre row(y)-coordinate of Robot

	 * @param isReal
	 * @see map
	 */
	public Robot (int startRow, int startCol, boolean isReal){
		row = startRow;
		col = startCol;
		direction = RobotConstant.DEFAULT_START_DIR;
		map = new Map();

		realBot = isReal;
	}
	
	/**
	 * Instantiate a robot 'placed' at coordinate row(x) and col(y)
	 * facing in set direction, with an blank map
	 * @param startRow Centre row(x)-coordinate of Robot
	 * @param startCol Centre col(y)-coordinate of Robot

	 * @param isReal
	 * @param direction Direction Robot's facing
	 */
	public Robot(int startRow, int startCol, DIRECTION startDir, boolean isReal) {
		row = startRow;
		col = startCol;
		direction = startDir;
		map = new Map();
		realBot = isReal;
	}
	
	// Getter(s)
	/**
	 * 
	 * @return
	 */
	public int getRobotRow() {
		return row;
	}
  
	public int getRobotCol() {
		return col;
	}
	
	public DIRECTION getRobotDir() {
		return direction;
	}
	
	// Setter
	/**
	 * 
	 * @param row
	 */
	public void setRobotRow(int newRow) {
		row = newRow;
	}
	
	/**
	 * 
	 * @param col
	 */
	public void setRobotCol(int newCol) {
		col = newCol;
	}
	
	/**
	 * 
	 * @param newDir
	 */
	public void setRobotDir(DIRECTION newDir) {
		direction = newDir;
	}
	// Other Functions
	/**
	 * 
	 * @param m
	 * @param sendToAndroid
	 */
	
	public boolean isRealBot(){
		return this.realBot;
	}
	

	public void move(MOVEMENT m, boolean sendToAndroid) {
        /* if (!realBot) {
            // Emulate real movement by pausing execution.
            try {
                TimeUnit.MILLISECONDS.sleep(speed);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong in Robot.move()!");
            }
        } */

        switch (m) {
            case FORWARD:
                switch (direction) {
                    case UP:
                        row++;
                        break;
                    case RIGHT:
                        col++;
                        break;
                    case DOWN:
                        row--;
                        break;
                    case LEFT:
                        col--;
                        break;
                }
                break;
            case BACKWARD:
                switch (direction) {
                    case UP:
                        row--;
                        break;
                    case RIGHT:
                        col--;
                        break;
                    case DOWN:
                        row++;
                        break;
                    case LEFT:
                        col++;
                        break;
                }
                break;
            case TURNRIGHT:
            case TURNLEFT:
                direction = updateTurnDirection(m);
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
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	private DIRECTION updateTurnDirection(MOVEMENT m){
		if(m == MOVEMENT.TURNLEFT) {
			return DIRECTION.getNext(direction);
		}
		else {
			return DIRECTION.getNext(direction);
		}
	}
	
}
