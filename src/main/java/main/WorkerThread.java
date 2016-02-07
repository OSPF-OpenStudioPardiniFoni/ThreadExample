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
	private final Condition wakeUpBoss;
	
	public WorkerThread(SynchronizedLinkedList<T> workQueue, Boolean[] minionsStatus, Boolean[] stopFlags, int id){
		this.workQueue=workQueue;
		this.minionsStatus=minionsStatus;
		this.stopFlags=stopFlags;
		this.myID = id;
		
		listLock = workQueue.getLock();
		noJobs = workQueue.getNoJobsCondition();
		wakeUpBoss = workQueue.getNewJobsCondition();
	}
	
	@Override
	public void start(){
	
		while(true){
			
			if(stopFlags[myID]==Boolean.TRUE) break; 
			
			T myJob = workQueue.syncGet();
			
			try{
				sleep(500);
			}catch(InterruptedException e){}
			
			if(myJob==null){
				
				minionsStatus[myID]= Boolean.FALSE;
				listLock.lock();
				
				try{
					System.out.println("ID="+myID+" : invia signal al boss");
					sleep(1000);
					wakeUpBoss.signal();
					//noJobs.wait();
					
					
				}catch(InterruptedException e){}
				
				finally{
					listLock.unlock();
				}
			}else{
				// consuma
				System.out.println("ID="+myID+" : estrae job utile dalla workQueue");
				minionsStatus[myID]= Boolean.TRUE;
				System.out.println("ID="+myID+" : job="+myJob.toString());
			}
			
			
		}
		minionsStatus[myID]= Boolean.FALSE;
		System.out.println("ID="+myID+" finito");
		
	}
	
}
