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
	 * 
	 * @param row
	 * @param col
	 * @param dir
	 * @param type
	 * @param id
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

	// Simulated
	/**
	 * 
	 * @param mapExplore Map used to track exploration
	 * @param mapActual Simulated map
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
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
	 * 
	 * @param mapExplore Map used to track exploration
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
	 * 
	 * @param exploredMap Map used to track exploration
	 * @param sensorVal Sensor values
	 * @param rowMul Numerically adjust where the sense would occur, on the row(x)-axis
	 * @param colMul Numerically adjust where the sense would occur, on the col(y)-axis
	 */
	private void senseInfo(Map exploredMap, int sensorVal, int rowMul, int colMul) {
		int row, col;
		/*
		 * Check if obstacle exist between sensor and lower limit
		 * (used only by sensor with lower limit above 1)
		 */
		
		/*
		if (sensorVal == 0) {
			if (sensorID == 6) {
				return;
			} else {
				row = sensorRow+(1*rowMul);
				col = sensorCol+(1*colMul);
				// invalid combo:
				// 012,12 012,13 012,14
				if (Map.isValidTile(row, col)) {
					boolean goalCoor = ((row == 0 || row == 1 || row == 2) && (col == 12 || col == 13 || col == 14)); 
					boolean startCoor = ((row == 19 || row == 18 || row == 17) && (col == 0 || col == 1 || col == 2)); 
					if (!goalCoor && !startCoor) {
						Tile obsTile = exploredMap.getTile(sensorRow+(1*rowMul), sensorCol+(1*colMul));
						obsTile.setExplored(true);
						exploredMap.setObstacleTile(row, col, true);
					}
				}
				return;
			}
		}
		*/
		if (sensorVal == 0) {
				row = sensorRow+(1*rowMul);
				col = sensorCol+(1*colMul);
				// invalid combo:
				// 012,12 012,13 012,14
				if (Map.isValidTile(row, col)) {
					boolean goalCoor = ((row == 0 || row == 1 || row == 2) && (col == 12 || col == 13 || col == 14)); 
					boolean startCoor = ((row == 19 || row == 18 || row == 17) && (col == 0 || col == 1 || col == 2)); 
					if (!goalCoor && !startCoor) {
						Tile obsTile = exploredMap.getTile(sensorRow+(1*rowMul), sensorCol+(1*colMul));
						obsTile.setExplored(true);
						exploredMap.setObstacleTile(row, col, true);
					}
				}
				return;
			}
		

		// If above fails, check if starting point is valid for sensors with lowerRange > 1.
		/*if (sensorID == 6) {
			for (int i = 1; i < sensorLowerLimit; i++) {
				row = sensorRow + (rowMul * i);
				col = sensorCol + (colMul * i);

				// if 'sensed' tile within blindspot is invalid/does not exist/an obstacle, terminate immediately
				if (!Map.isValidTile(row, col) ||
						exploredMap.getTile(row, col).isObstacle()) return;
			}
		}*/

		// update map
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

			// AutoLogic Set Blockers
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
