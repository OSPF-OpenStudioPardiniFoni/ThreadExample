package main;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Master extends Thread {
	private Thread[] minions;
	private SyncStatus minionsStatus;
	private WorkQueue[] minionsQueues;
	private WorkQueue bigWork;
	private SynchedContainer[] currentWorks;
	
	public Master(Double first){
		int proc = 8;//Runtime.getRuntime().availableProcessors();
		minions = new Thread[proc];
		minionsStatus=new SyncStatus(proc);
		minionsQueues=new WorkQueue[proc];
		bigWork=new WorkQueue(first);
		currentWorks=new SynchedContainer[proc];
		
		for(int i=0;i<proc;i++){
			minionsQueues[i]= new WorkQueue();
			currentWorks[i]=new SynchedContainer();
			minions[i]=new Minion(i, minionsStatus,minionsQueues[i], currentWorks[i]);
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
		currentWorks[0].loadWork(bigWork.pop());
		while(true){
			// Aspetto che almeno un mionion torni Idle
			minionsStatus.waitForIdles();
			
			//random sleep
			if(Math.random()>0.7){
				int j;
				for(int i=0; i<100000; i++){
					j=i;
				}
			}
			
			if(minionsStatus.areAllIdle()&&bigWork.isEmpty()){
				boolean test=true;
				for(int i=0; i<minionsQueues.length;i++){
					test=test&&minionsQueues[i].isEmpty();
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
					bigWork.addAll(minionsQueues[id]);
					minionsQueues[id].clearList();
				}
				iter = IdleIDs.listIterator();
				while(iter.hasNext()){
					id=iter.next().intValue();
					if(!bigWork.isEmpty()){
						
						minionsStatus.setRunning(id);
						currentWorks[id].loadWork(bigWork.pop());
						
					}
				}
			}
			
			
			
		}
		
		// Abbiamo la condizione di terminazione
		// diamo l'interrupt a tutti i minions.
		
		for(int i=0;i<minions.length;i++){
			minions[i].interrupt();
		}
		
		if(bigWork.isEmpty()){
			System.out.println("OK coda vuota");
		}
		System.out.println("MASTER FINISHED");
	}
	
	
	
	
}

