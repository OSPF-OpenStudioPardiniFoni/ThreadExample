package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class WorkerThread<T> extends Thread{

	private final SynchronizedLinkedList<T> workQueue;
	private Boolean[] minionsStatus;
	private Boolean[] stopFlags;
	private final int myID;
	
	private final Lock listLock;
	private final Condition noJobs;
	private final Condition newJobs;
	
	public WorkerThread(SynchronizedLinkedList<T> workQueue, Boolean[] minionsStatus, Boolean[] stopFlags, int id){
		this.workQueue=workQueue;
		this.minionsStatus=minionsStatus;
		this.stopFlags=stopFlags;
		this.myID = id;
		
		listLock = workQueue.getLock();
		noJobs = workQueue.getNoJobsCondition();
		newJobs = workQueue.getNewJobsCondition();
	}
	
	@Override
	public void start(){
	
		while(true){
			// acquisci un lock sullo stopFlags
				//se e' true allora break e fine
			// rilascia stopFlag
			if(stopFlags[myID]==Boolean.TRUE) break; 
			
			
			// acquisisci lock sulla workQueue
				// testa la condizione vuota
					// SI: wait
				// estrai dalla coda il job
			// rilascia la coda
			T myJob = workQueue.syncGet();
			if(myJob==null){
				listLock.lock();
				try{
					noJobs.wait();
					
				}catch(InterruptedException e){}
				finally{
					listLock.unlock();
				}
			}else{
				// consuma
				
				System.out.println("ID="+myID+" : job="+myJob.toString());
			}
			
			
		}
		
	}
	
}
