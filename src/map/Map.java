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
	private static int row = MapConstant.MAP_ROWS, col = MapConstant.MAP_COLS ;
	
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
	
	// Setters
	// TODO setObstacle & setBoundary to merge, remove redundancies
	/**
	 * Sets Tile as an obstacles, adjacent Tile(s) are set as virtualWalls 
	 * @param row
	 * @param col
	 * @param obstacle
	 */
	public void setObstacleTile(int row, int col,boolean obstacle) {
		field[row][col].setObstacle(obstacle);
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				if (!((i == 0) && (j == 0))) field[row+i][col+j].setVirtualWall(true);
	}

	// TODO account for format difference in getAdjCoor (i.e. [row,col] instead of [col,row])
	/*
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
	}*/

	// Validity Functions
	/**
	 * 
	 * @param row 
	 * @param col 
	 * @return list of integer arrays in [row,col] format (valid adjacent coordinates to passed row & col)
	 */
	public static List<int[]> getAdjCoor(int row, int col) {
		List<int[]> listCoor = new ArrayList<int[]>();
		
		int r, c;
		int[] coor = new int[2];
		
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				r = row + y;
				c = col + x;
				
				if (!((y == 0) || (x == 0))) {
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
	
	/* TODO convert into row,col format
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapcomp List of strings that represent the map
	 
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
	} */
	
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
