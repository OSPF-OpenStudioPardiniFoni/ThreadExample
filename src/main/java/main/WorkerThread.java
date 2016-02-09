package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Handler;

import javax.security.auth.callback.Callback;

public class WorkerThread extends Thread{

	private final int myID;

	
	public WorkerThread(int id, Runnable r){
		super(r);
		myID=id;
	}
	
}
