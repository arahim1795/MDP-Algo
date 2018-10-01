import java.io.IOException;
import java.util.concurrent.*;

import utility.Comms;

/**
 * @author ARAHIM-WPC
 */
public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		String mail;
		
		Comms.openSocket();
		
		boolean test = Comms.sendMsg("TSDF");
		System.out.println(test);
		
		do {
			mail = Comms.receiveMsg();
		}while(mail==null);
		System.out.println(mail);
		
		
		boolean fun = Comms.sendMsg(".");
		System.out.println(fun);
		
		do {
			mail = Comms.receiveMsg();
		} while (mail==null);
		System.out.println(mail);
		
		TimeUnit.MILLISECONDS.sleep(5000);
		Comms.closeSocket();
		
		
	}

}
