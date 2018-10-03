package robot;

public class SensorConstant {
	
	// Sensor Sense Range (Simulated)
	// -> Short Range
	public static final int SR_LOWER_SIM = 1;
	public static final int SR_UPPER_SIM = 3;
	// -> Long Range
	public static final int LR_LOWER_SIM = 3;
	public static final int LR_UPPER_SIM = 5;
	
	// Sensor Sense Range (Physical/Actual)
	// -> Short Range
	public static final int SR_LOWER_PHY = 1;
	public static final int SR_UPPER_PHY = 3;
	// -> Long Range
	public static final int LR_LOWER_PHY = 3;
	public static final int LR_UPPER_PHY = 5;
	
	// private constructor (prevent accidental instantiation)
	private SensorConstant() {}

}
