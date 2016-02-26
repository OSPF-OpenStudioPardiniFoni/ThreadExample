package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import it.unifi.oris.oris.sirio.models.stpn.EmbeddedDTMC;
import it.unifi.oris.oris.sirio.models.stpn.RegenerativeSteadyStateAnalysis;
import it.unifi.oris.oris.sirio.models.stpn.SteadyStateInitialStateBuilder;
import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.log.AnalysisLogger;
import it.unifi.oris.sirio.analyzer.log.AnalysisMonitor;
import it.unifi.oris.sirio.analyzer.policy.EnumerationPolicy;
import it.unifi.oris.sirio.analyzer.state.State;
import it.unifi.oris.sirio.analyzer.state.StateBuilder;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class ParallelizedCompute {

	private static <DeterministicEnablingState> Map<Marking, BigDecimal> calculateSteadyState(
            RegenerativeSteadyStateAnalysis<DeterministicEnablingState> a) {

        a.seteDTMC(EmbeddedDTMC.compute(a.getRegenerations(), a.getRegenerationClasses()));
        Map<DeterministicEnablingState, BigDecimal> eDTMCSteadyState = a.geteDTMC().getSteadyState();

        Map<Marking, BigDecimal> steadyState = new HashMap<Marking, BigDecimal>();
        BigDecimal normalizationFactor = BigDecimal.ZERO;
        for (Marking mrk : a.getReachableMarkings()) {
            BigDecimal aij = BigDecimal.ZERO;
            for (DeterministicEnablingState reg : a.getRegenerations()) {
                if (a.getSojournMap().get(reg).containsKey(mrk)) {
                    BigDecimal prob = eDTMCSteadyState.get(reg).multiply(
                            a.getSojournMap().get(reg).get(mrk));
                    aij = aij.add(prob);
                    normalizationFactor = normalizationFactor.add(prob);
                }
            }
            steadyState.put(mrk, aij);
        }
        for (Marking mrk : a.getReachableMarkings()) {
            steadyState.replace(mrk,
                    steadyState.get(mrk).divide(normalizationFactor, MathContext.DECIMAL128));
        }

        return steadyState;
    }
	
	public static RegenerativeSteadyStateAnalysis<DeterministicEnablingState> parallelizedCompute 
	(PetriNet petriNet,DeterministicEnablingState initialRegeneration, StateBuilder<DeterministicEnablingState> stateBuilder, 
			SuccessionProcessor postProcessor, 
			EnumerationPolicy enumerationPolicy, MarkingCondition absorbingCondition,
            boolean truncationLeavesInGlobalKernel, boolean markTruncationLeavesAsRegenerative,
            AnalysisLogger l, AnalysisMonitor monitor, boolean verbose)throws InterruptedException{
		
		RegenerativeSteadyStateAnalysis<DeterministicEnablingState> a = new RegenerativeSteadyStateAnalysis<DeterministicEnablingState>();
		a.setPetriNet(petriNet);
        a.setInitialRegeneration(initialRegeneration);
        a.setTruncationPolicy(enumerationPolicy);
        a.setAbsorbingCondition(absorbingCondition);

        a.setLocalClasses(new HashMap<DeterministicEnablingState, Map<Marking, Set<State>>>());
        a.setRegenerationClasses(new HashMap<DeterministicEnablingState, Map<DeterministicEnablingState, Set<State>>>());
        a.setAbsorbingMarkings(new LinkedHashSet<Marking>());

        a.setSojournMap(new HashMap<DeterministicEnablingState, Map<Marking, BigDecimal>>());
		
        Set<Marking> sometimesRegenerativeMarkings = new LinkedHashSet<Marking>();
        Set<Marking> sometimesNotRegenerativeMarkings = new LinkedHashSet<Marking>();

        // Adds the initialRegeneration to the list of regenerations to start
        // the analysis from
        Set<DeterministicEnablingState> reachedRegenerations = new LinkedHashSet<DeterministicEnablingState>();
        reachedRegenerations.add(initialRegeneration);

        Queue<DeterministicEnablingState> initialRegenerations = new LinkedList<DeterministicEnablingState>();
        initialRegenerations.add(initialRegeneration);
        
       //---------------------------------------------------------------------------------------------------------------------------------//
        
        //CREAZIONE DELL'fMaker
        RegenerativeComponentsFactoryAndPetriNetMaker fMaker = 
        		new RegenerativeComponentsFactoryAndPetriNetMaker(
    			postProcessor,
    			enumerationPolicy,
    			absorbingCondition,
    			monitor,
    			petriNet);
        
        //CREAZIONE DEL PRIMO LAVORO 
        //(CONTIENE I RIFERIMENTI A TUTTE LE VARIABILI GLOBALI DA POPOLARE)
        System.out.println("Initial Regeneration "+initialRegeneration.toString());
        
        Job first = new InitialRegenerationJob(
    			initialRegeneration,
    			fMaker,
    			a.getAbsorbingMarkings(),
    			reachedRegenerations,
    			a.getRegenerationClasses(),
    			a.getSojournMap(),
    			a.getLocalClasses());
        
        first.setStateBuilder(new SteadyStateInitialStateBuilder(petriNet));
        
        System.out.println("Creazione Master...");
        
        Master m = new Master(first, fMaker);
        
        m.start();
		m.join();
        
		
		//-------------------------------------------------------------------------------------------//
		a.setAlwaysRegenerativeMarkings(new LinkedHashSet<Marking>(sometimesRegenerativeMarkings));
        a.getAlwaysRegenerativeMarkings().removeAll(sometimesNotRegenerativeMarkings);

        a.setNeverRegenerativeMarkings(new LinkedHashSet<Marking>(sometimesNotRegenerativeMarkings));
        a.getNeverRegenerativeMarkings().removeAll(sometimesRegenerativeMarkings);

        a.setRegenerativeAndNotRegenerativeMarkings(new LinkedHashSet<Marking>(
                sometimesRegenerativeMarkings));
        a.getRegenerativeAndNotRegenerativeMarkings().retainAll(sometimesNotRegenerativeMarkings);

        a.setReachableMarkings(new LinkedHashSet<Marking>(sometimesRegenerativeMarkings));
        a.getReachableMarkings().addAll(sometimesNotRegenerativeMarkings);

        a.setRegenerations(reachedRegenerations);
        
        a.setSteadyState(calculateSteadyState(a));

        
        System.out.println("REGENERATION CLASSES PARALLELIZZATO= "+a.getRegenerationClasses().size());
		return a;
	}
}
