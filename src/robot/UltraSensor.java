package robot;

import robot.RobotConstant.DIRECTION;

public class UltraSensor extends Sensor {
	
	int range;	
	
	// Constructor
	public UltraSensor(int lowerLimit, int upperLimit, int row, int col, DIRECTION dir, String id) {
		super(lowerLimit, upperLimit, row, col, dir, id);
	}
	
	// 
	
}
