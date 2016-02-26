package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import it.unifi.oris.oris.sirio.models.stpn.SteadyStateInitialStateBuilder;
import it.unifi.oris.sirio.analyzer.state.StateBuilder;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingStateBuilder;

public abstract class Job {

	protected int type;
	private DeterministicEnablingState current;
	private SteadyStateInitialStateBuilder sb;
	
	protected DeterministicEnablingState getRegeneration(){
		return current;
	}
	
	protected void setRegeneration(DeterministicEnablingState ir){
		current=ir;
	}
	
	protected int getType(){
		return type;
	}
	
	protected abstract Job executeJob();
	
	protected void setStateBuilder(SteadyStateInitialStateBuilder sb){
		this.sb=sb;
	}
	
	protected  SteadyStateInitialStateBuilder getStateBuilder(){
		return sb;
	}
	
}
