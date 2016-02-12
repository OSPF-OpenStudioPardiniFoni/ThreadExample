package main;


public class SynchedContainer {
	private Double innerData=null;
	
	public synchronized Double extractWork() throws InterruptedException{
		while(innerData==null){
			wait();
		}
		Double tmp = innerData;
		innerData=null;
		return tmp;
	}
	
	public synchronized void loadWork(Double d){
		innerData=d;
		notify();
	}
	
	 
}
