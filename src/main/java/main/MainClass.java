package main;

public class MainClass {
	public static void main(String[] args)throws InterruptedException{
		Double first=Double.valueOf(0.3);
		Master m = new Master(first);
		m.start();
		m.join();
		System.out.println("Main terminato");
	}
}
