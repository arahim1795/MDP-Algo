package utility;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import robot.RobotConstant.DIRECTION;

/**
 * @author 18/19 S1 G3
 * Tutorial: https://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html
 */
public class Comms {

	// Major Headers
	public static final String ARDUINO = "ZYXA";
	public static final String ANDROID = "ZYXB";
	public static final String ARDnAND = "ZYXC";
	public static final String RPI = "ZYXR";

	// Android Headers
	// - to
	public static final String MAP = "#mdf:";		// + "/", send map descriptor 
	public static final String POS = "#setrobot:";	// + "/", send current bot position
    public static final String FP = "#fp:";
    //  - from
    public static final String EX = "#exp";
    public static final String MP = "mp";	// Android>PC - Setting Mid Point 
    public static final String SP = "sp";	// Android>PC - Setting Mid Point 
    public static final String START = "r1";
    public static final String STOP = "r0";
  
	// Arduino Headers
	// - to
	public static final String SET = "SET";     // PC>Arduino - Set-Up Bot
	public static final String INS = "INSTR";      // PC>Arduino - Give Instruction
	public static final String END = "END";
	// - from
	public static final String SENSOR_DATA = "SDATA";       // Arduino>PC - Sensor Data
	public static final String ACK = "ACK";	//TODO tentative
	// RPi Headers
	// - to
	public static final String C = "CAM";

	private static String robotName = "192.168.3.1";
	private static int portNum = 1224;

	private static Socket robotComms = null;
	// private static BufferedReader is = null; // read from Pi
	private static DataInputStream is = null;
	// private static BufferedWriter os = null; // write to Pi
	private static DataOutputStream os = null;

	/**
	 * Open connection (socket) with Pi, communication streams open afterwards 
	 */
	public static void openSocket() {
		try {
			robotComms = new Socket(robotName, portNum);
			// is = new BufferedReader( new InputStreamReader(robotComms.getInputStream()));
			is = new DataInputStream( robotComms.getInputStream());
			// os = new BufferedWriter ( new OutputStreamReader(robotComms.getOutputStream()));
			os = new DataOutputStream(robotComms.getOutputStream());
		} catch (UnknownHostException u) {
			System.err.println("Cannot Resolve Host: " + robotName);
		} catch (IOException e) {
			System.err.println("Lack of I/O Connection to " + robotName);
		}
	}

	/**
	 * Closes communication streams with Pi, connection (socket) closed afterwards
	 */
	public static void closeSocket() {
		try {
			is.close(); is = null;
			os.close(); os = null;
			robotComms.close(); robotComms = null;
		} catch (UnknownHostException u) {
			System.err.println("Cannot Resolve Host: " + robotName);
		} catch (IOException e) {
			System.err.println("I/O Connection to " + robotName + " still active");
		}
	}
	/**
	 * 
	 * @param major
	 * @param sub
	 * @param content
	 * @return
	 */
	public static boolean sendMsg(String major, String sub, String content) {
		StringBuilder sb = new StringBuilder();
		switch (major) {
			case ARDUINO:
				sb.append(major);
				sb.append("_");
				switch (sub) {
					case END:
					case SET:
					case INS:
						sb.append(sub);
						sb.append("_");
						break;
					default:
						System.out.println("Invalid Purpose");
						return false;
				}
				break;
			case ANDROID:
				switch (sub) {
					case MAP:
					case POS:
					case FP:
						sb.append(sub);
						break;
					default:
						System.err.println("Invalid Purpose");
						return false;
				}
				break;
			case RPI:
				// TODO incorporate Image Tracking
				sb.append(major);
				sb.append("_");
				switch (sub) {
					case C:
						sb.append(sub);
						sb.append("_");
						break;
					default:
						System.err.println("Invalid Purpose");
						return false;
				}
				break;
			default:
				System.err.println("Invalid Destination");
				return false;
		}
		
		if (content != null && content.length() != 0) sb.append(content);
    
		boolean sent;
		try {
			os.writeBytes(sb.toString());
			sent = true;
		} catch (IOException e) {
			System.err.println(e);
			sent = false;
		}
		return sent;
	}

	/**
	 * Receives messages from Pi
	 * @return message
	 */
	@SuppressWarnings("deprecation")
	public static String receiveMsg() {
		StringBuilder msg = new StringBuilder();
		StringBuilder outMsg = new StringBuilder();
		try {
			msg.append(is.readLine());

		} catch (IOException e) {
			System.err.println(e);
		}

		for(int i=2;i<msg.length()-1;i++){
			outMsg.append(msg.charAt(i));
		}	
		return outMsg.toString();

	}

	/**
	 * Returns true if connection and communication streams to Pi are active
	 * @return true if connection and communication streams are active, false otherwise
	 */
	public static boolean connectionActive() {
		return robotComms == null && is == null && os == null;
	}
  
	// TODO complete method
	public static int readCoor(String pos, String s){
		int ptr=0;
		StringBuilder result = new StringBuilder("");
		switch(pos.toLowerCase()){
		case "row":
//			System.out.println("parsing row");
			while(s.charAt(ptr)!=','){
				ptr++;
			}
			ptr++;
//			System.out.println(ptr);
			while(s.charAt(ptr)!= ',' && s.charAt(ptr)!='/' ){
//				System.out.println(ptr+","+s.charAt(ptr));
				result.append(s.charAt(ptr));
				ptr++;
			}
//			System.out.println(result.toString());
			return MapDescriptor.getMapRow(Integer.parseInt(result.toString()));
		case"col":
			ptr=2;
			while(s.charAt(ptr)!= ','){
				result.append(s.charAt(ptr));
				ptr++;
			}
//			System.out.println(result.toString());
			return MapDescriptor.getMapRow(Integer.parseInt(result.toString()));
		default:
			System.out.println("Could not read coordinates");
			return -1;
		}
	}
	
	public static String encodeCoor(int row, int col){
		StringBuilder sb = new StringBuilder();
		sb.append(row);
		sb.append(",");
		sb.append(col);
		sb.append("/");
		return sb.toString();
	}
	
	public static String encodeCoor(int row, int col, int dir){
		StringBuilder sb = new StringBuilder();
		sb.append(row);
		sb.append(",");
		sb.append(col);
		sb.append(",");
		sb.append(dir);
		sb.append("/");
		return sb.toString();
	}
}
