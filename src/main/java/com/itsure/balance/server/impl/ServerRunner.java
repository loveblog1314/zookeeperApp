package com.itsure.balance.server.impl;

import com.itsure.balance.IServer;
import com.itsure.balance.ServerData;
import java.util.ArrayList;
import java.util.List;


public class ServerRunner {
	
    private static final int  SERVER_QTY = 5;
    private static final String  ZOOKEEPER_SERVER = "101.132.134.183:2181";
    private static final String  SERVERS_PATH = "/servers";
	
	public static void main(String[] args) {
		
		List<Thread> threadList = new ArrayList<Thread>(); 
		
		for(int i = 0; i < SERVER_QTY; i++){
			
			final Integer count = i;
			Thread thread = new Thread(new Runnable() {
				
				public void run() {		
					ServerData serverData= new ServerData();
					serverData.setBalance(0);
					serverData.setHost("localhost");
					serverData.setPort(6000+count);
					IServer server = new ServerImpl(ZOOKEEPER_SERVER, SERVERS_PATH, serverData);
					server.bind();					
				}
			});			
			threadList.add(thread);
			thread.start();
		}
		
		for (int i = 0; i < threadList.size(); i++){
			try {
				threadList.get(i).join();
			} catch (InterruptedException ignore) {
				//
			}
			
		}
		

	}

}
