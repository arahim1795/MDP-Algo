//MapUI

package map;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.*;

import map.Map;
import map.Tile;
import map.*;
import map.Constants;
public class MapUI extends Map {
	
	private Tile[][] tile = null;
	private Map map = new Map();
	
	// For measuring size of the canvas
    private boolean _bMeasured = false;
    private boolean _bSetMid = false;

    // Size of the tile
    private int _mapLength = Constants.MAP_COLS;
    private int _mapWidth = Constants.MAP_ROWS;
    
    // Mid Point
    public static int midRow = Constants.START_GRID_ROW;
    public static int midCol = Constants.START_GRID_COL;
    
    
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
                                if (map.field[gridRow+i][gridCol+j].isObstacle()){
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
                        removeObstacle(gridRow, gridCol);
                    } 
                    else {
                        addObstacle(gridRow, gridCol);
                    }
                }
                System.out.println(generateMapString());
            }
        }
    });
    
    
    public void paintComponent(Graphics g) {
    	
    	System.out.println("tile width: " + _mapWidth + ", tile length: " + _mapLength);

        // Calculate the tile grids for rendering
        tile = new Tile[Constants.MAP_COLS][Constants.MAP_ROWS];
        for (int mapRow = 0; mapRow < Constants.MAP_COLS; mapRow++) {
            for (int mapCol = 0; mapCol < Constants.MAP_ROWS; mapCol++) {
            	tile [mapRow][mapCol] = new Tile(mapCol* Constants.GRID_SIZE, mapRow*Constants.GRID_SIZE, Constants.GRID_SIZE);
               
                g.setColor(Constants.C_GRID_LINE);
                g.fillRect (tile[mapRow][mapCol].borderX,
                        	tile[mapRow][mapCol].borderY,
                        	tile[mapRow][mapCol].borderSize,
                        	tile[mapRow][mapCol].borderSize);

                Color gridColor = null;

                //color start zone
                if (mapRow >= (Constants.START_GRID_ROW) && (mapRow <= (Constants.START_GRID_ROW +2)) && 
                (mapCol >= (Constants.START_GRID_COL)) && (mapCol <= (Constants.START_GRID_COL +2))) {
                    gridColor = Constants.C_START;
                } 
                
                //color goal zone
                else if ((mapRow >= (Constants.GOAL_GRID_ROW)) && (mapRow <= (Constants.GOAL_GRID_ROW +2)) && 
                		(mapCol >= (Constants.GOAL_GRID_COL)) && (mapCol <= (Constants.GOAL_GRID_COL +2))) {
                    gridColor = Constants.C_GOAL;
                } 
                
                // color mid zone
                else if (mapRow >= midRow && mapRow <= midRow + 2 && mapCol >= midCol && mapCol <= midCol + 2){
                    gridColor = Constants.C_MID;
                } 
                
                // color obstacle
                else if (tile[mapRow][mapCol].isObstacle()) {
                    gridColor = Constants.C_OBSTACLE;
                } 
                
                // color free tile
                else {
                    gridColor = Constants.C_FREE;
                }

                g.setColor(gridColor);
                g.fillRect(tile[mapRow][mapCol].gridX,
                        tile[mapRow][mapCol].gridY,
                        tile[mapRow][mapCol].gridSize,
                        tile[mapRow][mapCol].gridSize);
            }
        }	
    }
    

	public class Tile {

        public int borderX;
        public int borderY;
        public int borderSize;

        public int gridX;
        public int gridY;
        public int gridSize;

        public Tile(int borderX, int borderY, int borderSize) {
            this.borderX = borderX;
            this.borderY = borderY;
            this.borderSize = borderSize;

            this.gridX = borderX + Constants.GRID_LINE_WEIGHT;
            this.gridY = borderY + Constants.GRID_LINE_WEIGHT;
            this.gridSize = borderSize - (Constants.GRID_LINE_WEIGHT * 2);
        }

    }
	
	public String generateMapString() {

        String mapString = "";

        for (int row = 0; row < Constants.MAP_ROWS ; row++) {
                for (int col = 0; col < Constants.MAP_COLS; col++) {
                // Obstacle - Border walls
                if (tile[row][col].isObstacle()) {
                    mapString += "1";
                } 
                else {
                    mapString += "0";
                }
            }
        }

        return mapString;
    } 
	
	public void loadFromMapString(String mapString) {

	        for (int row = 0; row < Constants.MAP_ROWS ; row++) { 
	        	for (int col = 0; col < Constants.MAP_COLS; col++) {
	                int charIndex = (row * Constants.MAP_COLS ) + col;

	                // Obstacle - Border walls
	                if (mapString.charAt(charIndex) == '1') {
	                    tile[row][col].setObstacle(true);
	                } else {
	                    tile[row][col].setObstacle(false);
	                }
	            }
	        }
	    }
	
	public void clearMap() {

        for (int row = 0; row < (Constants.MAP_COLS); row++) {
            for (int col = 0; col < (Constants.MAP_ROWS); col++) {
                tile[row][col].setObstacle(false);
            }
        }
    }
	
	private void addObstacle(int row, int col) {
        if (tile[row][col].isObstacle()) {
        	JOptionPane.showMessageDialog(Simulator.frame, "Tile selected is already an obstacle. Please try again.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } 
        else if (isStartZone(row, col)) {
            JOptionPane.showMessageDialog(Simulator.frame, "Tile selected is the start zone. Please try again.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } 
        else if (isGoalZone(row, col)) {
            JOptionPane.showMessageDialog(Simulator.frame, "Tile selected is the goal zone. Please try again.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        else {
            tile[row][col].setObstacle(true);
        }
    }
	
	 private void removeObstacle(int row, int col) {
	        if (tile[row][col].isObstacle()) {
	            if (tile[row][col].isVirtualWall) {
	                JOptionPane.showMessageDialog(null,
	                        "Removing the border walls will cause the robot to"
	                        + " fall off the edge of the arena. Please do not"
	                        + " attempt to kill the robot!", "Warning",
	                        JOptionPane.WARNING_MESSAGE);
	            } 
	            else {
	                tile[row][col].setObstacle(false);
	            }
	        }
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
}