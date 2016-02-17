package main;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Minion extends Thread {
	private int id;
	private SyncStatus status;
	private WorkQueue works;
	private SynchedContainer myWork;
	
	
	public Minion(int id, SyncStatus status, WorkQueue w, SynchedContainer work){
		this.id=id;
		this.status=status;
		this.works=w;
		this.myWork=work;
		
	}
	
	@Override
	public void run(){
		Double job;
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
			System.out.println("Thread "+id+" consumo "+job.toString());
			if(job.doubleValue()<0.5){
				double d1 = Math.random();
				double d2 = Math.random();
				System.out.println("Thread "+id+" creo "+d1+" e "+d2);
				works.push(Double.valueOf(d1));
				works.push(Double.valueOf(d2));
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
		
		if(works.isEmpty()){
			//System.out.println("Minion "+id+" termina con coda vuota");
		}else{
			System.out.println("Minion "+id+" MEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEe");
		}
	}
}
