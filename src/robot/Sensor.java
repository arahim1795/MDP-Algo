package robot;

import map.Map;
import robot.RobotConstant.DIRECTION;

/**
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
	public void setSensor(int row, int col, DIRECTION dir) {
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
				sensorInfoSim(mapExplore, mapActual, -1, 0);
				return;
			case DOWN:
				sensorInfoSim(mapExplore, mapActual, 1, 0);
				return;
			case LEFT:
				sensorInfoSim(mapExplore, mapActual, 0, -1);
				return;
			default:
				sensorInfoSim(mapExplore, mapActual, 0, 1);
				return;
		}		
	}
	
	// Simulated
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
	 */
	private void sensorInfoSim(Map mapExplore, Map mapActual, int rowMul, int colMul) {		
		int sensedRow, sensedCol;
		/*
		 * checks whether there is an obstacle/invalid map coordinates within the sensor's
		 * blindspot (i.e. between 1 unit/10cm and lower limit)
		 * applicable only to sensors which lower limit beyond 1 unit/10 cm
		 */
		if (sensorLowerLimit > 1) {
			for (int i = 1; i < sensorLowerLimit; i++) {
				sensedRow = sensorRow + (rowMul * i);
				sensedCol = sensorCol + (colMul * i);
				
				// if 'sensed' tile within blindspot is invalid/does not exist/an obstacle, terminate immediately
				if (!Map.isValidTile(sensedRow, sensedCol) ||
					mapActual.getTile(sensedRow, sensedCol).isObstacle()) return;
			}
		}
		
		// Update mapExplore from mapActual from range sensor can see
		for (int i = sensorLowerLimit; i <= sensorUpperLimit; i++) {
			sensedRow = sensorRow + (rowMul * i);
			sensedCol = sensorCol + (colMul * i);
			
			// if 'sensed' tile is invalid/does not exist, terminate immediately
			if (!Map.isValidTile(sensedRow, sensedCol)) return;
			
			// if 'sensed tile is an obstacle, set Tile to obstacle and terminate immediately
			if (mapActual.getTile(sensedRow, sensedCol).isObstacle()) {
				mapExplore.getTile(sensedRow, sensedCol).setExplored(true);
				mapExplore.setObstacleTile(sensedRow, sensedCol, true);
				return;
			}
			// if 'sensed' tile is valid/does exist, set Tile to explored
			mapExplore.getTile(sensedRow, sensedCol).setExplored(true);
		}
		// If no obstacles detected between sensor's lower and upper limit, return -1
		return;
	}

	
	// Physical	
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param sensorVal Sensor values
	 */
	public void sensePhys(Map mapExplore, int sensorVal) {
		switch (sensorDir) {
			case UP:
				sensorInfoPhys(mapExplore, sensorVal, -1, 0);
				return;
			case DOWN:
				sensorInfoPhys(mapExplore, sensorVal, 1, 0);
				break;
			case LEFT:
				sensorInfoPhys(mapExplore, sensorVal, 0, -1);
				break;
			default:
				sensorInfoPhys(mapExplore, sensorVal, 0, 1);
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
    private void sensorInfoPhys(Map exploredMap, int sensorVal, int rowMul, int colMul) {
       
    	/*
    	 * Check if obstacle exist between sensor and lower limit
    	 * (used only by sensor with lower limit above 1)
    	 */
    	if (sensorVal == 0) return;  // return value for LR sensor if obstacle before lowerRange
        
        int row, col;
        // If above fails, check if starting point is valid for sensors with lowerRange > 1.
        for (int i = 1; i < sensorLowerLimit; i++) {
        	row = sensorRow + (rowMul * i);
        	col = sensorCol + (colMul * i);

        	// if 'sensed' tile within blindspot is invalid/does not exist/an obstacle, terminate immediately
			if (!Map.isValidTile(row, col) ||
				exploredMap.getTile(row, col).isObstacle()) return;
        }

        // Update map according to sensor's value.
        for (int i = sensorLowerLimit; i <= sensorUpperLimit; i++) {
        	row = this.sensorRow + (rowMul * i);
            col = this.sensorCol + (colMul * i);
            
            if (!Map.isValidTile(row, col)) continue;

            exploredMap.getTile(row, col).setExplored(true);

            if (sensorVal == i) {
                exploredMap.setObstacleTile(row, col, true);
                break;
            }

            // Override values set by Long Range sensor when front Short Range sensor detects discrepancies
            boolean frontSensor = sensorID.equals("S2") || sensorID.equals("S3") || sensorID.equals("S4"); 
            if (exploredMap.getTile(row, col).isObstacle() && frontSensor) exploredMap.setObstacleTile(row, col, false);
            else break;
        }
    }
    
}
