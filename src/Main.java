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
		
		while (true) {
			do {
				tmp = Comms.receiveMsg();
				System.out.println(tmp);
			} while (tmp == null);
			msgList.add(tmp); msgCount++;
		}
		
		
	}

}
