package map;

/**
 * @author 18/19 S1 G3
 */
public class Tile {

	private int col;
	private int row;
	
	private boolean explored = false;
	private boolean obstacle = false;
	private boolean virtualWall = false;
	private boolean persistent = false;
	
	// Constructor
	/**
	 * Tiles of field
	 * @param row row(y)-coordinate of Tile
	 * @param col col(x)-coordinate of Tile
	 */
	public Tile(int row, int col) {
		this.col = col;
		this.row = row;
	}
	
	// Getter(s)
	/**
	 * 
	 * @return
	 */
	public int getRow() {
		return row;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCol() {
		return col;
	}
	
	/**
	 * Returns true is Tile is an obstacle
	 * @return true if Tile is an obstacle, false otherwise
	 */
	public boolean isObstacle() {
		return obstacle;
	}
	
	/**
	 * Returns true is Tile is explored
	 * @return true if Tile is explored, false otherwise
	 */
	public boolean isExplored() {
		return explored;
	}
	
	/**
	 * Returns true if Tile is a virtual wall
	 * @return true if Tile is a virtual wall, false otherwise
	 */
	public boolean isVirtualWall() {
		return virtualWall;
	}

	/**
	 * Returns true if Tile is persistent
	 * @return true if Tile is persistent, false otherwise
	 */
	public boolean isPersistent() {
		return persistent;
	}
	
	
	// Setter(s)
	
	/**
	 * Set Tile as explored
	 * @param bool If set to true, tile is explored
	 */
	public void setExplored(boolean bool) {
		explored = bool;
	}
	
	
	/**
	 * Set Tile as an obstacle
	 * @param bool If set to true, tile is an obstacle
	 */
	public void setObstacle(boolean bool) {
		this.obstacle = bool;
	}
	
	/**
	 * Set Tile as a virtual wall (i.e. tile cannot be traversed by centre-coordinate of Robot)
	 * @param bool If set to true, Tile is a virtual wall
	 */
	public void setVirtualWall(boolean bool) {
		if (!bool && this.persistent) {return;}
		virtualWall = bool;
	}

	/**
	 * Set Tile as a persistent Tile
	 * @param bool If set to true, Tile is persistent
	 */
	void setPersistent(boolean bool) {
		persistent = bool;
	}
	
	
	// Other Function(s)
	/**
	 * 
	 */
	public void reset() {
		explored = false;
		obstacle = false;
		virtualWall = false;
		persistent = false;
	}

	
}