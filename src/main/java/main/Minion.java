package main;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Minion extends Thread {
	private int id;
	private SyncStatus status;
	private WorkQueue uploadBigWorksQueue;
	private WorkQueue uploadSmallWorksQueue;
	private SynchedContainer myWork;
	private Job job;
	
	
	private SharedVariable sharedVariableOwner;
	private LinkedList<Double> sharedVariable;
	
	public Minion(int id, SyncStatus status, WorkQueue w, WorkQueue small,SynchedContainer work, SharedVariable sharedVariableOwner, LinkedList<Double> sharedVariable){
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
			// vecchio consumo
			/*if(job.doubleValue()<0.5){
				double d1 = Math.random();
				double d2 = Math.random();
				System.out.println("Thread "+id+" creo "+d1+" e "+d2);
				uploadBigWorksQueue.push(Double.valueOf(d1));
				uploadBigWorksQueue.push(Double.valueOf(d2));
			}*/
			
			
			
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
	}
}
