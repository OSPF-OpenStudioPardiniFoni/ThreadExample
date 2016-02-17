package main;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Master extends Thread {
	private Thread[] minions;
	private SyncStatus minionsStatus;
	private WorkQueue[] minionsUploadBigWorksQueues;
	private WorkQueue[] minionsUploadSmallWorksQueues;
	private WorkQueue bigWorkQueue;
	private WorkQueue smallWorkQueue;
	private SynchedContainer[] currentWorks;
	
	public Master(Double first){
		int proc = 4;//Runtime.getRuntime().availableProcessors();
		minions = new Thread[proc];
		minionsStatus=new SyncStatus(proc);
		
		minionsUploadBigWorksQueues=new WorkQueue[proc];
		minionsUploadSmallWorksQueues= new WorkQueue[proc];
		
		bigWorkQueue=new WorkQueue(first);
		smallWorkQueue = new WorkQueue();
		
		//test
		bigWorkQueue.push(Math.random());
		bigWorkQueue.push(Math.random());
		//
		currentWorks=new SynchedContainer[proc];
		
		for(int i=0;i<proc;i++){
			minionsUploadBigWorksQueues[i]= new WorkQueue();
			minionsUploadSmallWorksQueues[i]= new WorkQueue();
			currentWorks[i]=new SynchedContainer();
			minions[i]=new Minion(i, minionsStatus,minionsUploadBigWorksQueues[i], minionsUploadSmallWorksQueues[i],currentWorks[i]);
		}
		
	}
	
	public void startMinions(){
		
		for(int i=0;i<minions.length;i++){
			minions[i].start();
		}
		
	}
	
	@Override
	public void run(){
		System.out.println("Master: Lancio i minions!");
		startMinions();
		currentWorks[0].loadWork(bigWorkQueue.pop());
		
		//timeLog
		long t1;
		long t2;
		double sum =0;
		double wakesUp = 0;
		
		
		while(true){
			// Aspetto che almeno un mionion torni Idle
			minionsStatus.waitForIdles();
			
			//TimeLog
			t1= System.currentTimeMillis();
			wakesUp=wakesUp+1;
			
			//wakeUp
			if(minionsStatus.areAllIdle()&&bigWorkQueue.isEmpty()){
				boolean test=true;
				for(int i=0; i<minionsUploadBigWorksQueues.length;i++){
					test=test&&minionsUploadBigWorksQueues[i].isEmpty();
				}
				if(test==true){
					break;
				}
			}
			
			LinkedList<Integer> IdleIDs = minionsStatus.getIdleList();
			
			if(IdleIDs != null){
			
				ListIterator<Integer> iter = IdleIDs.listIterator();
				int id;
				while(iter.hasNext()){
					id=iter.next().intValue();
					bigWorkQueue.addAll(minionsUploadBigWorksQueues[id]);
					minionsUploadBigWorksQueues[id].clearList();
				}
				iter = IdleIDs.listIterator();
				while(iter.hasNext()){
					id=iter.next().intValue();
					if(!bigWorkQueue.isEmpty()){
						
						minionsStatus.setRunning(id);
						currentWorks[id].loadWork(bigWorkQueue.pop());
						
					}
				}
			}
			
			//TimeLog
			t2 = System.currentTimeMillis();
			sum+=(t2-t1);
			
		}
		
		// Abbiamo la condizione di terminazio
		// diamo l'interrupt a tutti i minions.
		
		for(int i=0;i<minions.length;i++){
			minions[i].interrupt();
		}
		
		System.out.println("Master: wakedUp = "+wakesUp+" avg WakedUp = "+(sum/wakesUp));
		
		if(bigWorkQueue.isEmpty()){
			System.out.println("OK coda vuota");
		}
		System.out.println("MASTER FINISHED");
	}
	
	
	
	
}

