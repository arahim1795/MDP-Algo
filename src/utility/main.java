import Comms;

public static void main(String[] args){
		Comms temp = new Comms();
		temp.openSocket();
		temp.sendMsg("test string");
		temp.closeSocket();
	}