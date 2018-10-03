package map;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.*;
import javax.swing.border.Border;

import map.*;
import map.Map;
import map.Tile;
import map.GraphicConstant;

import robot.Robot;
import robot.RobotConstant;

public class MapUI extends JPanel{
	
	private Map map;
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
    
    
    	       
    public MapUI(Robot bot) {

    	this.bot=bot;
    	this.map = new Map(bot);
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
                                    if (map.isObstacleTile(gridRow+i, gridCol+j)){
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
                        	map.setObstacleTile(gridRow, gridCol, false); //clear obstacle if Ctrl is pressed
                        } 
                        else {
                            map.setObstacleTile(gridRow, gridCol, true);  //else, set as obstacle
                        }
                    }
                    //
                    repaint();
                    System.out.println(utility.MapDescriptor.generateMapString(map));
                }
            }
        });
    	
    }   
    
    /*
     * 
     * 
     * 
     */
    
    public Map getMap(){
    	return this.map;
    }
    /*
     * 
     * 
     */
    
    private ColorTile[][] mapColorTiles = null;
    
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
    
    /*
     * 
     * 
     * 
     */
    


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
    
    
    
   /*
    * 
    * 
    * 
    * (non-Javadoc)
    * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
    */

    public void paintComponent(Graphics g) {
 
        if (!_bMeasured) {
            System.out.println("Map width: " + _mapWidth + ", Map height: " + _mapWidth);

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
                } else if (map.isObstacleTile(mapRow, mapCol)) {
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
        g.fillOval((c - 1) * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_X_OFFSET + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - (r * GraphicConstant.TILE_SIZE + GraphicConstant.ROBOT_Y_OFFSET), GraphicConstant.ROBOT_W, GraphicConstant.ROBOT_H);

        // Paint the robot's direction indicator on-screen.
        g.setColor(GraphicConstant.C_ROBOT_DIR);
        RobotConstant.DIRECTION d = bot.getRobotDir();
        switch (d) {
            case UP:
                g.fillOval(c * GraphicConstant.TILE_SIZE + 10 + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - r * GraphicConstant.TILE_SIZE - 15, GraphicConstant.ROBOT_DIR_W, GraphicConstant.ROBOT_DIR_H);
                break;
            case RIGHT:
                g.fillOval(c * GraphicConstant.TILE_SIZE + 35 + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - r * GraphicConstant.TILE_SIZE + 10, GraphicConstant.ROBOT_DIR_W, GraphicConstant.ROBOT_DIR_H);
                break;
            case DOWN:
                g.fillOval(c * GraphicConstant.TILE_SIZE + 10 + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - r * GraphicConstant.TILE_SIZE + 35, GraphicConstant.ROBOT_DIR_W, GraphicConstant.ROBOT_DIR_H);
                break;
            case LEFT:
                g.fillOval(c * GraphicConstant.TILE_SIZE - 15 + GraphicConstant.MAP_X_OFFSET, GraphicConstant.MAP_H - r * GraphicConstant.TILE_SIZE + 10, GraphicConstant.ROBOT_DIR_W, GraphicConstant.ROBOT_DIR_H);
                break;
        }
    }
   

    public void clearMap() {
        for (int row = 0; row < (MapConstant.MAP_ROWS); row++) {
            for (int col = 0; col < (MapConstant.MAP_COLS); col++) {
                map.setObstacleTile(row, col, false);;
            }
        }
        this.repaint();
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
