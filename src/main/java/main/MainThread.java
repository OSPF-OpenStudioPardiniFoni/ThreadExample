package main;

import java.lang.Integer;

public class MainThread {

	public static void main(String args[]){
		
		Job<String> first = new Job<String>(new String("ciao"));
		
		DynamicExecutorService boss = new  DynamicExecutorService(first);
		
		System.out.println("prima di boss start");
		boss.start();
		System.out.println("subito dopo boss start");
		
		try{
			boss.join();
		}catch(Exception e){
			
		}
		
		System.out.println("Main finito");
		
	}
	
}
