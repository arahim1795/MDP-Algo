package map;

/**
 * @author 18/19 S1 G3
 */
public class Tile {

	private boolean explored = false;
	private boolean obstacle = false;
	private boolean boundary = false;
	private int[] coor;
	
	/**
	 * Tiles of field
	 * @param c x-coordinate of Tile
	 * @param r y-coordinate of Tile
	 */
	public Tile(int c, int r) {
		coor = new int[2];
		
		coor[0] = c;
		coor[1] = r;
	}

	/**
	 * Returns true is tile is marked as an obstacle
	 * @return true if tile is set as an obstacle, false otherwise
	 */
	public boolean isObstacle() {
		return this.obstacle;
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
	 * Returns true if this Tile is bounded by adjacent obstacle(s) or map edge(s)
	 * @return true Tile is a boundary tile, false otherwise
	 */
	public boolean isBoundary() {
		return this.boundary;
	}
	
	/**
	 * 
	 * @param virtualWall
	 */
	public void setBoundary(boolean boundary) {
		this.boundary = boundary;
	}

	/**
	 * Return an integer array of tile coordinates (i.e. assigned coordinates,
	 * not with respect to the array index of the Tile[][])
	 * @return an array of coordinates [c,r]
	 */
	public int[] getCoor() {
		return coor;
	}

	
}
