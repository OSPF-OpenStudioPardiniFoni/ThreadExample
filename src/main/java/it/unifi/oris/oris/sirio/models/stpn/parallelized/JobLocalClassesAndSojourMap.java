package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import it.unifi.oris.sirio.analyzer.graph.Node;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.petrinet.Marking;

public class JobLocalClassesAndSojourMap extends Job{
	
	//variabili per il tipo 5 (per generare il Job 5)
	private BigDecimal sojourTime;
	private Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap;
	private State s;
	private Marking m;// petriFeature
	private DeterministicEnablingState current;
	private Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses;
	
	protected JobLocalClassesAndSojourMap(
			Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses,
			DeterministicEnablingState current,
			Marking m,
			State s,
			Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap,
			BigDecimal sojourTime){
		
		type = 5;	//tipo 5
		
		this.localClasses = localClasses;
		this.current = current;
		this.s = s;
		this.m = m;
		this.sojourMap = sojourMap;
		this.sojourTime=sojourTime;
		
	}
	
	
	protected Job executeJob(){
		
		// remake della istruzione a riga 270
		if(!sojourMap.containsKey(current)){
			sojourMap.put(current, new HashMap<Marking,BigDecimal>());
		}
		
		if(!localClasses.containsKey(current)){
			localClasses.put(current, new HashMap<Marking,Set<State>>());
		}
		
		if(!localClasses.get(current).containsKey(m)){
			localClasses.get(current).put(m, new LinkedHashSet<State>());
		}
		
		localClasses.get(current).get(m).add(s);
		
		if(!sojourMap.get(current).containsKey(m)){
			sojourMap.get(current).put(m, BigDecimal.ZERO);
		}
		
		if(current==null)
			System.out.println("Current nullo");
		
		if(sojourMap==null)
			System.out.println("SojourMap nullo");
		
		if(m==null)
			System.out.println("m nullo");
		
		if(sojourTime==null)
			System.out.println("sojourTime nullo");
		
		sojourMap.get(current).replace(m, sojourMap.get(current).get(m).add(sojourTime));
		
		return null;
	}

}
