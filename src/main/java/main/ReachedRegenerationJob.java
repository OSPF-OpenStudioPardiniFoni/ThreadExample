package main;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.stpn.*;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.PetriNet;


public class ReachedRegenerationJob extends Job{
	
	private Set<DeterministicEnablingState> reachedRegenerations;
	private	DeterministicEnablingState regeneration; 
	
	private RegenerativeComponentsFactoryAndPetriNetMaker fMaker;
	private PetriNet petriNet;
	
	//variabile globale
	private Set<Marking> absorbingMarkings;
	private Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses;
	private Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap;
	private Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses;
	
	protected ReachedRegenerationJob(
			Set<DeterministicEnablingState> reachedRegenerations, 
			DeterministicEnablingState regeneration,
			/* variabili per il tipo 0, devono essere copie!*/
			RegenerativeComponentsFactoryAndPetriNetMaker fMaker,
			PetriNet petriNet,
			Set<Marking> absorbingMarkings,
			Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses,
			Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap,
			Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses
			){
		
		this.type = 2; // tipo 2
		
		this.reachedRegenerations = reachedRegenerations;
		this.regeneration = regeneration;
		
		this.fMaker = fMaker; 
		this.petriNet = petriNet;
		
		this.absorbingMarkings = absorbingMarkings;
		this.regenerationClasses = regenerationClasses;
		
		this.sojourMap = sojourMap;
		this.localClasses = localClasses;
		
	}

	// il Thread esegue questo metodo, se ritorna null non fare niente
	// altrimenti aggiunge il ritorno alla coda di upload di tipo 0
	protected InitialRegenerationJob executeJob(){
		if(!reachedRegenerations.contains(regeneration)){
			
			reachedRegenerations.add(regeneration);
			// genera un nuovo lavoro di tipo 0
			return new InitialRegenerationJob(
					regeneration,
					fMaker, 
					absorbingMarkings, 
					this.reachedRegenerations,
					this.regenerationClasses,
					this.sojourMap,
					this.localClasses
					);
		}
		//non genera nulla
		return null;
	}
	
}
