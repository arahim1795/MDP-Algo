package map;

/**
 * @author 18/19 S1 G3
 */
public class Tile {

	private boolean explored = false;

	private boolean virtualWall= false;
	private boolean obstacle = false;
	private boolean persistent = false;
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
	
	public int getRow(){
		return coor[0];
	}
	
	public int getCol(){
		return coor[1];
	}
	/**
	 * Returns true is Tile is marked as an obstacle
	 * @return true if Tile is set as an obstacle, false otherwise
	 */
	public boolean isObstacle() {
		return this.obstacle;
	}

	/**
	 * Set Tile as an obstacle
	 * @param obstacle If set to true, tile is set as obstacle
	 */
	public void setObstacle(boolean obstacle) {
		this.obstacle = true;
	}

	/**
	 * Returns true if Tile has been explored by Robot
	 * @return true if Tile has been explored, false otherwise
	 * @see robot.Robot
	 */
	public boolean isExplored() {
		return explored;
	}

	/**
	 * Set Tile as explored by robot
	 * @param explored If set to true, tile has been explored by Robot
	 * @see robot.Robot
	 */
	public void setExplored() {
		this.explored = true;
	}
	
	/**
	 * Returns true if this Tile is bounded by adjacent obstacle(s) or map edge(s)
	 * @return true if Tile is a boundary Tile, false otherwise
	 */
	public boolean isVirtualWall() {
		return this.virtualWall;
	}

  
  /**
	 * Set Tile as a virtual wall (i.e. Tile must not be traversed by robot)
	 * @param virtualWall If set to true, Tile is set as virtual wall
	 */
  
	public void setVirtualWall(boolean virtualWall) {
		if(!virtualWall && this.persistent){return;}
		this.virtualWall = virtualWall;

	}
	
	/**
	 * Return true if Tile is persistent
	 * @return true if Tile is a persistent Tile, false otherwise
	 */
	public boolean isPersistent() {
		return this.persistent;
	}
	
	/**
	 * Set Tile as a persistent Tile
	 * @param persistent If set to true, Tile is set as persistent
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}

	/**
	 * Return an integer array of Tile coordinates (i.e. assigned coordinates,
	 * not with respect to the array index of the Tile[][])
	 * @return an array of coordinates [c,r]
	 */
	public int[] getCoor() {
		return coor;
	}
	
	public void reset() {
		this.explored = false;
		this.obstacle = false;
		this.virtualWall = false;
		this.persistent = false;
	}

	
}
