package map;

import java.util.ArrayList;
import javax.swing.*;
import java.util.List;
import javax.swing.*;

import javax.swing.JPanel;

import utility.Utility;
import javax.swing.*;

/**
 * @author 18/19 S1 G3
 * getTile
 * setObstacleTile
 * setBoundary
 * getAdjCoor
 * 
 */
public class Map extends JPanel{
	private Tile[][] field;
	private static int col = MapConstant.MAP_ROWS, row = MapConstant.MAP_ROWS ;
	
	// Constructor
	/**
	 * Create map of Tiles with row = 20 and col = 15
	 * Up-Left Coordinates: 0,0
	 * Down-Right Coordinates: 14,19
	 * @see Tile
	 */
	public Map() {

		this.field = new Tile[MapConstant.MAP_ROWS][MapConstant.MAP_COLS];
		
		for (int i = 0; i < MapConstant.MAP_ROWS; i++) {
			for (int j = 0; j < MapConstant.MAP_COLS; j++) {
				field[i][j] = new Tile(i,j);
				if(i==0 || j==0 || i==MapConstant.MAP_ROWS-1 || j==MapConstant.MAP_COLS)
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
	public Tile getTile(int row, int col){
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
	public void setObstacleTile(int row, int col,boolean obstacle) {
		this.field[row][col].setObstacle(obstacle);
		/*
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if((i != 0 || j != 0) && isValidTile(row+i, col+j)) field[row+i][col+j].setVirtualWall(true);
	*/
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
				int x = tile.getRow(), y = tile.getCol();
				
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
				if ((!(adj[0] == refx) || !(adj[1] == refy)) && isValidTile(adj[0], adj[1])) {
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
		return row >= 0 && row <= MapConstant.MAP_ROWS && col >= 0 && col <= MapConstant.MAP_COLS;
	}
	
	/**
	 * 
	 * @param coor
	 * @return
	 */
	public static boolean isBoundaryTile(int row, int col) {
		return col == 0 || col == MapConstant.MAP_COLS || row == 0 || row == MapConstant.MAP_ROWS;
	}
	public boolean isObstacleTile(int row, int col) {
		return this.field[row][col].isObstacle();
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
	public void reset() {
		for (Tile[] row : this.field)
			for (Tile tile : row)
				tile.reset();
	}
	
	public void setAllExplored(){
		for(Tile[] row : this.field){
			for (Tile tile : row){
				tile.setExplored(true);
			}
		}
	}
	

}
