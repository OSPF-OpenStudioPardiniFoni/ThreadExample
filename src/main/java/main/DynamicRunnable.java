package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DynamicRunnable implements Runnable{

	private int numMinions;
	private WorkerThread[] minions;
	private Boolean[] minionsStatus; // false = idle
	private Boolean[] stopFlags;
	
	private SynchronizedLinkedList workQueue;
	private Lock workQueueLock;
	private Condition isEmpty;
	private Condition wakeUpBoss;
	
	public DynamicRunnable(int numMinions, WorkerThread[] minions, Boolean[] minionsStatus, Boolean[] stopFlags, SynchronizedLinkedList workQueue){
		this.numMinions=numMinions;
		this.minions=minions;
		this.minionsStatus=minionsStatus;
		this.stopFlags=stopFlags;
		this.workQueue=workQueue;
		
		workQueueLock = workQueue.getLock();
		isEmpty = workQueue.getNoJobsCondition();
		wakeUpBoss = workQueue.getNewJobsCondition();
		
		
		// test - aggiungo qui altri job
		Job<String> j;
		for(int x=0; x<10; x++){
			j = new Job<String>(new String("ciaone"+x));
			workQueue.syncAdd(j);
		}
	}
	
	private void startAllMinions(){
		for(int i=0; i<minions.length;i++){ minions[i].start(); };
	}
	
	@Override
	public void run(){
		System.out.println("ExecutorService lancia i minions");
		this.startAllMinions();
		
		while(true){
			System.out.println("ExecutorService tenta il lock");
			workQueueLock.lock();
			try{
				
				System.out.println("ExecutorService si addormenta");
				
				wakeUpBoss.wait();
				
				System.out.println("ExecutorService si sveglia");
				
				boolean notAllIdle = false;
				for(int i=0; i<minionsStatus.length; i++) 
					notAllIdle = notAllIdle || minionsStatus[i].booleanValue();
				if(!notAllIdle){
					
					for(int i=0; i<stopFlags.length; i++)
						stopFlags[i] = Boolean.TRUE;
					
					isEmpty.signalAll();	
					break;
				}
				
			}catch(InterruptedException e){}
			finally{
				workQueueLock.unlock();
			}
			
		}
		workQueueLock.unlock();
		System.out.println("ExecutorService Finito");
		//signal per il main
		
	}
}
