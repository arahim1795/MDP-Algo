package robot;

import map.Map;
import utility.Utility.Orientation;

/**
 * @author 18/19 S1 G3
 *
 */
public class Robot {

	private int[] coor;
	private Orientation orientation;
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
	public Robot(int size, int x, int y, Orientation direction, int len, int wid) {
		memory = new Map(len, wid);
		
		coor = new int[2];
		coor[0] = x;
		coor[1] = y;
		
		orientation = direction;
		
		validate();
	}
	public int getRobotRow(){
		return coor[0];
		
	}
	
	public int getRobotCol(){
		return coor[1];
	}
	/*
	 * Checks if robot is in a valid position on the map
	 */
	public void validate() {
		
	}
	
}
