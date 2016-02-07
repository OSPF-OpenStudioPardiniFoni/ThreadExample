package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DynamicExecutorService<T> extends Thread {

	private int numMinions;
	private WorkerThread[] minions;
	private Boolean[] minionsStatus; // false = idle
	private Boolean[] stopFlags;
	
	private SynchronizedLinkedList<T> workQueue;
	private Lock workQueueLock;
	private Condition isEmpty;
	private Condition newJobs;
	
	/**
	 * Returns a new DynamicExecutorService. The firstJob is 
	 * already in the work queue and the number of threads is 
	 * equal to the number of avaible cores.  
	 * @param firstJob
	 */
	public DynamicExecutorService(T firstJob){
		
		workQueue = new SynchronizedLinkedList<T>();
		workQueue.syncAdd(firstJob);
		
		numMinions = Runtime.getRuntime().availableProcessors();
		
		minions = new WorkerThread[numMinions];
		minionsStatus = new Boolean[numMinions];
		
		for(int i=0; i<numMinions; i++){
			minions[i]=new WorkerThread(workQueue, minionsStatus, stopFlags, i);
			minionsStatus[i] = Boolean.FALSE;
			stopFlags[i] = Boolean.FALSE;
		}
		
		workQueueLock = workQueue.getLock();
		isEmpty = workQueue.getNoJobsCondition();
		newJobs = workQueue.getNewJobsCondition();
		
	}
	
	/**
	 * Add a new job BEFORE running the DynamicExecutorService
	 * @param newJob the job to add
	 */
	public void addJob(T newJob){
		workQueue.syncAdd(newJob);
	}
	
	private void startAllMinions(){
		for(WorkerThread wt : minions){ wt.start(); };
	}
	
	@Override
	public void start(){
		this.startAllMinions();
		
		while(true){
			workQueueLock.lock();
			try{
				while(!workQueue.syncIsEmpty()) isEmpty.wait();
				// la coda e' vuota
				boolean notAllIdle = false;
				for(int i=0; i<minionsStatus.length; i++) 
					notAllIdle = notAllIdle || minionsStatus[i].booleanValue();
				if(!notAllIdle){
					for(int i=0; i<stopFlags.length; i++)
						stopFlags[i] = Boolean.TRUE;
					//signalALL
					break;
				}
				
			}catch(InterruptedException e){}
			finally{
				workQueueLock.unlock();
			}
			
		}
		System.out.println("ExecutorService Finito");
		
	}
	
}
