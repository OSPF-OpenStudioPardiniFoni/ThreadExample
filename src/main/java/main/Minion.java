package main;

import java.math.BigDecimal;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import it.unifi.oris.oris.sirio.models.stpn.SteadyStateInitialStateBuilder;
import it.unifi.oris.sirio.analyzer.Analyzer;
import it.unifi.oris.sirio.analyzer.graph.Node;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.models.pn.PetriStateFeature;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.models.stpn.Regeneration;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;
import it.unifi.oris.sirio.petrinet.Transition;
// sostituisce lo state di Thread
import it.unifi.oris.sirio.analyzer.state.State;

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
	/*
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
	*/
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
		
		//estraggo la rigenerazione corrente
		DeterministicEnablingState current = job.getRegeneration();
		//faccio un cast al Job per avere la visibilita' di alcuni metodi
		InitialRegenerationJob myJob = (InitialRegenerationJob)job;
		
		//ottengo le copie del RegenerativeComponentsFactory e della PN
		RegenerativeComponentsFactory f = myJob.getRegenerativeComponentsFactory();
		PetriNet petriNet = myJob.getPN();
		MarkingCondition absorbingCondition = myJob.getAbsorbingCondition();
		RegenerativeComponentsFactoryAndPetriNetMaker fMaker = myJob.getMaker();
		
		//creo stateBuilder locale
		SteadyStateInitialStateBuilder stateBuilder = new SteadyStateInitialStateBuilder(petriNet);
		
		//creo un analyzer locale
		Analyzer<PetriNet, Transition> analyzer = new Analyzer<PetriNet,Transition>(
				f,
				petriNet,
				stateBuilder.build(current));
		
		//creazione del succession Graph locale
		SuccessionGraph graph = analyzer.analyze(); 
		
		Deque<Node> stack = new LinkedList<Node>();
		stack.push(graph.getRoot());
		
		while(!stack.isEmpty()){ // while VERDE
			
			Node n = stack.pop();
			
			if(n!=null){
				
				//necessario per evitare di usare State della classe Thread
				it.unifi.oris.sirio.analyzer.state.State s = graph.getState(n);
				
				PetriStateFeature petriFeature = s.getFeature(PetriStateFeature.class);
				StochasticStateFeature stochasticFeature = s.getFeature(StochasticStateFeature.class);
				
				if(absorbingCondition.evaluate(petriFeature.getMarking())){
					
					// GENERARE NUOVO LAVORO DI TIPO 1
					Job type1Job = new AbsorbingMarkingJob(myJob.getAbsorbingMarkings(), petriFeature.getMarking());
					// AGGIUNGERE TYPE1JOB ALLA CODA DI UPLOAD DI TIPO 1
				
				}else{
					
					if(s.hasFeature(Regeneration.class)){
						
						DeterministicEnablingState regeneration = 
							(DeterministicEnablingState) 
								s.getFeature(Regeneration.class).getValue();
						
						//CREO LAVORO DI TIPO 2
						Job type2Job = new ReachedRegenerationJob(
								myJob.getReachedRegenerations(), 
								regeneration,
								/* variabili per il tipo 0, devono essere copie!*/
								fMaker,
								fMaker.getPetriNetCopy(),
								myJob.getAbsorbingMarkings(),
								myJob.getRegenerationClasses(),
								myJob.getSojourMap(),
								myJob.getLocalClasses()
								);
						
						//CARICARE type2Job NELLA CODA DI UPLOAD DI TIPO 2
						
					}
					
					//FIXME
					/*if(s.hasFeature(Regeneration.class)){
						someTimesRegenerativeMarkings.add(petriFeature.getMarking());
					}else{
					    someNotTimesRegenerativeMarkings.add(petriFeature.getMarking());
					}*/  
					
					if( s.hasFeature(Regeneration.class) &&
						graph.getSuccessors(n).size()==0 &&
						(!n.equals(graph.getRoot())) &&
						(!absorbingCondition.evaluate(petriFeature.getMarking())) ||
					    (false && graph.getSuccessors(n).size()==0)){
							
							DeterministicEnablingState regenerationStar = 
								(DeterministicEnablingState) 
									s.getFeature(Regeneration.class).getValue();						
							
							//CREO UN LAVORO DI TIPO 3
							Job type3Job = new RegenerationClassesJob(
								myJob.getRegenerationClasses(),
								current,
								regenerationStar,
								s);
							
							//CARICARE JOB NELLA PILA DI UPLOAD DI TIPO 3
							
					}else{
						
						//CREO UN LAVORO DI TIPO 4
						Job type4Job = new SojourTimeJob(
								graph,
								n,
								stochasticFeature,
								myJob.getSojourMap(),
								s,
								petriFeature.getMarking(),
								current,
								myJob.getLocalClasses());
						
						//CARICARE LAVORO 4 NELLA PILA 4
						
					}//fine if enorme
					
					stack.push(null);
					
					for(Node m : graph.getSuccessors(n)){
						stack.push(m);
					}
					
						
				}
				
			}/*else{
				//cavolata su offset
			}*/
			
		}
		
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
