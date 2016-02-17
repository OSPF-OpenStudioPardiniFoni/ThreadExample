package main;

public class MainClass {
	public static void main(String[] args)throws InterruptedException{
		Double first=Double.valueOf(0.3);
		Master m = new Master(first);
		
		long t1 = System.currentTimeMillis();
		
		m.start();
		m.join();
		
		long t2 = System.currentTimeMillis();
		System.out.println("Total Time = "+(t2-t1));
		
		System.out.println("Main terminato");
	}
}
