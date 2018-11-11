package map;

/**
 * @author 18/19 S1 G3
 */
public class Tile {

	private int col;
	private int row;
	private int obStacles = 0;

	private boolean explored = false;
	private boolean obstacle = false;
	private boolean virtualWall = false;
	private boolean persistent = false;
	private boolean midPoint = false;

	// Constructor
	/**
	 * Tiles of field
	 * @param row Tile row coordinate
	 * @param col Tile col coordinate
	 */
	public Tile(int row, int col) {
		this.col = col;
		this.row = row;
	}

	
	// Getter(s)
	/**
	 * Returns tile row (y) coordinate
	 * @return tile row coordinate
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns tile col (x) coordinate
	 * @return tile col coordinate
	 */
	public int getCol() {
		return col;
	}

	/**
	 * Returns true if tile is explored
	 * @return true if tile is explored, false otherwise
	 */
	public boolean isExplored() {
		return explored;
	}
	
	/**
	 * Returns true if tile is an obstacle
	 * @return true if tile is an obstacle, false otherwise
	 */
	public boolean isObstacle() {
		return obstacle;
	}

	/**
	 * Returns true if tile is a virtual wall
	 * @return true if tile is a virtual wall, false otherwise
	 */
	public boolean isVirtualWall() {
		return virtualWall;
	}

	/**
	 * Returns true if tile is persistent
	 * @return true if tile is persistent, false otherwise
	 */
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * Returns true if tile is midpoint
	 * @return ture if tile is midpoint, false otherwise
	 */
	public boolean isMidPoint(){
		return midPoint;
	}


	// Setter(s)
	/**
	 * Set tile as explored
	 * @param bool If set to true, tile is explored
	 */
	public void setExplored(boolean bool) {
		explored = bool;
	}

	/**
	 * Set tile as an obstacle
	 * @param bool If set to true, tile is an obstacle
	 */
	public void setObstacle(boolean bool) {
		this.obstacle = bool;
	}

	/**
	 * Set tile as a virtual wall (i.e. tile cannot be traversed by centre coordinate of Robot)
	 * @param bool If set to true, tile is a virtual wall
	 */
	public void setVirtualWall(boolean bool) {
		if (this.persistent) return;

		else if (bool) {
			obStacles++;
			virtualWall = bool;
		} else {
			obStacles--;
			if(obStacles == 0) virtualWall = bool;
		}
	}

	/**
	 * Set tile as persistent
	 * @param bool If set to true, tile is persistent
	 */
	void setPersistent(boolean bool) {
		persistent = bool;
	}

	/**
	 * Set tile as waypoint
	 * @param bool If set to true, tile is waypoint
	 */
	public void setMidPoint(boolean bool) {
		midPoint = bool;
	}


	// Other Function(s)
	/**
	 * Reset all values, except persistent virtual walls
	 */
	public void reset() {
		if (!persistent) virtualWall = false;
		explored = false;
		obstacle = false;
		midPoint = false;
	}


}