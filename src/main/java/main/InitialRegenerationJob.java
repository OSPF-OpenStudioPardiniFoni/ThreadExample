package main;

import java.util.Set;

import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.policy.EnumerationPolicy;
import it.unifi.oris.sirio.models.stpn.*;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class InitialRegenerationJob extends Job{

	private DeterministicEnablingState current; 
	
	private RegenerativeComponentsFactory f;
	private RegenerativeComponentsFactoryAndPetriNetMaker fMaker;
	private PetriNet petriNet;
	
	protected InitialRegenerationJob(
			DeterministicEnablingState current,
			RegenerativeComponentsFactoryAndPetriNetMaker fMaker,
			PetriNet petriNet){
		
		this.type = 0; // Job di tipo 0 
		this.current=current;
		
		this.fMaker = fMaker;
		this.f = fMaker.getCopy();
		this.petriNet = petriNet;// FIXME copy
	}
	
	protected Job executeJob(){
		return null;
	};
	
}
