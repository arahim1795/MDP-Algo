package utility;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import robot.RobotConstant;
import robot.RobotConstant.DIRECTION;

/**
 * @author 18/19 S1 G3
 * Tutorial: https://www.javaworld.com/article/2077322/core-java/core-java-sockets-programming-in-java-a-tutorial.html
 */
public class Comms {

	// Receipt Strings



	// Major Headers
	public static final String ARDnAND = "CCCC";
	public static final String RPI = "RRRR";

	// android
	public static final String an = "BBBB";
	// pc > an
	public static final String anMdf = "BBBB#mdf";		// + "/", send map descriptor 
	public static final String anPos = "BBBB#setrobot:";	// + "/", send current bot position
	// an > pc
	public static final String anDone = "#done";
	public static final String anEx = "#exp";
	public static final String anFp = "#fp";
	public static final String anWp = "mp"; // set way point
	public static final String anSp = "sp"; // set start point
	public static final String anStart = "r1";
	public static final String anStop = "r0";

	// arduino
	public static final String ar = "AAAA";
	// pc > ar
	public static final String arIns = "INSTR";      // PC>Arduino - Give Instruction
	public static final String arEnd = "END";
	public static final String arSense = "C";
	public static final String arDone = "done";
	public static final String arData = "sdata";
	public static final String arCal = "S";
	public static final char charCal = 'S';

	// arduino & android
	public static final String MULTI = "MULTI"; //for mutli-movement string

	// RPi Headers
	// pc > rpi
	public static final String rpCam = "CAM";

	// Robot Information
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
		case ar:
			sb.append(major);
			sb.append("_");
			switch (sub) {
			case arEnd:
			case arIns:
			case arSense:
			case arCal:
			case MULTI:
			case "E":
				sb.append(sub);
				sb.append("_");
				break;
			default:
				System.out.println("Invalid Purpose");
				return false;
			}
			break;
		case an:
			switch (sub) {
			case anMdf:
			case anPos:
			case anFp:
			case MULTI:
				sb.append(sub);
				break;
			default:
				System.err.println("Invalid Purpose");
				return false;
			}
			break;
		case ARDnAND:
			sb.append(major);
			switch (sub) {
			case MULTI:
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
			case rpCam:
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
			os.flush();
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
		String msg;
		StringBuilder msgBuilder = new StringBuilder();

		try {
			msgBuilder.append(is.readLine());
		} catch (IOException e) {
			System.err.println(e);
		}

		msg = msgBuilder.toString().toLowerCase();
		System.out.println(msg);

		if (msg.contains(";")) {
			String[] strArr = msg.split(";");
			StringBuilder outMsg = new StringBuilder();
			for (String s : strArr) {
				outMsg.append(s);
				outMsg.append(";");
			}
			return outMsg.toString().trim();
		}

		if (msg.contains("#")) {
			String[] strArr = msg.split("'");
			return strArr[1];
		}

		/*
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < msg.length(); i++) {
			if (i < 2 || i == msg.length()-1);
			else result.append(msg.charAt(i));
		}

		return result.toString();
		 */

		return "Error";

	}

	/**
	 * Returns true if connection and communication streams to Pi are active
	 * @return true if connection and communication streams are active, false otherwise
	 */
	public static boolean connectionActive() {
		return robotComms == null && is == null && os == null;
	}

	public static int readCoor(String pos, String s){
		int ptr=0;
		StringBuilder result = new StringBuilder("");
		switch(pos.toLowerCase()){

		case "col":
			// System.out.println("parsing row");
			while(s.charAt(ptr)!=','){
				ptr++;
			}
			ptr++;
			// System.out.println(ptr);
			while(s.charAt(ptr)!= ',' && s.charAt(ptr)!='/' ){
				// System.out.println(ptr+","+s.charAt(ptr));
				result.append(s.charAt(ptr));
				ptr++;
			}

			// System.out.println(result.toString());
			return MapDescriptor.getMapCol(Integer.parseInt(result.toString()));
		case"row":

			ptr=2;
			while(s.charAt(ptr)!= ','){
				result.append(s.charAt(ptr));
				ptr++;
			}

			// System.out.println(result.toString());
			return MapDescriptor.getMapRow(Integer.parseInt(result.toString()));
		case "dir":
			ptr = s.length()-2;
			result.append(s.charAt(ptr));
			return Integer.parseInt(result.toString());

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

	public static String getArdReceipt(String expMsg) {
		String str;
		String[] strArr;
		while (true) {
			str = Comms.receiveMsg();
//			System.out.println("gate1: " + str);
//			System.out.println("gate2: " + expMsg);
			System.out.println(str);
			strArr = str.split(";");
			if (strArr[1].equals(expMsg.toLowerCase())) break;
		}
		return str;
	}

	public static String getAndReceipt(String expMsg) {
		String str;
		while (true) {
			str = Comms.receiveMsg().toLowerCase();
			System.out.println(str);
			if (str.equals(expMsg.toLowerCase())) break;
		}
		return str;
	}

	public static void sleepWait() {
		try {
			TimeUnit.MILLISECONDS.sleep(150);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
