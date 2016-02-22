package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class Minion extends Thread {
	private int id;
	private SyncStatus status;
	private WorkQueue uploadBigWorksQueue;
	private WorkQueue uploadSmallWorksQueue;
	private SynchedContainer myWork;
	private Job job;
	
	
	private SharedVariable sharedVariableOwner;
	private LinkedList<Double> sharedVariable;
	
	//per parallelizzare il lavoro 0
	private RegenerativeComponentsFactory f;
	private PetriNet petriNet;
	//
	
	public Minion(
			int id,
			SyncStatus status, 
			WorkQueue w, 
			WorkQueue small,
			SynchedContainer work, 
			SharedVariable sharedVariableOwner, 
			LinkedList<Double> sharedVariable,
			/* variabili nuove, non toccare*/
			RegenerativeComponentsFactory f,
			PetriNet petriNet
			){
		this.id=id;
		this.status=status;
		this.uploadBigWorksQueue=w;
		this.uploadSmallWorksQueue=small;
		this.myWork=work;
		
		this.sharedVariableOwner=sharedVariableOwner;
		this.sharedVariable=sharedVariable;
	}
	
	private boolean checkParity(double d){
		int r = (int)(d);
		if(r%2==0){
			//pari
			return true;
		}else{
			//dispari
			return false;
		}
		
	}
	
	private void bigWorkGenerator(){
		System.out.println("Thread "+id+" consumo bigWork "+job.toString());
		if(Math.random()<0.8){
			// genero un nuovo bigwork
			int x = (int)(Math.random()*10);
			double y =(double)x;
			x=(int)(Math.random()*10);
			double z =(double)x;
			System.out.println("Thread "+id+" creo smallWork "+y+" e "+z);
			this.uploadSmallWorksQueue.push(new Job(Double.valueOf(y),false));
			this.uploadSmallWorksQueue.push(new Job(Double.valueOf(z),false));
		}
	}
	
	@Override
	public void run(){
		Job job;
		while(true){
			
			// fase 0 estrarre il job
			try{
				job = myWork.extractWork();
			}catch(InterruptedException e){
				break;
			}
			
			//fase 1 individuare il tipo di Job
			switch(1/*job.getType()*/){
				case 0:
					doInitialRegenerationsJob(job);
					break;
				case 1: 
					doAbsorbingMarkingJob(job);
					break;
				case 2:
					doReachedRegenerationJob(job);
					break;
				case 3:
					doRegenerationClassesJob(job);
					break;
				case 4:
					doEvaluateSojourTimeJob(job);
					break;
				case 5:
					doLocalClassesAndSojourMapJob(job);
					break;
			}
			
			
			
		}
	
	}
	
	//tipo 0
	private void doInitialRegenerationsJob(Job job){
		
		DeterministicEnablingState current = job.getRegeneration();
		
		InitialRegenerationJob myJob = (InitialRegenerationJob)job;
		
		RegenerativeComponentsFactory f = new RegenerativeComponentsFactory(
				false,
				null,
				null,
				true,
				myJob.getPostProcessor(),
				myJob.getEnumerationPolicy(),
				OmegaBigDecimal.POSITIVE_INFINITY,
				myJob.getMarkingCondition(),
				null,
				0,
				null);
		
		
		
	}
	
	//tipo 1
	private void doAbsorbingMarkingJob(Job job){
		job.executeJob();
	}
	
	//tipo 2
	private void doReachedRegenerationJob(Job job){
		Job ret = job.executeJob();
		if(ret == null){
			// nulla
		}else{
			// metti nella pila di upload 0 ret
		}
		
	}
	
	//tipo 3
	private void doRegenerationClassesJob(Job job){
		job.executeJob();
	}
	
	//tipo 4
	private void doEvaluateSojourTimeJob(Job job){
		Job ret = job.executeJob();
		// aggiungi ret alla upload 5
	}
	
	//tipo 5
	private void doLocalClassesAndSojourMapJob(Job job){
		job.executeJob();
	}
	
	//-----------------------------------------------------
	/*@Override
	public void run(){
		//Job job;
		
		//TimeLog
		long t1;
		long t2;
		double sum=0;
		double jobsConsumed=0;
		
		while(true){
			
			//Tenta di estrarre lavoro 1. wait()
			try{
				job = myWork.extractWork();
			}catch(InterruptedException e){
				break;
			}
			//status.setRunning(id);
			
			//timeLog (1)
			t1 = System.currentTimeMillis();
			jobsConsumed=jobsConsumed+1;
			
			
			//Fase consumo
			//System.out.println("Thread "+id+" consumo "+job.toString());
			
			
			
			
			if(job.getType()){
			
				// nuovo consumo - BIGWORK (true)
				bigWorkGenerator();
				
				
					
				
				//
			}else{
				
				// small WORK (false)
				if(sharedVariable.contains(job.getInnerData())){
					//non fare nulla
				}else{
					sharedVariable.add(job.getInnerData());
					this.uploadBigWorksQueue.push(new Job(job.getInnerData(),true));
				}
				// libero l'owner
				System.out.println("Minion "+id+" libera Owner "+job.getInnerData().doubleValue());
				sharedVariableOwner.setID(-1);
				System.out.println(sharedVariableOwner.getID());
			}
		
			//timeLog (2)
			t2 = System.currentTimeMillis();
			sum+=(t2-t1);
			
			// 2.notify to master()
			status.setIdle(id);
		}
		
		// media tempi:
		if(jobsConsumed!=0){
			double avg = sum/jobsConsumed;
			System.out.println("Thread "+id+" jobs consumed = "+jobsConsumed+" avg = "+avg) ;
		}
		
		if(uploadBigWorksQueue.isEmpty()){
			//System.out.println("Minion "+id+" termina con coda vuota");
		}else{
			System.out.println("Minion "+id+" MEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEe");
		}
	}*/
}
