package robot;

import map.Map;
import map.Tile;
import robot.RobotConstant.DIRECTION;
import robot.SensorConstant.SENSORTYPE;

/**
 * @author 18/19 S1 G3
 */
public class Sensor {

	// Variables
	private int sensorLowerLimit, sensorUpperLimit;
	private int sensorRow, sensorCol; // position
	private DIRECTION sensorDir;
	@SuppressWarnings("unused")
	private final SENSORTYPE sensorType;
	private int sensorID;


	// Constructor
	/**
	 * Initialise a sensor at intended [row, col], face an intended direction, 
	 * of an intended type and id
	 * @param row row (y) coordinate
	 * @param col col (x) coordinate
	 * @param dir Direction sensor faces
	 * @param type Sensor's type
	 * @param id Sensor's ID
	 */
	public Sensor (int row, int col, DIRECTION dir, SENSORTYPE type, int id) {
		sensorRow = row;
		sensorCol = col;
		sensorDir = dir;
		sensorID = id;
		sensorType = type;

		if (type == SENSORTYPE.SHORT) {
			sensorLowerLimit = SensorConstant.SR_LOWER;
			sensorUpperLimit = SensorConstant.SR_UPPER;
		} else {
			sensorLowerLimit = SensorConstant.LR_LOWER;
			sensorUpperLimit = SensorConstant.LR_UPPER;
		}
	}


	// Setter(s)
	/**
	 * Set sensor at intended [row, col], face an intended direction
	 * @param row row (y) coordinate
	 * @param col col (x) coordinate
	 * @param dir Direction sensor faces
	 */
	public void setSensor(int row, int col, DIRECTION dir) {
		sensorRow = row;
		sensorCol = col;
		sensorDir = dir;
	}

	
	// Simulator Function(s)
	/**
	 * Accounts for simulated sensor's direction when updating tracking map
	 * from simulated map
	 * @param mapExplore Tracking map used in Exploration
	 * @param mapActual Simulated map
	 */
	public void sense(Map mapExplore, Map mapActual) {
		switch (sensorDir) {
			case UP:
				senseInfo(mapExplore, mapActual, -1, 0);
				return;
			case DOWN:
				senseInfo(mapExplore, mapActual, 1, 0);
				return;
			case LEFT:
				senseInfo(mapExplore, mapActual, 0, -1);
				return;
			default:
				senseInfo(mapExplore, mapActual, 0, 1);
				return;
		}		
	}

	/**
	 * Updates tracking map from simulated map
	 * @param mapExplore Tracking map used in Exploration
	 * @param mapActual Simulated map
	 * @param rowMul Numeric accounting of sensed tiles, on the row (y) axis
	 * @param colMul Numeric accounting of sensed tiles, on the col (x) axis
	 */
	private void senseInfo(Map mapExplore, Map mapActual, int rowMul, int colMul) {		
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
						mapActual.getTile(sensedRow, sensedCol).isObstacle()) {
					return;
				}
			}
		}

		// Update mapExplore from mapActual from range sensor can see
		for (int i = sensorLowerLimit; i <= sensorUpperLimit; i++) {
			sensedRow = sensorRow + (rowMul * i);
			sensedCol = sensorCol + (colMul * i);

			// if 'sensed' tile is invalid/does not exist, terminate immediately
			if (!Map.isValidTile(sensedRow, sensedCol)) {
				return;
			}

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
	 * Accounts for sensor's direction when updating tracking map
	 * (physically integrated)
	 * @param mapExplore Tracking map used in Exploration
	 * @param sensorVal Sensor values
	 */
	public void sense(Map mapExplore, int sensorVal) {
		switch (sensorDir) {
			case UP:
				senseInfo(mapExplore, sensorVal, -1, 0);
				return;
			case DOWN:
				senseInfo(mapExplore, sensorVal, 1, 0);
				return;
			case LEFT:
				senseInfo(mapExplore, sensorVal, 0, -1);
				return;
			case RIGHT:
				senseInfo(mapExplore, sensorVal, 0, 1);
				return;
		}
	}

	/**
	 * Updates tracking map from physical sensor values
	 * (physically integrated)
	 * @param exploredMap Tracking map used in Exploration
	 * @param sensorVal Sensor values
	 * @param rowMul Numeric accounting of sensed tiles, on the row (y) axis
	 * @param colMul Numeric accounting of sensed tiles, on the col (x) axis
	 */
	private void senseInfo(Map exploredMap, int sensorVal, int rowMul, int colMul) {
		int row, col;

		boolean goalCoor, startCoor;
		Tile obsTile;
		if (sensorVal == 0) {
			if (sensorID == 6) {
				row = sensorRow+(3*rowMul);
				col = sensorCol+(3*colMul);
			} else {
				row = sensorRow+(1*rowMul);
				col = sensorCol+(1*colMul);
			}
			if (Map.isValidTile(row, col)) {
				goalCoor = ((row == 0 || row == 1 || row == 2) && (col == 12 || col == 13 || col == 14)); 
				startCoor = ((row == 19 || row == 18 || row == 17) && (col == 0 || col == 1 || col == 2));
				if (!goalCoor && !startCoor){
					obsTile = exploredMap.getTile(row, col);
					obsTile.setExplored(true);
					exploredMap.setObstacleTile(row, col, true);
				}
			}
			return;
		}

		if (sensorID == 6) {
			sensorVal = sensorVal + 2;
		}
		for (int i = sensorLowerLimit; i <= sensorVal; i++) {
			row = sensorRow + (rowMul * i);
			col = sensorCol + (colMul * i);

			if (!Map.isValidTile(row, col)) {
				return;
			}

			exploredMap.getTile(row, col).setExplored(true);

			// Legacy Code - AutoLogic Set Blockers
			/*
			if ((sensorVal == 1 || sensorVal == 2) && sensorVal == i) {
				if (!Map.isValidTile(row+(1*rowMul), col+(1*colMul))) return;
				
				Tile obsTile = exploredMap.getTile(row+(1*rowMul), col+(1*colMul));
				obsTile.setExplored(true);
				exploredMap.setObstacleTile(row+(1*rowMul), col+(1*colMul), true);
				System.out.println("tile" + obsTile.getRow() + "," + obsTile.getCol() + "setObstacle");
				return;
			}
			*/

			// Override values set by Long Range sensor when any short range sensor detects discrepancies 
			if (exploredMap.getTile(row, col).isObstacle() && (sensorID == 1 || sensorID == 2 || sensorID == 3)) {
				exploredMap.setObstacleTile(row, col, false);
			}
				
		}
	}

}
