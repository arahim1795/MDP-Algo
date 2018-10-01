package robot;

import map.Map;
import robot.RobotConstant.DIRECTION;

/**
 * Represents a sensor mounted on the robot.
 *
 * @author 
 */

public class Sensor {
    private final int lowerRange;
    private final int upperRange;
    private int sensorPosRow;
    private int sensorPosCol;
    private DIRECTION sensorDir;
    private final String id;

    public Sensor(int lowerRange, int upperRange, int row, int col, DIRECTION dir, String id) {
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
        this.id = id;
    }

    public void setSensor(int row, int col, DIRECTION dir) {
        this.sensorPosRow = row;
        this.sensorPosCol = col;
        this.sensorDir = dir;
    }

    /**
     * Returns the number of cells to the nearest detected obstacle or -1 if no obstacle is detected.
     */
    public int sense(Map exploredMap, Map realMap) {
        switch (sensorDir) {
            case UP:
                return getSensorVal(exploredMap, realMap, 1, 0);
            case RIGHT:
                return getSensorVal(exploredMap, realMap, 0, 1);
            case DOWN:
                return getSensorVal(exploredMap, realMap, -1, 0);
            case LEFT:
                return getSensorVal(exploredMap, realMap, 0, -1);
        }
        return -1;
    }

    /**
     * Sets the appropriate obstacle cell in the map and returns the row or column value of the obstacle cell. Returns
     * -1 if no obstacle is detected.
     */
    private int getSensorVal(Map exploredMap, Map realMap, int rowInc, int colInc) {
        // Check if starting point is valid for sensors with lowerRange > 1.
        if (lowerRange > 1) {
            for (int i = 1; i < this.lowerRange; i++) {
                int row = this.sensorPosRow + (rowInc * i);
                int col = this.sensorPosCol + (colInc * i);

                if (!Map.isValidTile(row, col)) return i;
                if (realMap.getTile(row, col).isObstacle()) return i;
            }
        }

        // Check if anything is detected by the sensor and return that value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

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
        for (int i = 1; i < this.lowerRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!Map.isValidTile(row, col)) return;
            if (exploredMap.getTile(row, col).isObstacle()) return;
        }

        // Update map according to sensor's value.
        for (int i = this.lowerRange; i <= this.upperRange; i++) {
            int row = this.sensorPosRow + (rowInc * i);
            int col = this.sensorPosCol + (colInc * i);

            if (!Map.isValidTile(row, col)) continue;

            exploredMap.getTile(row, col).setExplored(true);

            if (sensorVal == i) {
                exploredMap.setObstacleTile(row, col, true);
                break;
            }

            // Override previous obstacle value if front sensors detect no obstacle.
            if (exploredMap.getTile(row, col).isObstacle()) {
                if (id.equals("SRFL") || id.equals("SRFC") || id.equals("SRFR")) {
                    exploredMap.setObstacleTile(row, col, false);
                } else {
                    break;
                }
            }
        }
    }
}
