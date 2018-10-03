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
	private Tile[][] field = null;
	public static int row = MapConstant.MAP_ROWS, col = MapConstant.MAP_COLS ;
	
	// Constructor
	/**
	 * Create map of Tiles with col = 15 and row = 15
	 * Up-Left Coordinates: 0,0
	 * Down-Right Coordinates: 14,19
	 * @see Tile
	 */
	public Map() {

		field = new Tile[row][col];
		
		for (int r = 0; r < row; r++)
			for (int c = 0; c < col; c++) {
				field[r][c] = new Tile(r,c);
				if (r == 0 || r == (row - 1) || c == 0 || c == (col -1)) 
					field[r][c].setVirtualWall(true);
			}
	}
	
	// Getters
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Tile getTile(int row, int col){
		return field[row][col];
	}
	
	
	// Getter(s)
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isObstacleTile(int row, int col) {
		return field[row][col].isObstacle();
	}
	
	
	// Setter(s)
	/**
	 * Sets Tile as an obstacles, adjacent Tile(s) are set as virtualWalls 
	 * @param row
	 * @param col
	 * @param bool
	 */
	public void setObstacleTile(int row, int col, boolean bool) {
		Tile tile = field[row][col];
		List<int[]> adjCoor = getAdjCoor(row,col);
		
		if (!adjCoor.isEmpty())
			for (int[] coor : adjCoor)
				field[coor[0]][coor[1]].setVirtualWall(bool);
		else System.out.println("No valid adjacent Tile");		
	}

	// Validity Functions
	/**
	 * 
	 * @param row 
	 * @param col 
	 * @return list of integer arrays in [row(y),col(x)] format (valid adjacent coordinates to
	 * passed row(x) & col(y))
	 */
	public static List<int[]> getAdjCoor(int row, int col) {
		List<int[]> listCoor = new ArrayList<int[]>();
		
		int r, c;
		int[] coor = new int[2];
		
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				r = row + y;
				c = col + x;
				
				if (!((y == 0) || (x == 0)) && isValidTile(r,c)) {
					coor[0] = r;
					coor[1] = c;
					listCoor.add(coor);
				}
			}
		}
		return listCoor;
	}
	
	/**
	 * 
	 * @param checkRow row(y)-coordinates
	 * @param checkCol col(x)-coordinates
	 * @return true if row(y) and col(x)-coordinates are within 0 and maxRow(19) or maxCol(14)
	 * respectively, false otherwise
	 */
	public static boolean isValidTile(int checkRow, int checkCol){
		return checkRow > 0 && checkRow < row && checkCol > 0 && checkCol < col;
	}
	
	/**
	 * 
	 * @param checkRow row(y)-coordinates
	 * @param checkCol col(x)-coordinates
	 * @return true if either row(y) and col(x)-coordinates are 0 or at maxRow(19) or maxCol(14)
	 * respectively, false otherwise
	 */
	public static boolean isBoundaryTile(int checkRow, int checkCol) {
		return checkRow == 0 || checkRow == (row - 1) || checkCol == 0 || checkCol == (col - 1);
	}
	
	// Other Function(s)
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapStr Map represented by a series of String
	 */
	public void parseMap(List<String> mapStr) {
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < col; c++) {
				switch (mapStr.get(r).charAt(c)) {
					case '1':
						field[r][c].setObstacle(true);
					default:
						break;
				}
			}
		}
	}
	
	/**
	 * Prints map to console for debugging purposes
	 */
	public void printMap() {
		for (int r = 0; r < row; r++) 
			for (int c = 0; c < col; c++) {
				if (field[r][c].isObstacle()) System.out.print("1");
				else System.out.print("0");
				if (c == (col-1)) System.out.print("\n");
			}
	}
	
	/**
	 * 
	 */
	public void reset() {
		for (Tile[] row : field)
			for (Tile tile : row)
				tile.reset();
	}
	
	/**
	 * 
	 */
	public void setAllExplored() {
		for(Tile[] row : field)
			for (Tile tile : row)
				tile.setExplored(true);
	}
	

}
