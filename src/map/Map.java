package map;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;


import java.util.List;
import robot.Robot;
import robot.RobotConstant;

import utility.Utility;
import utility.MapDescriptor;

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
	private ColorTile[][] mapColorTiles = null;
	public static int row = MapConstant.MAP_ROWS, col = MapConstant.MAP_COLS;
	private Robot bot;
	
	// For measuring size of the canvas
    private boolean _bMeasured = false;
    private boolean _bSetMid = false;

    // Size of the tile
    private int _mapLength = 0;
    private int _mapWidth = 0;

    
    
	// Constructor
	/**
	 * Create map of Tiles with col = 15 and row = 15
	 * Up-Left Coordinates: 0,0
	 * Down-Right Coordinates: 14,19
	 * @see Tile
	 */
	public Map(Robot bot) {

		field = new Tile[row][col];
		this.bot = bot;
		
		for (int r = 0; r < row; r++)
			for (int c = 0; c < col; c++) {
				field[r][c] = new Tile(r,c);
				// Set Tiles at Map boundaries as persistent virtual wall
				if (r == 0 || r == (row - 1) || c == 0 || c == (col -1)) {
					field[r][c].setVirtualWall(true);
					field[r][c].setPersistent(true);
				}
			}

		//adds mouseListener for graphical rendering
		
		this.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e) {

                boolean bControlDown = e.isControlDown(); //if Control key is pressed

                int mouseClickX = e.getX();
                int mouseClickY = e.getY();

                int gridRow = mouseClickY / GraphicConstant.TILE_SIZE;
                int gridCol = mouseClickX / GraphicConstant.TILE_SIZE;
                System.out.println("(" + gridRow + "," + gridCol + ")");
                

                    if (Map.isValidTile(gridRow, gridCol)) {
                        if (bControlDown) {
                        	setObstacleTile(gridRow, gridCol, false); //clear obstacle if Ctrl is pressed
                        } 
                        else {
                            setObstacleTile(gridRow, gridCol, true);  //else, set as obstacle
                        }
                    }
                    //
                    repaint();}
        });
		
	}
	
	// Getter(s)
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Tile getTile(int row, int col){
		return field[row][col];
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isObstacleTile(int row, int col) {
		return field[row][col].isObstacle();
	}
	
	// Setter(s)
	/**
	 * Sets Tile as an obstacles, adjacent Tile(s) are set as virtual walls, except for
	 * persistent Tile(s)
	 * @param row
	 * @param col
	 * @param bool
	 * @return
	 */
	public void setObstacleTile(int row, int col, boolean bool) {
		if (isObstacleTile(row,col) == bool) return;
		field[row][col].setObstacle(bool);
		
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				if (!((i == 0) && (j == 0)) && isValidTile(row+i,col+j)) field[row+i][col+j].setVirtualWall(bool);

	}

	// Validity Functions
	/**
	 * 
	 * @param checkRow row(y)-coordinates
	 * @param checkCol col(x)-coordinates
	 * @return true if row(y) and col(x)-coordinates are within 0 and maxRow(19) or maxCol(14)
	 * respectively, false otherwise
	 */
	public static boolean isValidTile(int checkRow, int checkCol){
		return checkRow >= 0 && checkRow < row && checkCol >= 0 && checkCol < col;
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
	
	
	// Other Function(s)
	/**
	 * 
	 * @param row 
	 * @param col 
	 * @return list of integer arrays in [row(y),col(x)] format (valid adjacent coordinates to
	 * passed row(x) & col(y))
	 */
	public static List<int[]> getAdjCoor(int row, int col) {
		List<int[]> listCoor = new ArrayList<int[]>();
		
		int r, c;
		
		
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				r = row + y;
				c = col + x;
				
				if (!((y == 0) && (x == 0)) && isValidTile(r,c)) {
					int[] coor = new int[2];
					coor[0] = r;
					coor[1] = c;
					listCoor.add(coor);
				}
			}
		}
		return listCoor;
	}
	
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapStr Map represented by a series of String
	 */
	public Map parseMap(List<String> mapStr) {
		Map map = new Map(bot);
		for (int r = 0; r < row; r++) 
			for (int c = 0; c < col; c++) {
				Tile tile = map.getTile(r, c);
				switch (mapStr.get(r).charAt(c)) {
					case '1':
						tile.setObstacle(true);
					default:
						break;
				}
			}
		
		return map;
	}
	
	// check if given tile is in start zone
    public boolean isStartZone(int row, int col) {
    	return ((row >= (MapConstant.START_GRID_ROW-1)) && (row <= (MapConstant.START_GRID_ROW +1)) && 
    			(col >= (MapConstant.START_GRID_COL-1)) && (col <= (MapConstant.START_GRID_COL +1)));
	}
    
    // check if given tile is in goal zone
    public boolean isGoalZone(int row, int col) {
    	return ((row >= (MapConstant.GOAL_GRID_ROW-1)) && (row <= (MapConstant.GOAL_GRID_ROW +1)) 
    			&& (col >= (MapConstant.GOAL_GRID_COL-1)) && (col <= (MapConstant.GOAL_GRID_COL +1)));
	}
    
    public boolean isVirtualWall(int row, int col){
    	return(this.field[row][col].isVirtualWall());
    }
	
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
	
	
	public void paintComponent(Graphics g) {
		 
        if (!_bMeasured) {
        	//TODO dummy debug
            System.out.println("Map width: " + _mapWidth + ", Map height: " + _mapWidth);
            System.out.println(bot.getRobotRow()+","+bot.getRobotCol());
            System.out.println((bot.getRobotCol()+2) * GraphicConstant.TILE_SIZE - GraphicConstant.ROBOT_X_OFFSET - 5);
            // Calculate the map grids for rendering
            
            mapColorTiles = new ColorTile [MapConstant.MAP_ROWS][MapConstant.MAP_COLS];
            for (int mapRow = 0; mapRow < MapConstant.MAP_ROWS; mapRow++) {
                for (int mapCol = 0; mapCol < MapConstant.MAP_COLS; mapCol++) {
                    mapColorTiles [mapRow][mapCol] = new ColorTile (mapCol * GraphicConstant.TILE_SIZE, mapRow * GraphicConstant.TILE_SIZE, GraphicConstant.TILE_SIZE);
                }
            }

            _bMeasured = true;
        }

        // Clear the map
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, _mapWidth, _mapLength);

        Border border = BorderFactory.createLineBorder(GraphicConstant.C_GRID_LINE, GraphicConstant.TILE_LINE_WEIGHT);
        this.setBorder(border);

        // Paint the grids
        for (int mapRow = 0; mapRow < MapConstant.MAP_ROWS; mapRow++) {
            for (int mapCol = 0; mapCol < MapConstant.MAP_COLS; mapCol++) {
                g.setColor(GraphicConstant.C_GRID_LINE);
                g.fillRect(	mapColorTiles[mapRow][mapCol].borderX,
                        	mapColorTiles[mapRow][mapCol].borderY,
                        	mapColorTiles[mapRow][mapCol].borderSize,
                        	mapColorTiles[mapRow][mapCol].borderSize);

                Color gridColor = null;
                
                if (isStartZone(mapRow, mapCol)) {
                    gridColor = GraphicConstant.C_START;
                } else if (isGoalZone(mapRow, mapCol)) {
                    gridColor = GraphicConstant.C_GOAL;
                } else if (isObstacleTile(mapRow, mapCol)) {
                    gridColor = GraphicConstant.C_OBSTACLE;
                } else if(isVirtualWall(mapRow,mapCol)){
                	gridColor = GraphicConstant.C_VIRTUAL_WALL;
                } else {
                    gridColor = GraphicConstant.C_FREE;
                }

                g.setColor(gridColor);
                g.fillRect(mapColorTiles[mapRow][mapCol].gridX,
                        mapColorTiles[mapRow][mapCol].gridY,
                        mapColorTiles[mapRow][mapCol].gridSize,
                        mapColorTiles[mapRow][mapCol].gridSize);

            }
        } // End outer for loop	
        
        // Paint the robot on-screen.
        
        
        g.setColor(GraphicConstant.C_ROBOT);
        int r = bot.getRobotRow();
        int c = bot.getRobotCol();
        
        //TODO dummy
        //System.out.println(r + "," + c);
        //g.fillOval((c - 1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - (r * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET), GraphicConstant.ROBOT_W, GraphicConstant.ROBOT_H);
        g.fillOval(
        		(c-1) * (GraphicConstant.TILE_SIZE)+ GraphicConstant.ROBOT_X_OFFSET, 
        		(r-1) * (GraphicConstant.TILE_SIZE) + GraphicConstant.ROBOT_Y_OFFSET, 
        		GraphicConstant.ROBOT_W, 
        		GraphicConstant.ROBOT_H);
        
        // Paint the robot's direction indicator on-screen.
        g.setColor(GraphicConstant.C_ROBOT_DIR);
        RobotConstant.DIRECTION d = bot.getRobotDir();
        switch (d) {
            case UP:
                g.fillOval((c) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET, 
                		(r-1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET + 5, 
                		GraphicConstant.ROBOT_DIR_W, 
                		GraphicConstant.ROBOT_DIR_H);
                break;
            case RIGHT:
                g.fillOval((c+1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET - 5, 
                		(r-1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET + 30,
                		GraphicConstant.ROBOT_DIR_W,
                		GraphicConstant.ROBOT_DIR_H);
                break;
            case DOWN:
                g.fillOval((c) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET, 
                		(r-1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET + 55,
                		GraphicConstant.ROBOT_DIR_W, 
                		GraphicConstant.ROBOT_DIR_H);
                break;
            case LEFT:
                g.fillOval((c-1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET + 5, 
                		(r-1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET + 30, 
                		GraphicConstant.ROBOT_DIR_W, 
                		GraphicConstant.ROBOT_DIR_H);
                break;
        }
    }
	@Deprecated
	public void clearMap() {
        for (int row = 0; row < (MapConstant.MAP_ROWS); row++) {
            for (int col = 0; col < (MapConstant.MAP_COLS); col++) {
                getTile(row, col).setObstacle(false);
            }
        }
        this.repaint();
    }
	
	public Map getMap (Map map) {
		return map;
	}

    private class ColorTile {

        public int borderX;
        public int borderY;
        public int borderSize;

        public int gridX;
        public int gridY;
        public int gridSize;

        public ColorTile (int borderX, int borderY, int borderSize) {
            this.borderX = borderX;
            this.borderY = borderY;
            this.borderSize = borderSize;

            this.gridX = borderX + GraphicConstant.TILE_LINE_WEIGHT;
            this.gridY = borderY + GraphicConstant.TILE_LINE_WEIGHT;
            this.gridSize = borderSize - (GraphicConstant.TILE_LINE_WEIGHT * 2);
        }
    }

}
