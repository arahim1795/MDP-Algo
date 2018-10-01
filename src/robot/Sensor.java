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
	
	// Other Function(s)
	/**
	 * Returns number of cells that could be seen by the sensor
	 * -1 is returned if obstacle, invalid map coordinates is first detected
	 * @param exploredMap
	 * @param realMap
	 * @return number of cells that is seen by the sensor, -1 if an obstacle or invalid map coordinates is first detected
	 */
	public int sense(Map exploredMap, Map realMap) {
		switch (sensorDir) {
			case UP:
				return getSensorVal(exploredMap, realMap, 0, -1);
			case DOWN:
				return getSensorVal(exploredMap, realMap, 0, 1);
			case LEFT:
				return getSensorVal(exploredMap, realMap, -1, 0);
			default:
				return getSensorVal(exploredMap, realMap, 1, 0);
		}		
	}
	
	/**
	 * Update map  in the map and returns the row or column value of the obstacle cell
	 * @param exploredMap
	 * @param realMap
	 * @param colMul
	 * @param rowMul
	 * @return
	 */
    private int getSensorVal(Map exploredMap, Map realMap, int colMul, int rowMul) {
        if (sensorLowerLimit > 1) {
            for (int i = 1; i < this.sensorLowerLimit; i++) {
                int row = this.sensorRow + (colMul * i);
                int col = this.sensorCol + (rowMul * i);

                if (!Map.isValidTile(row, col)) return i;
                if (realMap.getTile(row, col).isObstacle()) return i;
            }
        }

        // Check if anything is detected by the sensor and return that value.
        for (int i = this.sensorLowerLimit; i <= this.sensorUpperLimit; i++) {
            int row = this.sensorRow + (colMul * i);
            int col = this.sensorCol + (rowMul * i);

            if (!Map.isValidTile(row, col)) return i;

            exploredMap.getTile(row, col).setExplored(true);

            if (realMap.getTile(row, col).isObstacle()) {
                exploredMap.setObstacleTile(row, col, true);
                return i;
            }
        }

        // Else, return -1.
        return -1;
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    
    /**
     * Uses the sensor direction and given value from the actual sensor to update the map.
     */
    public void senseReal(Map exploredMap, int sensorVal) {
        switch (sensorDir) {
            case UP:
                processSensorVal(exploredMap, sensorVal, 1, 0);
                break;
            case RIGHT:
                processSensorVal(exploredMap, sensorVal, 0, 1);
                break;
            case DOWN:
                processSensorVal(exploredMap, sensorVal, -1, 0);
                break;
            case LEFT:
                processSensorVal(exploredMap, sensorVal, 0, -1);
                break;
        }
    }

    /**
     * Sets the correct cells to explored and/or obstacle according to the actual sensor value.
     */
    private void processSensorVal(Map exploredMap, int sensorVal, int rowInc, int colInc) {
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
