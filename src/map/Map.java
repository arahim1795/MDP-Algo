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
		this.field = new Tile[Constants.LEN][Constants.WID];
		
		for (int i = 0; i < Constants.WID; i++) {
			for (int j = 0; j < Constants.LEN; j++) {
				field[i][j] = new Tile(i,j);
				if(i==0||j==0||i==Constants.LEN-1||j==Constants.WID){
					field[i][j].setVirtualWall(true);
				}
			}
		}
	}
	
	// returns if a tile coordinate is within bounds of the map
	public boolean isValid(int row, int col){
		return (row>=0 && row < Constants.WID && col >=0 && col < Constants.LEN );}
	
	public Tile getTile(int row, int col){
		return field[row][col];
	}
	
	//sets a tile as obstacle and surrounding tiles as virtual walls
	public void setObstacle(int row, int col){
		
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
		return map;
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
						this.map[i][j].setObstacle(true);
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
				if (map[i][j].isObstacle()) System.out.print("1");
				else System.out.print("0");
				if (j == (this.len-1)) System.out.print("\n");
			}
		}
		
	}
	

}
