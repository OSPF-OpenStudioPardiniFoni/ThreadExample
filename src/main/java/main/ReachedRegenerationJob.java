package main;
import java.util.Set;

import it.unifi.oris.sirio.models.stpn.*;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.PetriNet;


public class ReachedRegenerationJob extends Job{
	
	private Set<DeterministicEnablingState> reachedRegenerations;
	private	DeterministicEnablingState regeneration; 
	
	private RegenerativeComponentsFactoryAndPetriNetMaker fMaker;
	private PetriNet petriNet;
	
	protected ReachedRegenerationJob(
			Set<DeterministicEnablingState> reachedRegenerations, 
			DeterministicEnablingState regeneration,
			/* variabili per il tipo 0, devono essere copie!*/
			RegenerativeComponentsFactoryAndPetriNetMaker fMaker,
			PetriNet petriNet){
		
		this.type = 2; // tipo 2
		
		this.reachedRegenerations = reachedRegenerations;
		this.regeneration = regeneration;
		
		this.fMaker = fMaker; 
		this.petriNet = petriNet;
		
	}

	// il Thread esegue questo metodo, se ritorna null non fare niente
	// altrimenti aggiunge il ritorno alla coda di upload di tipo 0
	protected InitialRegenerationJob executeJob(){
		if(!reachedRegenerations.contains(regeneration)){
			reachedRegenerations.add(regeneration);
			
			//crea fMaker
			
			// genera un nuovo lavoro di tipo 0
			return new InitialRegenerationJob(
					regeneration,
					fMaker,
					petriNet);
		}
		//non genera nulla
		return null;
	}
	
}
