package robot;

import java.util.List;

import map.Map;
import map.Tile;
import utility.Utility;
import utility.Utility.Orientation;

/**
 * @author 18/19 S1 G3
 *
 */
public class Robot {

	private int[] coor; // Coordinate of centre component
	private Orientation orientation;
	private boolean valid = false;
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
	public Robot(int size, int x, int y, Orientation direction) {
		memory = new Map();
		
		coor = new int[2];
		coor[0] = x;
		coor[1] = y;
		
		orientation = direction;
		
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
