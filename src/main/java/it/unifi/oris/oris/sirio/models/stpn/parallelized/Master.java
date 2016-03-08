package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class Master extends Thread {
	private Thread[] minions;
	private SyncStatus minionsStatus;
	
	//Definizione delle Threshold per evitare un sovraccarico di lavori nelle code a blocchi
	private final int TYPE_1_THRESHOLD = 256;
	private final int TYPE_2_THRESHOLD = 256;
	private final int TYPE_3_THRESHOLD = 256;
	private final int TYPE_4_THRESHOLD = 25;
	private final int TYPE_5_THRESHOLD = 256;
	
	//Array  delle code di consumo di tutti i minion
	private SynchedContainer[] currentWorks;
	
	//Array delle code di Upload di tutti i minion
	private WorkQueue[] minionsType0UploadWorksQueues;
	private WorkQueue[] minionsType1UploadWorksQueues;
	private WorkQueue[] minionsType2UploadWorksQueues;
	private WorkQueue[] minionsType3UploadWorksQueues;
	private WorkQueue[] minionsType4UploadWorksQueues;
	private WorkQueue[] minionsType5UploadWorksQueues;
	
	private WorkQueue masterType0WorkQueue;
	private WorkQueue masterType1WorkQueue;
	private WorkQueue masterType2WorkQueue;
	private WorkQueue masterType3WorkQueue;
	private WorkQueue masterType4WorkQueue;
	private WorkQueue masterType5WorkQueue;
	
	private RegenerativeComponentsFactoryAndPetriNetMaker fMaker;
	
	
	//BUG 2: degli oggetti Integer non puo' essere passato il riferimento in Java 
	private SharedVariable type1SharedVariableOwner;
	private SharedVariable type2SharedVariableOwner;
	private SharedVariable type3SharedVariableOwner;
	private SharedVariable type5SharedVariableOwner;
	
	private LinkedList<Double> sharedVariable;
	
	public Master(Job first, RegenerativeComponentsFactoryAndPetriNetMaker fMaker){
		//Mi salvo fMaker
		this.fMaker=fMaker;
		
		//Numero di core disponibili
		int proc = Runtime.getRuntime().availableProcessors();
		
		//Vettore dei minion e vettore di stato
		minions = new Thread[proc];
		minionsStatus=new SyncStatus(proc);
		
		//Inizializzo il vettore delle code di consumo
		currentWorks=new SynchedContainer[proc];
		
		//Inizializzazione delle code di Upload per i minion
		minionsType0UploadWorksQueues=new WorkQueue[proc];
		minionsType1UploadWorksQueues= new WorkQueue[proc];
		minionsType2UploadWorksQueues=new WorkQueue[proc];
		minionsType3UploadWorksQueues= new WorkQueue[proc];
		minionsType4UploadWorksQueues=new WorkQueue[proc];
		minionsType5UploadWorksQueues= new WorkQueue[proc];
		
		//Inizializzazione delle code di consumo del master
		masterType0WorkQueue = new WorkQueue(first);
		masterType1WorkQueue = new WorkQueue();
		masterType2WorkQueue = new WorkQueue();
		masterType3WorkQueue = new WorkQueue();
		masterType4WorkQueue = new WorkQueue();
		masterType5WorkQueue = new WorkQueue();
		
		//TBD
		// nessuno possiede la shared variable
		type1SharedVariableOwner= new SharedVariable(); // gia' a -1
		type2SharedVariableOwner= new SharedVariable(); // gia' a -1
		type3SharedVariableOwner= new SharedVariable(); // gia' a -1
		type5SharedVariableOwner= new SharedVariable(); // gia' a -1
		
		sharedVariable = new LinkedList<Double>();
		
		for(int i=0;i<proc;i++){
			currentWorks[i]=new SynchedContainer();
			
			minionsType0UploadWorksQueues[i]= new WorkQueue();
			minionsType1UploadWorksQueues[i]= new WorkQueue();
			minionsType2UploadWorksQueues[i]= new WorkQueue();
			minionsType3UploadWorksQueues[i]= new WorkQueue();
			minionsType4UploadWorksQueues[i]= new WorkQueue();
			minionsType5UploadWorksQueues[i]= new WorkQueue();
			
			minions[i]=
					new Minion(
							i,
							minionsStatus,
							currentWorks[i], 
							minionsType0UploadWorksQueues[i],
							minionsType1UploadWorksQueues[i],
							minionsType2UploadWorksQueues[i],
							minionsType3UploadWorksQueues[i],
							minionsType4UploadWorksQueues[i],
							minionsType5UploadWorksQueues[i],
							type1SharedVariableOwner,
							type2SharedVariableOwner,
							type3SharedVariableOwner,
							type5SharedVariableOwner,
							this.fMaker.getFactoryCopy(),
							this.fMaker.getPetriNetCopy()
							);
			
		}
		//System.out.println("...Riuscita! :-)");
		
	}
	
	public void startMinions(){
		
		for(int i=0;i<minions.length;i++){
			minions[i].start();
		}
		
	}
	
	
	@Override
	public void run(){
		//System.out.println("Master: lancio i minion");
		startMinions();
		
		currentWorks[0].loadSingleWork(masterType0WorkQueue.pop());
		
		while(true){
			//System.out.println("Master attendo Idle");
			minionsStatus.waitForIdles();
			//System.out.println("Master risveglio");
			//Il Master è stato risvegliato
			
			//Controllo della CONDIZIONE DI TERMINAZIONE
			// 1) Tutti i minion sono Idle
			// 2) Tutte le code di consumo del master sono vuote
			// 3) Tutte le variabili owner sono libere
			if(minionsStatus.areAllIdle() &&
					masterType0WorkQueue.isEmpty() &&
					masterType1WorkQueue.isEmpty() &&
					masterType2WorkQueue.isEmpty() &&
					masterType3WorkQueue.isEmpty() &&
					masterType4WorkQueue.isEmpty() &&
					masterType5WorkQueue.isEmpty() &&
					type1SharedVariableOwner.getID()==-1 &&
					type2SharedVariableOwner.getID()==-1 &&
					type3SharedVariableOwner.getID()==-1 &&
					type5SharedVariableOwner.getID()==-1){
				
				//System.out.println("Master: Pre-condizione di Terminazione");
				// Allora controllo che tutte le code di upload dei minion siano vuote
				boolean test=true;
				for(int i=0; i<minionsType0UploadWorksQueues.length;i++){
					test = test && 
							minionsType0UploadWorksQueues[i].isEmpty() &&
							minionsType1UploadWorksQueues[i].isEmpty() &&
							minionsType2UploadWorksQueues[i].isEmpty() &&
							minionsType3UploadWorksQueues[i].isEmpty() &&
							minionsType4UploadWorksQueues[i].isEmpty() &&
							minionsType5UploadWorksQueues[i].isEmpty();
				}
				
				if(test){
					//System.out.println("Master condizione di terminazione verificata");
					break;
				}
				//System.out.println("Master condizione di terminazione NON verificata");
			}
			//Fine controllo della CONDIZIONE DI TERMINAZIONE
			//System.out.println("Master vedo se ci sono Idle");
			//Ottengo la lista dei minion Idle
			LinkedList<Integer> idleIDs = minionsStatus.getIdleList();
			
			//Se la lista non è nulla la converto in Array
			if(idleIDs != null){
				//System.out.println("Master trovato Idle");
				
				Integer idleArray[] = new Integer[idleIDs.size()];
				idleArray = idleIDs.toArray(idleArray);
				
				//System.out.println("Master carico e svuoto code di upload");
				//Svuotiamo le code di Upload dei minion nelle code del master
				for(int j=0; j<idleArray.length;j++){
					//carico
					this.masterType0WorkQueue.addAll(minionsType0UploadWorksQueues[idleArray[j]]);
					this.masterType1WorkQueue.addAll(minionsType1UploadWorksQueues[idleArray[j]]);
					this.masterType2WorkQueue.addAll(minionsType2UploadWorksQueues[idleArray[j]]);
					this.masterType3WorkQueue.addAll(minionsType3UploadWorksQueues[idleArray[j]]);
					this.masterType4WorkQueue.addAll(minionsType4UploadWorksQueues[idleArray[j]]);
					this.masterType5WorkQueue.addAll(minionsType5UploadWorksQueues[idleArray[j]]);
					
					
					//System.out.println("Minion "+idleArray[j]+" Coda 0:"+minionsType0UploadWorksQueues[idleArray[j]].size());
					//System.out.println("Coda 1:"+minionsType1UploadWorksQueues[idleArray[j]].size());
					//System.out.println("Coda 2:"+minionsType2UploadWorksQueues[idleArray[j]].size());
					//System.out.println("Coda 3:"+minionsType3UploadWorksQueues[idleArray[j]].size());
					//System.out.println("Coda 4:"+minionsType4UploadWorksQueues[idleArray[j]].size());
					//System.out.println("Coda 5:"+minionsType5UploadWorksQueues[idleArray[j]].size());
					
					//svuoto
					minionsType0UploadWorksQueues[idleArray[j]].clearList();
					minionsType1UploadWorksQueues[idleArray[j]].clearList();
					minionsType2UploadWorksQueues[idleArray[j]].clearList();
					minionsType3UploadWorksQueues[idleArray[j]].clearList();
					minionsType4UploadWorksQueues[idleArray[j]].clearList();
					minionsType5UploadWorksQueues[idleArray[j]].clearList();
				}
				
				int index=0;
				int numFreeMinions=idleArray.length;
				boolean thereIsWork=true;
				// Scheduler
				while((numFreeMinions > 0) && thereIsWork){
					//System.out.println("INDEX= "+index);
					
					// Le code a blocchi sono prioritarie in caso di threshold
					if((numFreeMinions>0 && (!masterType1WorkQueue.isEmpty())) &&
							(masterType1WorkQueue.size()>this.TYPE_1_THRESHOLD || 
									(this.masterType0WorkQueue.isEmpty() && 
									 this.masterType4WorkQueue.isEmpty()))){
						//System.out.println("Master schedulato tipo 1");
						
						// a) Assegnamento dell'owner
						this.type1SharedVariableOwner.setID(idleArray[index]);
						
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadBlockWork(masterType1WorkQueue);
						
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					if((numFreeMinions>0 && (!masterType2WorkQueue.isEmpty())) && 
							(masterType2WorkQueue.size()>this.TYPE_2_THRESHOLD ||
									(this.masterType0WorkQueue.isEmpty() && 
									 this.masterType4WorkQueue.isEmpty()))){
						
						//System.out.println("Master schedulato tipo 2");
						// a) Assegnamento dell'owner
						this.type2SharedVariableOwner.setID(idleArray[index]);
						
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadBlockWork(masterType2WorkQueue);
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					if((numFreeMinions>0 && (!masterType3WorkQueue.isEmpty())) && 
							(masterType3WorkQueue.size()>this.TYPE_3_THRESHOLD || 
									(this.masterType0WorkQueue.isEmpty() && 
									 this.masterType4WorkQueue.isEmpty()))){
						//System.out.println("Master schedulato tipo 3");
						
						// a) Assegnamento dell'owner
						this.type3SharedVariableOwner.setID(idleArray[index]);
						
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadBlockWork(masterType3WorkQueue);
					//	this.currentWorks[idleArray[index]].loadSingleWork(masterType3WorkQueue.pop());
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					if((numFreeMinions>0 && (!masterType5WorkQueue.isEmpty())) && 
							(masterType5WorkQueue.size()>this.TYPE_5_THRESHOLD || 
									(this.masterType0WorkQueue.isEmpty() && 
									 this.masterType4WorkQueue.isEmpty()))){
						//System.out.println("Master schedulato tipo 5");
						
						// a) Assegnamento dell'owner
						this.type5SharedVariableOwner.setID(idleArray[index]);
						
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadBlockWork(masterType5WorkQueue);
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					if((numFreeMinions>0 && (!masterType4WorkQueue.isEmpty()))&& 
							(masterType4WorkQueue.size()>this.TYPE_4_THRESHOLD || 
									this.masterType0WorkQueue.isEmpty())){
						
						//System.out.println("Master schedulato tipo 4");
						// a) Non c'è assegnamento dell'owner
						
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadSingleWork(masterType4WorkQueue.pop());;
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					if(numFreeMinions>0 && (!this.masterType0WorkQueue.isEmpty())){
						// a) Non c'è assegnamento dell'owner
						//System.out.println("Master schedulato tipo 0");
						// b) Lo metto Running
						this.minionsStatus.setRunning(idleArray[index]);
						
						// c) Carico il lavoro (la coda del master si svuota automaticamente)
						this.currentWorks[idleArray[index]].loadSingleWork(masterType0WorkQueue.pop());;
						
						// d) Epilogo
						numFreeMinions--;
						index++;
					}
					
					//Controllo se c'è ancora lavoro da assegnare
					thereIsWork = (!masterType0WorkQueue.isEmpty())||
									(!masterType1WorkQueue.isEmpty())||
									(!masterType2WorkQueue.isEmpty())||
									(!masterType3WorkQueue.isEmpty())||
									(!masterType4WorkQueue.isEmpty())||
									(!masterType5WorkQueue.isEmpty());
					
				}
			}
			
		}
		
		// Abbiamo la condizione di terminazione, diamo l'interrupt a tutti i minions.
		//System.out.println("Master lancio interrupt");		
		for(int i=0;i<minions.length;i++){
			minions[i].interrupt();
		}
	}
	
}

