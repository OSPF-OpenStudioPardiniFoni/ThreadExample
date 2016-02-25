package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.policy.EnumerationPolicy;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.stpn.*;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class InitialRegenerationJob extends Job{

	//private DeterministicEnablingState current; 
	
	private RegenerativeComponentsFactory f;
	private RegenerativeComponentsFactoryAndPetriNetMaker fMaker;
	private PetriNet petriNet;
	
	//variabile globale per generare nuovi Job di tipo 1
	private Set<Marking> absorbingMarkings;
	private Set<DeterministicEnablingState> reachedRegenerations;
	private Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses;
	private Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap;
	private Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses;
	
	protected InitialRegenerationJob(
			DeterministicEnablingState current,
			RegenerativeComponentsFactoryAndPetriNetMaker fMaker,
			Set<Marking> absorbingMarkings,
			Set<DeterministicEnablingState> reachedRegenerations,
			Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses,
			Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap,
			Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses
			){
		
		this.type = 0; // Job di tipo 0 
		this.setRegeneration(current);
		
		this.fMaker = fMaker;
		
		this.f = fMaker.getFactoryCopy();
		this.petriNet = fMaker.getPetriNetCopy();
		
		// variabile globale, passare per riferimento
		this.absorbingMarkings = absorbingMarkings;
		this.reachedRegenerations = reachedRegenerations;
		this.regenerationClasses = regenerationClasses;
		
		this.sojourMap = sojourMap;
		this.localClasses = localClasses;
	}
	
	protected Job executeJob(){
		return null;
	};
	
	protected PetriNet getPN(){
		return this.petriNet;
	}
	
	protected RegenerativeComponentsFactory getRegenerativeComponentsFactory(){
		return this.f;
	}
	
	protected MarkingCondition getAbsorbingCondition(){
		return fMaker.getAbsorbingCondition();
	}
	
	protected Set<Marking> getAbsorbingMarkings(){
		return this.absorbingMarkings;
	}
	
	protected RegenerativeComponentsFactoryAndPetriNetMaker getMaker(){
		return this.fMaker;
	}
	
	protected Set<DeterministicEnablingState> getReachedRegenerations(){
		return this.reachedRegenerations;
	}
	
	protected Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> getRegenerationClasses(){
		return this.regenerationClasses;
	}
	
	protected Map<DeterministicEnablingState, Map<Marking, BigDecimal>> getSojourMap(){
		return this.sojourMap;
	}
	
	protected Map<DeterministicEnablingState, Map<Marking,Set<State>>> getLocalClasses(){
		return this.localClasses;
	}
	
}
