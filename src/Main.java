import java.io.IOException;

import utility.Comms;

/**
 * @author ARAHIM-WPC
 */
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Comms cm = new Comms();
		cm.openSocket();
		
		boolean test = cm.sendMsg("A");
		System.out.println(test);
		cm.closeSocket();
		
	}

}
