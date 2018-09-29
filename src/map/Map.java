package map;

import java.util.ArrayList;
import javax.swing.*;
import java.util.List;

import utility.Utility;

/**
 * @author 18/19 S1 G3
 */
public class Map extends JPanel {
	
	private Tile[][] field;
	private int row, col;
	
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
				if(i==0||j==0||i==Constants.MAP_COLS-1||j==Constants.MAP_ROWS){
					field[i][j].setVirtualWall(true);
				}
			}
		}
	}
	
	// returns if a tile coordinate is within bounds of the map
	public boolean isValid(int row, int col){
		return (row>=0 && row < Constants.MAP_ROWS && col >=0 && col < Constants.MAP_COLS );}
	
	
	public Tile getTile(int row, int col){
		return field[row][col];
	}
	
	//sets a tile as obstacle and surrounding tiles as virtual walls
	public void setObstacle(int row, int col,boolean obstacle){
		for(int i = -1;i<2;i++){
			for(int j=-1;j<2;j++){
				if(i!=0 || j!=0){
					this.field[row+i][col+j].setVirtualWall(obstacle);
				}
			}
		}
		return;

		row = Constants.ROW;
		col = Constants.COL;
		
		this.field = new Tile[row][col];
		
		for (int y = 0; y < row; y++) 
			for (int x = 0; x < col; x++)
				field[y][x] = new Tile(y,x);
	}
		
	
	/**
	 * 
	 */
	public void setBoundary() {
		for (Tile[] row : this.field) 
			for (Tile tile : row) {
				// reset virtualWall settings
				tile.setVirtualWall(false); 
				
				// get coordinates
				int[] coor = tile.getCoor();
				int x, y;
				
				// find adjacent coordinate and set them as virtualWall if
				// current Tile is either an obstacle or located at map boundary
				List<int[]> adjCoors = Utility.getAdjCoor(coor);
				if (tile.isObstacle() || isBoundaryTile(coor))
					if (!adjCoors.isEmpty()) {
						for (int[] adj : adjCoors) {
							x = adj[0]; y = adj[1];
							Tile tmp = this.field[y][x];
							if (!tmp.isObstacle()) tmp.setVirtualWall(true);
						}
					}
				
			}
	}

	/**
	 * 
	 * @param coor
	 * @return
	 */
	private boolean isBoundaryTile(int[] coor) {
		int x = coor[0], y = coor[1];
		return x == 0 || x == this.row-1 || y == 0 || y == this.col-1;
	}
	
	/**
	 * Returns map for evaluation
	 * @return the map
	 */

	public Tile[][] getMap() {
		return this.field;
  }
	
	/**
	 * Return Tile with corresponding 
	 * @param row x-coordinate
	 * @param col y-coordinates
	 * @return Tile at coordinate row,col
	 */
	public Tile getTile(int[] coor){
		return this.field[coor[1]][coor[2]];
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

						this.field[i][j].setObstacle();

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
		for (int i = 0; i < this.row; i++) {
			for (int j = 0; j < this.col; j++) {
				if (field[i][j].isObstacle()) System.out.print("1");
				else System.out.print("0");
				if (j == (this.col-1)) System.out.print("\n");
			}
		}
		
	}
	
	/**
	 * 
	 */
	public void resetMap() {
		for (Tile[] row : this.field)
			for (Tile tile : row)
				tile.reset();
	}
	
	

}
