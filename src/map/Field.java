package map;

import java.util.List;

/**
 * @author 18/19 S1 G3
 */
public class Field {
	
	private Tile[][] map;
	private int wid, len;
	
	/**
	 * Create map of Tiles with default sizes (len = 15, wid = 20)
	 * @see Tile
	 */
	public Field() {
		this.wid = Constants.WID;
		this.len = Constants.LEN;
		
		map = new Tile[wid][len];
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < len; j++) {
				map[i][j] = new Tile(wid-i,j+1);
			}
		}
	}
	
	/**
	 * Create map of stated sizes (len, wid) 
	 * @param len Length of map
	 * @param wid Width of map
	 * @see Tile
	 */
	public Field(int len, int wid) {
		this.wid = wid;
		this.len = len;
		map = new Tile[this.wid][this.len];
		
		for (int i = 0; i < this.wid; i++) {
			for (int j = 0; j < this.len; j++) {
				map[i][j] = new Tile(this.wid-i,j+1);
			}
		}
	}

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
