/**
 * 
 */
package map;

/**
 * @author 18/19 S1 G3
 *
 */
public class Field {
	
	private Tile[][] map;
	
	/**
	 * Create map of stated sizes (len, wid) 
	 * @param len Length of map
	 * @param wid Width of map
	 * @see Tile
	 */
	public Field(int len, int wid) {
		map = new Tile[wid][len];
		
		for (int i = 0; i < wid; i++) {
			for (int j = 0; j < len; j++) {
				map[i][j] = new Tile(wid-i,j+1);
			}
		}
	}

}
