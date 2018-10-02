package robot;

import map.Map;
import robot.RobotConstant.DIRECTION;

/**
 * DO NOT USE YET
 * TODO update simulated sensors
 * @author 18/19 S1 G3
 */
public class Sensor {
	
	// Variables
	private final int sensorLowerLimit, sensorUpperLimit; // 1 unit = 1 10cm block
	int sensorRow, sensorCol;
	DIRECTION sensorDir;
	String sensorID;
	
	// Constructor
	/**
	 * 
	 * @param lowerLimit
	 * @param upperLimit
	 */
	public Sensor (int lowerLimit, int upperLimit, int row, int col, DIRECTION dir, String id) {
		sensorLowerLimit = lowerLimit;
		sensorUpperLimit = upperLimit;
		sensorRow = row;
		sensorCol = col;
		sensorDir = dir;
		sensorID = id;
	}
	
	// Setter(s)
	/**
	 * 
	 * @param col
	 * @param row
	 * @param dir
	 */
	public void setSensor(int col, int row, DIRECTION dir) {
		sensorRow = row;
		sensorCol = col;
		sensorDir = dir;
	}
	
	// Simulator Function(s)
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @return number of cells that is seen by the sensor, -1 if no obstacle/invalid map coordinates 
	 * is within sensor and its upper limit
	 */
	public void senseSim(Map mapExplore, Map mapActual) {
		switch (sensorDir) {
			case UP:
				sensorInfoSim(mapExplore, mapActual, 0, -1);
				return;
			case DOWN:
				sensorInfoSim(mapExplore, mapActual, 0, 1);
				return;
			case LEFT:
				sensorInfoSim(mapExplore, mapActual, -1, 0);
				return;
			default:
				sensorInfoSim(mapExplore, mapActual, 1, 0);
				return;
		}		
	}
	
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
	 * @return number of cells 'seen' by the sensor, -1 returned is no obstacle/invalid map 
	 * coordinates is between sensor and its upper limit
	 */
	private void sensorInfoSim(Map mapExplore, Map mapActual, int colMul, int rowMul) {
		/*
		 * Checks whether there is an obstacle/invalid map coordinates between the sensor and
		 * its sensor's lower limit, applicable to sensors which lower limit beyond 1 unit/10 cm
		 */
		int row, col;
		if (sensorLowerLimit > 1) {
			for (int i = 1; i < sensorLowerLimit; i++) {
				row = sensorRow + (rowMul * i);
				col = sensorCol + (colMul * i);
				
				// Invalid map coordinates between sensor and lower limit, to immediately return
				if (!Map.isValidTile(row, col)) return;
				// Obstacle is between sensor and lower limit, to immediately return
				if (mapActual.getTile(row, col).isObstacle()) return;
			}
		}
		
		/*
		 * Check whether there is an obstacle/invalid map coordinates between the sensor's lower
		 * limit and its upper limit
		 */
		for (int i = sensorLowerLimit; i <= sensorUpperLimit; i++) {
			row = sensorRow + (rowMul * i);
			col = sensorCol + (colMul * i);
			
			// Check for invalid map coordinates
			if (!Map.isValidTile(row, col)) return;
			
			mapExplore.getTile(row, col).setExplored(true);
			
			/*
			 * Check for any obstacles in the actual map, update Tiles with correct boolean
			 * (obstacle, virtualWall)
			 */
			if (mapActual.getTile(row, col).isObstacle()) {
				mapExplore.setObstacleTile(row, col, true);
				return;
			}
		}
		
		// If no obstacles detected between sensor's lower and upper limit, return -1
		return;
	}

	// Physical Function(s)
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param sensorVal Sensor values
	 */
	public void sensePhys(Map mapExplore, int sensorVal) {
		switch (sensorDir) {
			case UP:
				sensorInfoPhys(mapExplore, sensorVal, 1, 0);
				return;
			case DOWN:
				sensorInfoPhys(mapExplore, sensorVal, 0, 1);
				break;
			case LEFT:
				sensorInfoPhys(mapExplore, sensorVal, -1, 0);
				break;
			default:
				sensorInfoPhys(mapExplore, sensorVal, 0, -1);
				break;
		}
	}

	/**
	 * 
	 * @param exploredMap Map used to track exploration
	 * @param sensorVal Sensor values
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
	 */
    private void sensorInfoPhys(Map exploredMap, int sensorVal, int colMul, int rowMul) {
       
    	/*
    	 * Check if obstacle exist between sensor and lower limit
    	 * (used only by sensor with lower limit above 1)
    	 */
    	if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lowerRange
        
        int row, col;
        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < sensorLowerLimit; i++) {
        	col = sensorCol + (colMul * i);
        	row = sensorRow + (rowMul * i);
            

            if (!Map.isValidTile(row, col)) return;
            if (exploredMap.getTile(row, col).isObstacle()) return;
        }

        // Update map according to sensor's value.
        for (int i = this.sensorLowerLimit; i <= this.sensorUpperLimit; i++) {
            
            col = this.sensorCol + (colMul * i);
            row = this.sensorRow + (rowMul * i);
            
            if (!Map.isValidTile(row, col)) continue;

            exploredMap.getTile(row, col).setExplored(true);

            if (sensorVal == i) {
                exploredMap.setObstacleTile(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle.
            if (exploredMap.getTile(row, col).isObstacle()) {
                if (sensorID.equals("SRFL") || sensorID.equals("SRFC") || sensorID.equals("SRFR")) {
                    exploredMap.setObstacleTile(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
    
    
}
