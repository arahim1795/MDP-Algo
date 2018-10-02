package map;
import java.awt.Color;

/**
 * @author 18/19 S1 G3
 */
public final class MapConstant {

	
	// Default map sizes
	public static final int MAP_ROWS = 20;
	public static final int MAP_COLS = 15;
        
	//Start grid information
	public static final int START_GRID_ROW = 18;
	public static final int START_GRID_COL = 1;
	
	// Goal Grid
	public static final int GOAL_GRID_ROW = 1;
	public static final int GOAL_GRID_COL = 13;
	
	/* TODO:MArk for deletion, moved to GraphicConstant
	public static final Color C_GRID_LINE = Color.GRAY;
	
	public static final Color C_START = Color.BLUE;
	public static final Color C_GOAL = Color.GREEN;
	public static final Color C_MID = Color.YELLOW;
	
	public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
	public static final Color C_FREE = Color.WHITE;
	public static final Color C_OBSTACLE = Color.BLACK;
	*/
	
	// Prevent Instantiation
	private MapConstant() {}

}
