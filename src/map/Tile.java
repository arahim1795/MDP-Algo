package map;

/**
 * @author 18/19 S1 G3
 *
 */
public class Tile {

	private boolean obstacle = false, waypoint = false;
	private int[] coor;
	
	/**
	 * Tiles of field
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
	 * Set tile as waypoint
	 * @param obstacle If set to true, tile is set as obstacle
	 */
	public void setObstacle(boolean obstacle) {
		this.obstacle = obstacle;
	}

	/**
	 * Returns true is tile is set as a waypoint
	 * @return true if tile is set as a waypoint, false otherwise
	 */
	public boolean isWaypoint() {
		return waypoint;
	}

	/**
	 * Set tile as waypoint
	 * @param waypoint If set to true, tile is set as waypoint
	 */
	public void setWaypoint(boolean waypoint) {
		this.waypoint = waypoint;
	}

	/**
	 * Return the coordinates of tile (i.e. working coordinates,
	 * not with respect to the array index of the tile)
	 * @return the x
	 */
	public int[] getCoor() {
		return coor;
	}

	
}
