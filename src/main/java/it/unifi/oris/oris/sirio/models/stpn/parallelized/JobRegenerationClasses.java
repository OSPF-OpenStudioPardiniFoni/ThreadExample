package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;

public class JobRegenerationClasses extends Job{
	
	private Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses;
	private DeterministicEnablingState current;
	private DeterministicEnablingState regenerationStar;
	private State s;
	
	protected JobRegenerationClasses(
			Map<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>> regenerationClasses,
			DeterministicEnablingState current,
			DeterministicEnablingState regenerationStar,
			State s){
		
			this.type = 3; // tipo 3
		
			this.regenerationClasses = regenerationClasses;
			this.current = current;
			this.s = s;
			this.regenerationStar =regenerationStar; 
	
	}
	
	protected Job executeJob(){
		
		if(!regenerationClasses.containsKey(current)){
			regenerationClasses.put(current, new HashMap<DeterministicEnablingState, Set<State>>());
		}
		
		if(!regenerationClasses.get(current).containsKey(regenerationStar)){
			regenerationClasses.get(current).put(regenerationStar, new LinkedHashSet<State>());
		}
		
		regenerationClasses.get(current).get(regenerationStar).add(s);
		
		return null;
	}
	
}

