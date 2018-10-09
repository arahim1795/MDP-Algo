import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import map.Map;

import robot.Robot;

import search.Explore;

import utility.Utility;
import utility.Comms;

/**
 * @author ARAHIM-WPC
 */
public class Main {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		/*
		String filename = "./map/test_1.txt";
		List<String> mapArray = Utility.getmap(filename);
		
		Robot bot = new Robot(18, 1, false);
		
		Map map = new Map(bot);
		map.parseMap(mapArray);
		map.printMap();
		
		if (map.getTile(0, 0).isObstacle()) System.out.println("Obstacle at 0,0");
		
		Explore ex = new Explore(bot, map, 50);
		
		ex.setupExplore();
		
		*/
		
		// Messages Tracker
		ArrayList<String> msgList = new ArrayList<String>();
		boolean msgSent = false;
		String msg = "B#setrobot:4,5,4/\n";
		int msgCount = 0;
		String tmp;
		
		// Establish Connection	
		Comms.openSocket();
		
		// Receive Start Message
		/*do {
			tmp = Comms.receiveMsg();
			System.out.println(tmp);
		} while (tmp == null);*/
		//msgList.add(tmp); 
		msgCount++;
		
		if (msgCount == 1) System.out.println("Initialising Start State");
		
		// 5 sec wait (as if to simulate start)
		TimeUnit.MILLISECONDS.sleep(5000);
		
		while (true) {
			do {
				if(!msgSent){
					Comms.sendMsg(msg);
					msgSent = true;
				}
				tmp = Comms.receiveMsg();
				System.out.println(tmp);
			} while (tmp == null);
			msgList.add(tmp); msgCount++;
		}
		
		
	}

}
