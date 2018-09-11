package map;

/**
 * @author 18/19 S1 G3
 */
public class Tile {

	private boolean obstacle = false, explored = false;
	private int[] coor;
	
	/**
	 * Tiles of field
	 * @param x x-coordinate of Tile
	 * @param y y-coordinate of Tile
	 */
	public Tile(int x, int y) {
		coor = new int[2];
		
		coor[0] = x;
		coor[1] = y;
	}

	/**
	 * Returns true is tile is marked as an obstacle
	 * @return true if tile is set as an obstacle, false otherwise
	 */
	public boolean isObstacle() {
		return obstacle;
	}

	/**
	 * Set tile as an obstacle
	 * @param obstacle If set to true, tile is set as obstacle
	 */
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}

	/**
	 * Returns true if tile has been explored by Robot
	 * @return true if tile has been explored, false otherwise
	 * @see robot.Robot
	 */
	public boolean isExplored() {
		return explored;
	}

	/**
	 * Set tile as explored by robot
	 * @param explored If set to true, tile has been explored by Robot
	 * @see robot.Robot
	 */
	public void setExplored(boolean explored) {
		this.explored = explored;
	}

	/**
	 * Return an integer array of tile coordinates (i.e. assigned coordinates,
	 * not with respect to the array index of the Tile[][])
	 * @return an array of coordinates [x,y]
	 */
	public int[] getCoor() {
		return coor;
	}

	
}
