package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.logging.Handler;

import javax.security.auth.callback.Callback;

public class WorkerThread extends Thread{

	private final int myID;
	private final SynchronizedLinkedList workQueue;
	private Boolean minionStatus;
	private Boolean stopFlags;
	private int runId;
	
	private final Lock listLock;
	private final Condition noJobs;
	private final Condition wakeUpBoss;
	
	
	public WorkerThread(int id, int numMinions, WorkerThread[] minions, Boolean minionsStatus, Boolean stopFlags, SynchronizedLinkedList workQueue){
		this.workQueue=workQueue;
		this.minionStatus=minionsStatus;
		this.stopFlags=stopFlags;
		listLock = workQueue.getLock();
		noJobs = workQueue.getNoJobsCondition();
		wakeUpBoss = workQueue.getNewJobsCondition();
		myID=id;
	}
	
	public void run(){
		while(true){
			
			if(stopFlags==Boolean.TRUE) break; 
			
			Job myJob = workQueue.syncGet();
			
		//	if(myJob==null){
				
		/*		minionStatus= Boolean.FALSE;
				listLock.lock();
				
				try{
					System.out.println("ID="+runId+" : invia signal al boss");
					wakeUpBoss.signal();
					noJobs.wait();
					
					
				}catch(InterruptedException e){}
				
				finally{
					listLock.unlock();
				}*/
		//	}else{
				// consuma
			System.out.println("ID="+runId+" : estrae job utile dalla workQueue");
			minionStatus= Boolean.TRUE;
			System.out.println("ID="+runId+" : job="+myJob.toString());
		//	}
			
			
			
		}
		minionStatus= Boolean.FALSE;
		System.out.println("ID="+runId+" finito");
		
	}
	
	
}
