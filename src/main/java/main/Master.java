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
	
	//BUG 2: degli oggetti Integer non puo' essere passato il riferimento in Java 
	private SharedVariable sharedVariableOwner;
	
	private LinkedList<Double> sharedVariable;
	
	public Master(Job first){
		int proc = Runtime.getRuntime().availableProcessors();
		minions = new Thread[proc];
		minionsStatus=new SyncStatus(proc);
		
		minionsUploadBigWorksQueues=new WorkQueue[proc];
		minionsUploadSmallWorksQueues= new WorkQueue[proc];
		
		bigWorkQueue=new WorkQueue(first);
		smallWorkQueue = new WorkQueue();
		
		//test
		bigWorkQueue.push(new Job(Double.valueOf(2),true));
		bigWorkQueue.push(new Job(Double.valueOf(3),true));
		//
		currentWorks=new SynchedContainer[proc];
		
		// nessuno possiede la shared variable
		sharedVariableOwner= new SharedVariable(); // gia' a -1
		
		sharedVariable = new LinkedList<Double>();
		
		for(int i=0;i<proc;i++){
			minionsUploadBigWorksQueues[i]= new WorkQueue();
			minionsUploadSmallWorksQueues[i]= new WorkQueue();
			currentWorks[i]=new SynchedContainer();
			minions[i]=
					new Minion(
							i, 
							minionsStatus,
							minionsUploadBigWorksQueues[i], 
							minionsUploadSmallWorksQueues[i],
							currentWorks[i], 
							sharedVariableOwner, 
							sharedVariable
							/* nuove non toccare*/
							//new RegenerativeComponentsFactory f ,
							//copyOf PetriNet );
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
			if(minionsStatus.areAllIdle()&&bigWorkQueue.isEmpty()&&smallWorkQueue.isEmpty()&&sharedVariableOwner.getID()==-1){
				boolean test=true;
				for(int i=0; i<minionsUploadBigWorksQueues.length;i++){
					test=test&&minionsUploadBigWorksQueues[i].isEmpty();
					// BUG 1
					test=test&&minionsUploadSmallWorksQueues[i].isEmpty();
				}
				if(test==true){
					break;
				}
			}
			
			LinkedList<Integer> idleIDs = minionsStatus.getIdleList();
			
			if(idleIDs != null){
			
				Integer idleArray[] = new Integer[idleIDs.size()];
				idleArray = idleIDs.toArray(idleArray);
				int index = 0;
				
				int id;
				for(index=0; index<idleArray.length; index++){
					id=idleArray[index];
					bigWorkQueue.addAll(minionsUploadBigWorksQueues[id]);
					minionsUploadBigWorksQueues[id].clearList();
					
					//uppare le code small e svuotarle
					smallWorkQueue.addAll(minionsUploadSmallWorksQueues[id]);
					minionsUploadSmallWorksQueues[id].clearList();
					
				}
				
				/*ListIterator<Integer> iter = idleIDs.listIterator();
				int id;
				while(iter.hasNext()){
					id=iter.next().intValue();
					bigWorkQueue.addAll(minionsUploadBigWorksQueues[id]);
					minionsUploadBigWorksQueues[id].clearList();
					
					//uppare le code small e svuotarle
					smallWorkQueue.addAll(minionsUploadSmallWorksQueues[id]);
					minionsUploadSmallWorksQueues[id].clearList();
					
				}
				iter = idleIDs.listIterator();
				
				
				System.out.println("Master: smallQueue vuota?"+smallWorkQueue.isEmpty()+" Valore owner "+sharedVariableOwner.getID());
				*/
				
				//assegnazione small work (uno solo alla volta)
				int dummyIndex=idleArray.length - 1;
				if(!smallWorkQueue.isEmpty()&&sharedVariableOwner.getID()==-1){
					id=idleArray[dummyIndex];
					dummyIndex--;
					//assegno l'ownwer
					sharedVariableOwner.setID(id);
					//lo setto running
					minionsStatus.setRunning(id);
					//carico il job e lancio
					System.out.println("Master assegna owner a "+id);
					currentWorks[id].loadWork(smallWorkQueue.pop());
				}
				
				// FIXME
				
				/*assegnazione small work (uno solo alla volta)
				if(!smallWorkQueue.isEmpty()&&sharedVariableOwner.getID()==-1){
					System.out.println("Master: small Work assegnato");
					
					id=iter.next().intValue();
					//assegno l'ownwer
					sharedVariableOwner.setID(id);
					//lo setto running
					minionsStatus.setRunning(id);
					//carico il job e lancio
					System.out.println("Master assegna owner a "+id);
					currentWorks[id].loadWork(smallWorkQueue.pop());
				}
				
				*/
				
				for(index=0; index<dummyIndex; index++){
					id=idleArray[index];
					if(!bigWorkQueue.isEmpty()){
						
						minionsStatus.setRunning(id);
						currentWorks[id].loadWork(bigWorkQueue.pop());
						
					}
				}
				
				/*
				
				
				while(iter.hasNext()){
					id=iter.next().intValue();
					if(!bigWorkQueue.isEmpty()){
						
						minionsStatus.setRunning(id);
						currentWorks[id].loadWork(bigWorkQueue.pop());
						
					}
				}*/
				
				
				
				
				
				
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
		
		//System.out.println("Master: wakedUp = "+wakesUp+" avg WakedUp = "+(sum/wakesUp));
		
		/*if(bigWorkQueue.isEmpty()){
			System.out.println("OK coda vuota");
		}*/
		
		//stampo la sharedVariable
		ListIterator<Double> iter = sharedVariable.listIterator();
		while(iter.hasNext()){
			System.out.print("Shared:");
			System.out.println(iter.next().doubleValue());
			
		}
		
		
		System.out.println("MASTER FINISHED");
	}
	
	
	
	
}

