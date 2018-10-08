package utility;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @author 18/19 S1 G3
 * Tutorial: https://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html
 */
public class Comms {
	
	// Major Headers
	public static final String ARDUINO = "A";
	public static final String ANDROID = "B";
	public static final String ARDnAND = "C";
	public static final String RPI = "R";
	
	// Android Headers
	// - to
	public static final String MAP = "#mdf";		// send map descriptor
	public static final String POS = "#setrobot";	// send current bot position
	// - from
    public static final String EX_S = "ex";	// Android>PC - Start Exploration
    public static final String FP_S = "fp";	// Android>PC - Start Fastest Path
    public static final String START = "r1";
    public static final String STOP = "r0";
	
	// Arduino Headers
    // - to
    public static final String SET = "SETBT";     // PC>Arduino - Set-Up Bot
    public static final String INS = "INSTR";      // PC>Arduino - Give Instruction
    // - from
    public static final String SENSOR_DATA = "SDATA";       // Arduino>PC - Sensor Data
    
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
	 * Sends messages to Pi
	 * @param msg Message to send
	 * @return true if message is successfully sent, false otherwise
	 */
	public static boolean sendMsg(String major, String sub, String content) {
		
		StringBuilder sb = new StringBuilder();
		
		if (major == null || sub == null) 
		
		if (!major.equals("A") || !major.equals("B") || !major.equals("C") || !major.equals("R")) {
			System.out.println("Invalid Destination");
			return false;
		}
		
		if (major == null || major.equals("") || sub == null || sub.equals("")) {
			System.out.println("Cannot send without Destination and Purpose");
			return false; 
		}
		
		sb.append(major);
		sb.append(sub); 
		
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
		try {
			msg.append(is.readLine());
			
		} catch (IOException e) {
			System.err.println(e);
		}
		return msg.toString();
	}
	
	/**
	 * Returns true if connection and communication streams to Pi are active
	 * @return true if connection and communication streams are active, false otherwise
	 */
	public static boolean connectionActive() {
		return robotComms == null && is == null && os == null;
	}
	
}
