package map;

import java.awt.*;

/**
 * Constants used in the Map class for rendering the arena in the simulator.
 *
 * @author 
 */

class GraphicConstant {
    public static final int TILE_LINE_WEIGHT = 2;


    public static final Color C_GRID_LINE = Color.GRAY;
    public static final Color C_START = Color.BLUE;
    public static final Color C_MID = Color.YELLOW;
    public static final Color C_GOAL = Color.GREEN;
    public static final Color C_UNEXPLORED = Color.LIGHT_GRAY;
    public static final Color C_FREE = Color.WHITE;
    public static final Color C_OBSTACLE = Color.BLACK;

    public static final Color C_ROBOT = Color.RED;
    public static final Color C_ROBOT_DIR = Color.WHITE;

    public static final int ROBOT_W = 70;
    public static final int ROBOT_H = 70;

    public static final int ROBOT_X_OFFSET = 10;
    public static final int ROBOT_Y_OFFSET = 10;

    public static final int ROBOT_DIR_W = 10;
    public static final int ROBOT_DIR_H = 10;

    public static final int TILE_SIZE = 30;

    public static final int MAP_H = 600;
    public static final int MAP_X_OFFSET = 120;
    
    // Prevent Instantiation
 	private GraphicConstant() {}
}
