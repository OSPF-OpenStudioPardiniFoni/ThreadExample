package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SynchronizedLinkedList{

	private LinkedList<Job> list; 
	
	final Lock listLock = new ReentrantLock();
	final Condition noJobs = listLock.newCondition();
	final Condition newJobs = listLock.newCondition();
	
	
	public SynchronizedLinkedList(){
		list = new LinkedList<Job>();
	}
	
	public synchronized void syncAdd(Job elem){
		list.addLast(elem);
	}
	
	public synchronized Job syncGet(){
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
