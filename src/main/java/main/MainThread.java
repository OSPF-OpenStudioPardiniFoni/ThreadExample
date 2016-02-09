package main;

import java.lang.Integer;

public class MainThread {

	public static void main(String args[]){
		
		Job<String> jobs[] = new Job[10];
		for(int j=0; j<jobs.length; j++) jobs[j]=new Job<String>(new String(Integer.valueOf(j*10).toString()));
		Job<String> first = new Job<String>(new String("ciao"));
		
		DynamicExecutorService<Job> boss = DynamicExecutorService.getNewService<Job>(first);
		for(Job<String> j : jobs) boss.addJob(j);
		
		boss.start();
		try{
			boss.join();
		}catch(Exception e){
			
		}
		
		
		System.out.println("Main finito");
		
	}
	
}
