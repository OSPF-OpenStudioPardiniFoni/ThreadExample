package main;

import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;

public abstract class Job {

	protected int type;
	private DeterministicEnablingState current;
	
	protected DeterministicEnablingState getRegeneration(){
		return current;
	}
	
	protected int getType(){
		return type;
	}
	
	protected abstract Job executeJob();
	
}
