package main;


public class SynchedContainer {
	private Job innerJob=null;
	
	public synchronized Job extractWork() throws InterruptedException{
		while(innerJob==null){
			wait();
		}
		Job tmp = innerJob;
		innerJob = null;
		return tmp;
	}
	
	public synchronized void loadWork(Job j){
		innerJob=j;
		notify();
	}
	 
}
