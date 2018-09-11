package map;
import java.awt.Color;

/**
 * @author 18/19 S1 G3
 */
public final class Constants {

	// Grid size - for rendering only
	public static final int GRID_SIZE = 30;
	
	// Default map sizes
	public static final int WID = 20;
	public static final int LEN = 15;
        
    //Start grid information
    public static final int START_GRID_ROW = 17;
    public static final int START_GRID_COL = 0;
	
	// Goal grid information
	public static final int GOAL_GRID_ROW = 0;
	public static final int GOAL_GRID_COL = 12;
	
	public static final Color C_GRID_LINE = Color.GRAY;
	public static final int GRID_LINE_WEIGHT = 1;
	
	public static final Color C_START = Color.BLUE;
	public static final Color C_GOAL = Color.GREEN;
    public static final Color C_MID = Color.YELLOW;
	
	public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
	public static final Color C_FREE = Color.WHITE;
	public static final Color C_OBSTACLE = Color.BLACK;
	
	// Prevent instantiation
	private Constants() {}

}
