package map;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.border.Border;

import map.*;
import map.Map;
import map.Tile;
import map.Constants;
public class MapUI extends Map {
	
	private Tile[][] mapTiles = new Tile [Constants.MAP_ROWS][Constants.MAP_COLS];
	
	//private Map map = new Map();
	
	// For measuring size of the canvas
    private boolean _bMeasured = false;
    private boolean _bSetMid = false;

    // Size of the tile
    private int _mapLength = 0;
    private int _mapWidth = 0;
    
    // Mid Point
    public static int midRow = Constants.START_GRID_ROW;
    public static int midCol = Constants.START_GRID_COL;
    
    private ColorTile[][] mapColorTiles = null;
    
    // check if given tile is in start zone
    public boolean isStartZone(int row, int col) {
    	return ((row >= (Constants.START_GRID_ROW)) && (row <= (Constants.START_GRID_ROW +2)) && (col >= (Constants.START_GRID_COL)) && (col <= (Constants.START_GRID_COL +2)));
	}
    
    // check if given tile is in goal zone
    public boolean isGoalZone(int row, int col) {
    	return ((row >= (Constants.GOAL_GRID_ROW)) && (row <= (Constants.GOAL_GRID_ROW +2)) && (col >= (Constants.GOAL_GRID_COL)) && (col <= (Constants.GOAL_GRID_COL +2)));
	}
    
    public boolean isMidZone(int row, int col, int midRow, int midCol) {
        return (row >= midRow && row <= midRow + 2 && col >= midCol && col <= midCol + 2);
    }
    
    
    public MapUI() {
    	super();
    	this.addMouseListener(new MouseAdapter() {
    		public void mousePressed(MouseEvent e) {

                boolean bControlDown = e.isControlDown();

                int mouseClickX = e.getX();
                int mouseClickY = e.getY();

                int gridRow = mouseClickY / Constants.GRID_SIZE;
                int gridCol = mouseClickX / Constants.GRID_SIZE;
                System.out.println("(" + gridCol + "," + gridRow + ")");
                
                if (_bSetMid) {
                    if ((gridRow < Constants.MAP_COLS && gridRow + 1 < Constants.MAP_ROWS && gridRow + 2 < Constants.MAP_ROWS)
                            && (gridCol < Constants.MAP_COLS && gridCol + 1 < Constants.MAP_COLS && gridCol + 2 < Constants.MAP_COLS)) {
                        if (bControlDown) {
                        } 
                        else {
                            boolean midPointAllowed = false;
                            for(int i = 0; i < 3; i++){
                                for(int j = 0; j < 3; j++){
                                    if (mapTiles[gridRow+i][gridCol+j].isObstacle()){
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
                    if ((gridRow < Constants.MAP_ROWS)
                            && (gridCol < Constants.MAP_COLS)) {
                        if (bControlDown) {
                        	setObstacle(gridRow, gridCol, false);
                        } 
                        else {
                            setObstacle(gridRow, gridCol, true);
                        }
                    }
                    System.out.println(generateMapString());
                }
            }
        });
    	
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

    private void addObstacle(int row, int col) {
        if (mapTiles[row][col].isObstacle()) {
            //remove obstacle
            mapTiles[row][col].removeObstacle();
        } else if (isStartZone(row, col) || isGoalZone(row, col)) {
            JOptionPane.showMessageDialog(null, "Grid clicked is the start/goal zone. Please select another tile.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            mapTiles[row][col].setObstacle();
        }
    }
    
    private void removeObstacle(int row, int col) {
        if (mapTiles[row][col].isObstacle()) {
            if (mapTiles[row][col].isVirtualWall()) {
                JOptionPane.showMessageDialog(null,
                        "Removing the border walls will cause the robot to"
                        + " fall off the edge of the arena. Please do not"
                        + " attempt to kill the robot!", "Warning",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                mapTiles[row][col].removeObstacle();
            }
        }
    }

    public void paintComponent(Graphics g) {
 
        if (!_bMeasured) {
            System.out.println("Map width: " + _mapWidth + ", Map height: " + _mapWidth);

            // Calculate the map grids for rendering
            mapColorTiles = new ColorTile [Constants.MAP_ROWS][Constants.MAP_COLS];
            for (int mapRow = 0; mapRow < Constants.MAP_ROWS; mapRow++) {
                for (int mapCol = 0; mapCol < Constants.MAP_COLS; mapCol++) {
                    mapColorTiles [mapRow][mapCol] = new ColorTile (mapCol * Constants.GRID_SIZE, mapRow * Constants.GRID_SIZE, Constants.GRID_SIZE);
                }
            }

            _bMeasured = true;
        }

        // Clear the map
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, _mapWidth, _mapLength);

        Border border = BorderFactory.createLineBorder(Constants.C_GRID_LINE, Constants.GRID_LINE_WEIGHT);
        this.setBorder(border);

        // Paint the grids
        for (int mapRow = 0; mapRow < Constants.MAP_ROWS; mapRow++) {
            for (int mapCol = 0; mapCol < Constants.MAP_COLS; mapCol++) {
                g.setColor(Constants.C_GRID_LINE);
                g.fillRect(	mapColorTiles[mapRow][mapCol].borderX,
                        	mapColorTiles[mapRow][mapCol].borderY,
                        	mapColorTiles[mapRow][mapCol].borderSize,
                        	mapColorTiles[mapRow][mapCol].borderSize);

                Color gridColor = null;
                
                if (isStartZone(mapRow, mapCol)) {
                    gridColor = Constants.C_START;
                } else if (isGoalZone(mapRow, mapCol)) {
                    gridColor = Constants.C_GOAL;
                } else if (isMidZone(mapRow,mapCol, midRow, midCol)){
                    gridColor = Constants.C_MID;
                } else if (mapTiles[mapRow][mapCol].isObstacle()) {
                    gridColor = Constants.C_OBSTACLE;
                } else {
                    gridColor = Constants.C_FREE;
                }

                g.setColor(gridColor);
                g.fillRect(mapColorTiles[mapRow][mapCol].gridX,
                        mapColorTiles[mapRow][mapCol].gridY,
                        mapColorTiles[mapRow][mapCol].gridSize,
                        mapColorTiles[mapRow][mapCol].gridSize);

            }
        } // End outer for loop	
    }
    
    public String generateMapString() {

        String mapString = "";

        for (int row = 0; row < Constants.MAP_ROWS ; row++) 
            {
                for (int col = 0; col < Constants.MAP_COLS; col++) {
                // Obstacle - Border walls
                if (!mapTiles[row][col].isObstacle()) {
                    mapString += "0";
                } else {
                    mapString += "1";
                }
            }
        }

        return mapString;
    }

    /**
     * Loads the map from a map descriptor string<br>
     * Not including the virtual border surrounding the area!
     */
    public void loadFromMapString(String mapString) {

        for (int row = 0; row < Constants.MAP_ROWS ; row++) 
        {
            for (int col = 0; col < (Constants.MAP_COLS); col++) {
                int charIndex = (row * Constants.MAP_COLS )
                        + col;

                // Obstacle - Border walls
                if (mapString.charAt(charIndex) == '1') {
                    mapTiles[row][col].setObstacle();
                } else {
                    mapTiles[row][col].removeObstacle();
                }
            }
        }
    }

    public void clearMap() {

        for (int row = 0; row < (Constants.MAP_ROWS); row++) {
            for (int col = 0; col < (Constants.MAP_COLS); col++) {
                mapTiles[row][col].removeObstacle();
            }
        }
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

            this.gridX = borderX + Constants.GRID_LINE_WEIGHT;
            this.gridY = borderY + Constants.GRID_LINE_WEIGHT;
            this.gridSize = borderSize - (Constants.GRID_LINE_WEIGHT * 2);
        }
    }
}