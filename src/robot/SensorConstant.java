package robot;

public class SensorConstant {

	// Sensor Sense Range
	// -> Short Range
	public static final int SR_LOWER = 1;
	public static final int SR_UPPER = 3; // inclusive
	// -> Long Range
	public static final int LR_LOWER = 2;
	public static final int LR_UPPER = 5; // inclusive

	public enum SENSORTYPE {
		SHORT, LONG
	}

	// private constructor (prevent accidental instantiation)
	private SensorConstant() {}

}
