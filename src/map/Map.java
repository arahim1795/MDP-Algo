package map;

import java.util.List;

/**
 * @author 18/19 S1 G3
 */
public class Map {
	
	private Tile[][] field;
	
	/**
	 * Create map of Tiles with default sizes (len = 15, wid = 20)
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
	}
		
	
	
	
	/*  DEBUG PURPOSES
	 * 
	 * 
	 * 
	 */
	 
	/**
	 * Returns map for evaluation
	 * @return the map
	 */
	public Tile[][] getMap() {
		return this.field;
	}
	
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapcomp List of strings that represent the map
	 */
	public void parseMap(List<String> mapcomp) {
		for (int i = 0; i < this.wid; i++) {
			for (int j = 0; j < this.len; j++) {
				switch (mapcomp.get(i).charAt(j)) {
					case '1':
						this.field[i][j].setObstacle(true);
						break;
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
		for (int i = 0; i < this.wid; i++) {
			for (int j = 0; j < this.len; j++) {
				if (field[i][j].isObstacle()) System.out.print("1");
				else System.out.print("0");
				if (j == (this.len-1)) System.out.print("\n");
			}
		}
		
	}
	

}
