package map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;

import java.util.List;
import javax.swing.*;

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
	//private Tile[][] mapTiles;
	private ColorTile[][] mapColorTiles = null;
	private static int row = MapConstant.MAP_ROWS, col = MapConstant.MAP_COLS ;
	private Robot bot;
	
	// For measuring size of the canvas
    private boolean _bMeasured = false;
    private boolean _bSetMid = false;

    // Size of the tile
    private int _mapLength = 0;
    private int _mapWidth = 0;
    
 // Mid Point TODO set mid point must be dynamic
    public int midRow = -5;
    public int midCol = -5;
	
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
				if (r == 0 || r == (row - 1) || c == 0 || c == (col -1)) 
					field[r][c].setVirtualWall(true);
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
                
                if (_bSetMid) {
                    if ((gridRow < MapConstant.MAP_ROWS && gridRow + 1 < MapConstant.MAP_ROWS && gridRow + 2 < MapConstant.MAP_ROWS)
                            && (gridCol < MapConstant.MAP_COLS && gridCol + 1 < MapConstant.MAP_COLS && gridCol + 2 < MapConstant.MAP_COLS)) {
                        if (bControlDown) {
                        } 
                        else {
                            boolean midPointAllowed = false;
                            //TODO change for values
                            for(int i = 0; i < 3; i++){
                                for(int j = 0; j < 3; j++){
                                    if (isObstacleTile(gridRow+i, gridCol+j)){
                                        System.out.println("You cannot set the mid point on an obstacle");
                                        midPointAllowed = false;
                                        return;
                                    } 
                                    else if(isStartZone(gridRow+i,gridCol+j) || isGoalZone(gridRow+i,gridCol+j)){
                                        System.out.println("You cannot set the mid point on start/goal zone");
                                        midPointAllowed = false;
                                        return;
                                    } 
                                    else {
                                        midPointAllowed = true;
                                    }
                                }
                            }
                            if(midPointAllowed)
                                addMidPoint(gridRow, gridCol);
                        }
                    }
                } 
                else {
                    if (Map.isValidTile(gridRow, gridCol)) {
                        if (bControlDown) {
                        	setObstacleTile(gridRow, gridCol, false); //clear obstacle if Ctrl is pressed
                        } 
                        else {
                            setObstacleTile(gridRow, gridCol, true);  //else, set as obstacle
                        }
                    }
                    //
                    repaint();
                }
            }
        });
		
	}
	
	// Getters
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Tile getTile(int row, int col){
		return field[row][col];
	}
	
	// Setters
	// TODO setObstacle & setBoundary to merge, remove redundancies
	/**
	 * Sets Tile as an obstacles, adjacent Tile(s) are set as virtualWalls 
	 * @param row
	 * @param col
	 * @param obstacle
	 */
	public void setObstacleTile(int row, int col,boolean obstacle) {
		field[row][col].setObstacle(obstacle);
		for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				if (!((i == 0) && (j == 0)) && isValidTile(row+i,col+j)) field[row+i][col+j].setVirtualWall(true);
	}

	// TODO account for format difference in getAdjCoor (i.e. [row,col] instead of [col,row])
	/*
	public void setBoundary() {
		for (Tile[] row : field) 
			for (Tile tile : row) {
				// reset virtualWall settings
				tile.setVirtualWall(false); 
				
				// get coordinates
				int x = tile.getRow(), y = tile.getCol();
				
				// find adjacent coordinate and set them as virtualWall if
				// current Tile is either an obstacle or located at map boundary
				List<int[]> adjCoors = getAdjCoor(x, y);
				if (tile.isObstacle() || isBoundaryTile(x, y))
					if (!adjCoors.isEmpty()) {
						for (int[] adj : adjCoors) {
							x = adj[0]; y = adj[1];
							Tile tmp = field[y][x];
							if (!tmp.isObstacle()) tmp.setVirtualWall(true);
						}
					}
				
			}
	}*/

	// Validity Functions
	/**
	 * 
	 * @param row 
	 * @param col 
	 * @return list of integer arrays in [row,col] format (valid adjacent coordinates to passed row & col)
	 */
	public static List<int[]> getAdjCoor(int row, int col) {
		List<int[]> listCoor = new ArrayList<int[]>();
		
		int r, c;
		int[] coor = new int[2];
		
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				r = row + y;
				c = col + x;
				
				if (!((y == 0) || (x == 0))) {
					coor[0] = r;
					coor[1] = c;
					listCoor.add(coor);
				}
			}
		}
		return listCoor;
	}
	
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
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public boolean isObstacleTile(int row, int col) {
		return this.field[row][col].isObstacle();
	}
	
	/* TODO convert into row,col format
	/**
	 * Convert from map read by Reader in List of Strings form to Tile[][] map
	 * @param mapcomp List of strings that represent the map
	 
	public void parseMap(List<String> mapcomp) {
		for (int i = 0; i < this.col; i++) {
			for (int j = 0; j < this.row; j++) {
				switch (mapcomp.get(i).charAt(j)) {
					case '1':
						this.field[i][j].setObstacle(true);
						break;
					default:
						break;
				}
			}
		}
		setBoundary();
	} */
	
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
    public boolean isMidZone(int row, int col, int midRow, int midCol) {
        return (row >= midRow-1 && row <= midRow + 1 
        		&& col >= midCol-1 && col <= midCol + 1);
    }
    
    public void toggleMidPoint(){
        if(!_bSetMid){
            _bSetMid = true;
            System.out.println("Click on a map grid to set mid point.");
        } else {
            _bSetMid = false;
        }
    }
    
    public void addMidPoint(int row, int col) {
        midRow = row;
        midCol = col;
    }
    
    public int getMidIndex(){
        return (midRow * 15) + midCol;
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
                } else if (isMidZone(mapRow,mapCol, midRow, midCol)){
                    gridColor = GraphicConstant.C_MID;
                } else if (isObstacleTile(mapRow, mapCol)) {
                    gridColor = GraphicConstant.C_OBSTACLE;
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
                g.fillOval((c) * GraphicConstant.TILE_SIZE - GraphicConstant.ROBOT_X_OFFSET - 5, 
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
