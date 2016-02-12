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
		while(true){
			Double job;
			//Tenta di estrarre lavoro 1. wait()
			try{
				job = myWork.extractWork();
			}catch(InterruptedException e){
				break;
			}
			status.setRunning(id);
			
			//Fase consumo
			System.out.println("Thread "+id+" consumo "+job.toString());
			if(job.doubleValue()<0.5){
				double d1 = Math.random();
				double d2 = Math.random();
				System.out.println("Thread "+id+" creo "+d1+" e "+d2);
				works.push(Double.valueOf(d1));
				//works.push(Double.valueOf(Math.random()));
				works.push(Double.valueOf(d2));
			}
		
			// 2.notify to master()
			status.setIdle(id);
		}
	}
}
