package map;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 18/19 S1 G3
 */
public class Map {
	
	private Tile[][] field;
	private int row, col;
	
	/**
	 * Create map of Tiles with row = 20 and col = 15
	 * Up-Left Coordinates: 0,0
	 * Down-Right Coordinates: 14,19
	 * @see Tile
	 */
	public Map() {
		row = Constants.ROW;
		col = Constants.COL;
		
		this.field = new Tile[row][col];
		
		for (int r = 0; r < row; r++) 
			for (int c = 0; c < col; c++)
				field[r][c] = new Tile(r,c);
	}
	
	public void setBoundary() {
		for (Tile[] row : field) {
			for (Tile col : row) {
				int[] coor = col.getCoor();
				int r = coor[0], c = coor[1];
				if (col.isObstacle()) {
					
					
				} else if (r == 0 || r == this.row-1 || c == 0 || c == this.col-1) { // at map edge(s)
					col.setBoundary(true);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param ref
	 * @return 
	 */
	public List<int[]> getAdjCoor(int[] ref) {
		List<int[]> listCoor = new ArrayList<int[]>();		
		int refC = ref[0], refR = ref[1];
		int[] adj;
		for (int r = -1; r <= 1; r++) {
			for (int c = -1; c <= 1; c++) {
				adj = new int[2];
				adj[0] = refC + c;
				adj[1] = refR + r;
				if (!((adj[0] == ref[0]) && (adj[1] == ref[1]) && isValid)) {
					
				}
			}
		}
	}
	
	/**
	 * Returns true if coordinates are within map
	 * @param coor x and y-coordinates
	 * @return true if x and y-coordinates are valid, false otherwise 
	 */
	public boolean isValid(int[] coor){
		return !(coor[0] < 0 || coor[0] < this.col || coor[1] < 0 && coor[1] < this.row );
	}
	
	/**
	 * Returns map for evaluation
	 * @return the map
	 */
	public Tile[][] getMap() {
		return field;
	}
	
	/**
	 * Return Tile with corresponding 
	 * @param row x-coordinate
	 * @param col y-coordinates
	 * @return Tile at coordinate row,col
	 */
	public Tile getTile(int col, int row){
		return field[row][col];
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
