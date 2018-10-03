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
    	
    	
    }   
 
    
    

} 
