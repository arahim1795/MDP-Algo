package map;

import java.util.ArrayList;
import java.util.List;

import utility.Utility;

/**
 * @author 18/19 S1 G3
 */
public class Map {
	
	private Tile[][] field;
	private static int col = Constants.MAP_ROWS, row = Constants.MAP_ROWS ;
	
	// Constructor
	/**
	 * Create map of Tiles with row = 20 and col = 15
	 * Up-Left Coordinates: 0,0
	 * Down-Right Coordinates: 14,19
	 * @see Tile
	 */
	public Map() {

		this.field = new Tile[Constants.MAP_COLS][Constants.MAP_ROWS];
		
		for (int i = 0; i < Constants.MAP_ROWS; i++) {
			for (int j = 0; j < Constants.MAP_COLS; j++) {
				field[i][j] = new Tile(i,j);
				if(i==0 || j==0 || i==Constants.MAP_COLS-1 || j==Constants.MAP_ROWS)
					field[i][j].setVirtualWall(true);
			}
		}
	}
	
	// Getters
	/**
	 * 
	 * @param col
	 * @param row
	 * @return
	 */
	public Tile getTile(int col, int row){
		return field[row][col];
	}
	
	// Setters
	// TODO setObstacle & setBoundary to merge, remove redundancies
	/**
	 * Sets Tile as an obstacles, adjacent Tile(s) are set as virtualWalls 
	 * @param row
	 * @param col
	 * @param obstacle
	 */
	public void setObstacleTile(int col, int row,boolean obstacle) {
		this.field[row][col].setObstacle(obstacle);
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(i != 0 || j != 0) field[row+i][col+j].setVirtualWall(true);
	}

	/**
	 * 
	 */
	public void setBoundary() {
		for (Tile[] row : field) 
			for (Tile tile : row) {
				// reset virtualWall settings
				tile.setVirtualWall(false); 
				
				// get coordinates
				int[] coor = tile.getCoor();
				int x = coor[0], y = coor[1];
				
				// find adjacent coordinate and set them as virtualWall if
				// current Tile is either an obstacle or located at map boundary
				List<int[]> adjCoors = getAdjCoor(x, y);
				if (tile.isObstacle() || isBoundaryTile(x, y))
					if (!adjCoors.isEmpty()) {
						for (int[] adj : adjCoors) {
							x = adj[0]; y = adj[1];
							Tile tmp = field[y][x];
							if (!tmp.isObstacle()) tmp.setVirtualWall(true);
						}
					}
				
			}
	}

	// Validity Functions
	/**
	 * Returns a list of valid adjacent coordinates
	 * @param ref Grid coordinate
	 * @return list of valid adjacent coordinates to the passed-in coordinates
	 */
	public static List<int[]> getAdjCoor(int refx, int refy) {
		List<int[]> listCoor = new ArrayList<int[]>();
		int[] adj;
		for (int r = -1; r <= 1; r++) {
			for (int c = -1; c <= 1; c++) {
				adj = new int[2];
				adj[0] = refx + c;
				adj[1] = refy + r;
				if ((!(adj[0] == refx) || !(adj[1] == refy)) && isValid(adj[0], adj[1])) {
					listCoor.add(adj);
				}
			}
		}
		return listCoor;
	}
	
	/**
	 * Returns true if coordinates are within map
	 * @param coor x and y-coordinates
	 * @return true if x and y-coordinates are valid, false otherwise
	 */
	public static boolean isValidTile(int row, int col){
		return row > 0 && row < Constants.MAP_ROWS && col > 0 && col < Constants.MAP_COLS;
	}
	
	/**
	 * 
	 * @param coor
	 * @return
	 */
	public static boolean isBoundaryTile(int col, int row) {
		return col == 0 || col == Constants.MAP_COLS || row == 0 || row == Constants.MAP_ROWS;
	}
	
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapcomp List of strings that represent the map
	 */
	public void parseMap(List<String> mapcomp) {
		for (int i = 0; i < this.col; i++) {
			for (int j = 0; j < this.row; j++) {
				switch (mapcomp.get(i).charAt(j)) {
					case '1':
						this.field[i][j].setObstacle(true);
						break;
					default:
						break;
				}
			}
		}
		setBoundary();
	}
	
	/**
	 * Prints map to console for debugging purposes
	 */
	public void printMap() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				if (field[i][j].isObstacle()) System.out.print("1");
				else System.out.print("0");
				if (j == (col-1)) System.out.print("\n");
			}
		}
		
	}
	
	/**
	 * 
	 */
	public void update() {
		
	}
	
	/**
	 * 
	 */
	public void reset() {
		for (Tile[] row : this.field)
			for (Tile tile : row)
				tile.reset();
	}
	

}
