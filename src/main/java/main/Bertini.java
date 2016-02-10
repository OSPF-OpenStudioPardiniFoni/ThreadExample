package main;

public class Bertini  {

	public static void main(String[] args) throws InterruptedException{
		
		final Counter counter = new Counter();
		
		class ReadingThread extends Thread{
			public void run(){
				System.out.println(counter.getCount());
			}
		}
		
		CountingThread t1 = new CountingThread(counter);
		CountingThread t2 = new CountingThread(counter);
		
		ReadingThread t3 = new ReadingThread();
		
		t1.start();
		t2.start();
		t3.start();
		
		t1.join();
		t2.join();
		t3.join();
		
		System.out.println(counter.getCount());
		
	}
	
}

