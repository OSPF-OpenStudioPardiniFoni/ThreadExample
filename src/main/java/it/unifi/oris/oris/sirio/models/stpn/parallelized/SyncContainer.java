package it.unifi.oris.oris.sirio.models.stpn.parallelized;


public class SyncContainer {
	private WorkQueue innerJobs=new WorkQueue();
	
	public synchronized Job extractWork() throws InterruptedException{
		while(innerJobs.isEmpty()){
			wait();
		}
		return innerJobs.pop();
	}
	
	public synchronized void loadSingleWork(Job j){
		innerJobs.push(j);
		notify();
	}
	 
	public synchronized void loadBlockWork(WorkQueue w){
		innerJobs.addAll(w);
		
		//Questa operazione dovrebbe farla il Master ma si trova qui per via della notify()
		// Dobbiamo essere sicuri che la coda del Master sia vuota prima che parta il minion.
		w.clearList();
		notify();
	}
	
	public synchronized boolean isEmpty(){
		return innerJobs.isEmpty();
	}
}
