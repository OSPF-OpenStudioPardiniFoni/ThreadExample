package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SynchronizedLinkedList<X>{

	private LinkedList<X> list; 
	
	final Lock listLock = new ReentrantLock();
	final Condition noJobs = listLock.newCondition();
	final Condition newJobs = listLock.newCondition();
	
	
	public SynchronizedLinkedList(){
		list = new LinkedList<X>();
	}
	
	public synchronized void syncAdd(X elem){
		list.addLast(elem);
	}
	
	public synchronized X syncGet(){
		if(!list.isEmpty()){
			return list.removeFirst(); 
		}else{
			return null;
		}
	}
	
	public synchronized boolean syncIsEmpty(){
		return list.isEmpty();
	}
	
	public synchronized Lock getLock(){
		return listLock;
	}
	
	public synchronized Condition getNoJobsCondition(){
		return noJobs;
	}
	
	public synchronized Condition getNewJobsCondition(){
		return newJobs;
	}
	
}
