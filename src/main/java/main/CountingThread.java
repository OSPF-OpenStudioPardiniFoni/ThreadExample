package main;

class CountingThread extends Thread{
	
	private Counter counter;
	
	public CountingThread(Counter c){
		counter=c;
	}
	
	public void run(){
		for(int x=0; x<10000; ++x){ 
			counter.increment();
			System.out.println(Thread.currentThread().getName());
		}
	}
	
}