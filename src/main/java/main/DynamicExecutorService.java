package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DynamicExecutorService<T> extends Thread{


	
	/**
	 * Returns a new DynamicExecutorService. The firstJob is 
	 * already in the work queue and the number of threads is 
	 * equal to the number of avaible cores.  
	 * @param firstJob
	 */
	public static DynamicExecutorService getNewService(T firstJob){ 
		
		SynchronizedLinkedList<T> workQueue = new SynchronizedLinkedList<T>();
		workQueue.syncAdd(firstJob);
		
		int numMinions = Runtime.getRuntime().availableProcessors();
		System.out.println("numero processori = "+numMinions);
		WorkerThread[] minions = new WorkerThread[numMinions];
		Boolean[] minionsStatus = new Boolean[numMinions];
		Boolean[] stopFlags = new Boolean[numMinions];
		
		
		for(int i=0; i<numMinions; i++){
			Instruction r=new Instruction(workQueue, minionsStatus[i], stopFlags[i], i);
			minions[i]=new WorkerThread(i,r);
			minionsStatus[i] = Boolean.FALSE;
			stopFlags[i] = Boolean.FALSE;
		}
		
		
		DynamicRunnable master= new DynamicRunnable(numMinions, minions, minionsStatus, stopFlags, workQueue);
		DynamicExecutorService ret= new DynamicExecutorService(master);
		return ret;
		
	}
	
	public DynamicExecutorService(Runnable master){
		super(master);
	}
	
	/**
	 * Add a new job BEFORE running the DynamicExecutorService
	 * @param newJob the job to add
	 */
	public void addJob(T newJob){
		workQueue.syncAdd(newJob);
	}
	
	
	
	@Override
	public void start(){
		
		
	}
	
}
