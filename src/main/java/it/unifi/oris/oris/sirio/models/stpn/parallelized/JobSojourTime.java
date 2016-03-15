package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import it.unifi.oris.oris.sirio.models.stpn.ReachingProbabilityFeature;
import it.unifi.oris.sirio.analyzer.Succession;
import it.unifi.oris.sirio.analyzer.graph.Node;
import it.unifi.oris.sirio.analyzer.graph.SuccessionGraph;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.math.expression.Variable;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.models.stpn.StochasticStateFeature;
import it.unifi.oris.sirio.petrinet.Marking;

public class JobSojourTime extends Job{
	
	// variabili per il tipo 4
	private SuccessionGraph graph;
	private Node n;
	private StochasticStateFeature stochasticFeature;
	
	//variabili per il tipo 5 (per generare il Job 5)
	private BigDecimal sojourTime;
	private Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap;
	private State s;
	private Marking m;// petriFeature
	private DeterministicEnablingState current;
	private Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses;
	
	protected JobSojourTime(
			SuccessionGraph graph,
			Node n,
			StochasticStateFeature stochasticFeature,
			Map<DeterministicEnablingState, Map<Marking, BigDecimal>> sojourMap,
			State s,
			Marking m,
			DeterministicEnablingState current,
			Map<DeterministicEnablingState, Map<Marking,Set<State>>> localClasses){
		
		type = 4;	//tipo 4
		
		this.graph =graph;
		this.n = n;
		this.stochasticFeature = stochasticFeature;
		//this.sojourTime = sojourTime perche' nasce a runtime
		this.localClasses = localClasses;
		this.current = current;
		this.s = s;
		this.m = m;
		this.sojourMap = sojourMap;
		
	}
	
	protected JobLocalClassesAndSojourMap executeJob(){
		
		Set<Succession> successions = graph.getOutgoingSuccessions(n);
		
		sojourTime = BigDecimal.ZERO;
		for(Succession succession : successions){
			
			StochasticStateFeature tmpStochasticFeature = 
					new StochasticStateFeature(stochasticFeature);
			
			Variable variable = new Variable(succession.getEvent().getName());
			
			tmpStochasticFeature.conditionToMinimum(variable);
			
			BigDecimal mean = tmpStochasticFeature.computeMeanValue(variable);
			BigDecimal mass = 
					succession.getChild().getFeature(ReachingProbabilityFeature.class).getValue();
			
			sojourTime = sojourTime.add(mean).multiply(mass);
			
		}
		
		// a questo punto creare un Job 5
		JobLocalClassesAndSojourMap ret = 
			new JobLocalClassesAndSojourMap(localClasses, current, m, s, sojourMap, sojourTime);
		
		//System.out.println("Execute Tipo 4 genera sojourTime "+sojourTime.toString());
		return ret;
	}

}
