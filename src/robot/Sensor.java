package robot;

import map.Map;
import robot.RobotConstant.DIRECTION;

/**
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
	 * @param row
	 * @param col
	 * @param dir
	 */
	public void setSensor(int row, int col, DIRECTION dir) {
		sensorRow = row;
		sensorCol = col;
		sensorDir = dir;
	}
	
	// Simulator Function(s)
	/**
	 * Returns number of cells 'seen' by a simulated sensor, based on its direction, lacks
	 * enumeration function
	 * -1 is returned if no obstacle/invalid map coordinates is between sensor and its upper 
	 * limit
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @return number of cells that is seen by the sensor, -1 if no obstacle/invalid map coordinates 
	 * is within sensor and its upper limit
	 */
	public int senseSim(Map mapExplore, Map mapActual) {
		switch (sensorDir) {
			case UP:
				return sensorInfoSim(mapExplore, mapActual, 0, -1);
			case DOWN:
				return sensorInfoSim(mapExplore, mapActual, 0, 1);
			case LEFT:
				return sensorInfoSim(mapExplore, mapActual, -1, 0);
			default:
				return sensorInfoSim(mapExplore, mapActual, 1, 0);
		}		
	}
	
	/**
	 * Returns the number of cells 'seen' by the sensor, not based on its direction and provides
	 * enumeration of cells function 
	 * -1 is returned if no obstacle/invalid map coordinates is between sensor and its upper 
	 * limit
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
	 * @return number of cells 'seen' by the sensor, -1 returned is no obstacle/invalid map 
	 * coordinates is between sensor and its upper limit
	 */
	private int sensorInfoSim(Map mapExplore, Map mapActual, int rowMul, int colMul) {
		/*
		 * Checks whether there is an obstacle/invalid map coordinates between the sensor and
		 * its sensor's lower limit, applicable to sensors which lower limit beyond 1 unit/10 cm
		 */
		int row, col;
		if (sensorLowerLimit > 1) {
			for (int i = 1; i < sensorLowerLimit; i++) {
				row = sensorRow + (rowMul * i);
				col = sensorCol + (colMul * i);
				
				// Check for invalid map coordinates
				if (!Map.isValidTile(row, col)) return i;
				// Check for any obstacles
				if (mapActual.getTile(row, col).isObstacle()) return i;
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
			if (!Map.isValidTile(row, col)) return i;
			
			mapExplore.getTile(row, col).setExplored(true);
			
			/*
			 * Check for any obstacles in the actual map, update Tiles with correct boolean
			 * (obstacle, virtualWall)
			 */
			if (mapActual.getTile(row, col).isObstacle()) {
				mapExplore.setObstacleTile(row, col, true);
				return i;
			}
		}
		
		// If no obstacles detected between sensor's lower and upper limit, return -1
		return -1;
	}

	// Physical Function(s)
	/**
	 * Returns number of cells that is seen by the physical sensor, based on its direction, lacks
	 * enumeration function
	 * -1 is returned if no obstacle/invalid map coordinates is within sensor's lower and upper 
	 * limit
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
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void sensorInfoPhys(Map exploredMap, int sensorVal, int rowInc, int colInc) {
        if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lowerRange

        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < this.sensorLowerLimit; i++) {
            int row = this.sensorRow + (rowInc * i);
            int col = this.sensorCol + (colInc * i);

            if (!Map.isValidTile(row, col)) return;
            if (exploredMap.getTile(row, col).isObstacle()) return;
        }

        // Update map according to sensor's value.
        for (int i = this.sensorLowerLimit; i <= this.sensorUpperLimit; i++) {
            int row = this.sensorRow + (rowInc * i);
            int col = this.sensorCol + (colInc * i);

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
