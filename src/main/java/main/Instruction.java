package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Instruction<T> implements Runnable{

	private final SynchronizedLinkedList workQueue;
	private Boolean minionStatus;
	private Boolean stopFlags;
	private int runId;
	
	private final Lock listLock;
	private final Condition noJobs;
	private final Condition wakeUpBoss;
	
	public Instruction(SynchronizedLinkedList w, Boolean minion, Boolean s, int r){
		this.workQueue=w;
		this.minionStatus=minion;
		this.stopFlags=s;
		listLock = workQueue.getLock();
		noJobs = workQueue.getNoJobsCondition();
		wakeUpBoss = workQueue.getNewJobsCondition();
		this.runId=r;
	}
	
	@Override
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
