package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;

public abstract class Job {

	protected int type;
	private DeterministicEnablingState current;
	
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
	
}
