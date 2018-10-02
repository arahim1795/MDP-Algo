import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

import utility.Comms;

/**
 * @author ARAHIM-WPC
 */
public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		// Messages Tracker
		ArrayList<String> msgList = new ArrayList<String>();
		int msgCount = 0;
		String tmp;
		
		// Establish Connection	
		Comms.openSocket();
		
		// Receive Start Message
		do {
			tmp = Comms.receiveMsg();
			System.out.println(tmp);
		} while (tmp == null);
		msgList.add(tmp); msgCount++;
		
		if (msgCount == 1) System.out.println("Initialising Start State");
		
		// 5 sec wait (as if to simulate start)
		TimeUnit.MILLISECONDS.sleep(5000);
		
		// Send a message
		boolean test = Comms.sendMsg("TSDF");
		if(test) {
			System.out.println("Successful Message Transmission");
			do {
				tmp = Comms.receiveMsg();
				System.out.println(tmp);
			} while (tmp == null);
			msgList.add(tmp); msgCount++;
		}
		
		// Send closing message and close socket if successful
		boolean fun = Comms.sendMsg(".");
		if(test) {
			System.out.println("Successful Message Transmission");
			do {
				tmp = Comms.receiveMsg();
				System.out.println(tmp);
			} while (tmp == null);
			msgList.add(tmp); msgCount++;
			TimeUnit.MILLISECONDS.sleep(2000); // wait 2 seconds
			Comms.closeSocket();
		}
		
		// if failure, close after a specific time wait 
		TimeUnit.MILLISECONDS.sleep(5000); // wait 5 seconds
		Comms.closeSocket();
		
		
	}

}
