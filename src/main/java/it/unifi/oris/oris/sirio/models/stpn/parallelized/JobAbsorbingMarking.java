package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.util.Set;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.petrinet.Marking;


public class JobAbsorbingMarking extends Job{

	private Set<Marking> absorbingMarking;
	private Marking m;
	
	protected JobAbsorbingMarking(Set<Marking> set, Marking m){
		this.type = 1; // Job di tipo 1
		
		this.absorbingMarking = set;
		this.m = m;
		
	}

	// il Thread verifica una condizione ed esegue questo metodo (riga 306)
	protected Job executeJob(){
		absorbingMarking.add(m);
		return null;
	}
	
}
