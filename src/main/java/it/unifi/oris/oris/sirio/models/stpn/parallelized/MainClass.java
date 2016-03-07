package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.math.BigDecimal;
import java.util.Map;

import it.unifi.oris.oris.sirio.models.stpn.RegenerativeSteadyStateAnalysis;
import it.unifi.oris.oris.sirio.models.stpn.SteadyStateInitialStateBuilder;
import it.unifi.oris.oris.sirio.models.stpn.SteadyStatePostProcessor;
import it.unifi.oris.oris.sirio.tests.TestCase;
import it.unifi.oris.oris.sirio.tests.TestCase2ParallelTasks;
import it.unifi.oris.oris.sirio.tests.TestCaseRejuvenation;
import it.unifi.oris.oris.sirio.tests.TestCaseSMP;
import it.unifi.oris.oris.sirio.tests.TestFoniPardini;
import it.unifi.oris.oris.sirio.tests.TestPaolieri;
import it.unifi.oris.sirio.analyzer.policy.FIFOPolicy;
import it.unifi.oris.sirio.models.stpn.DeterministicEnablingState;
import it.unifi.oris.sirio.petrinet.Marking;
import it.unifi.oris.sirio.petrinet.MarkingCondition;

public class MainClass {

	public static void main(String[] args) throws InterruptedException{
		// TODO Auto-generated method stub
		System.out.println("Lancio compute");
		TestCase test = new TestFoniPardini();
		//TestCase test = new TestCase2ParallelTasks();
		//TestCase test = new TestCaseRejuvenation();
		//TestCase test = new TestPaolieri();
        SteadyStateInitialStateBuilder sb = new SteadyStateInitialStateBuilder(test.getPN());
       
        long t0=System.currentTimeMillis();
        //*
        RegenerativeSteadyStateAnalysis<DeterministicEnablingState> analysis = RegenerativeSteadyStateAnalysis
                .compute(test.getPN(), new DeterministicEnablingState(test.getInitialMarking(),
                        test.getPN()), sb, new SteadyStatePostProcessor(), new FIFOPolicy(),
                        MarkingCondition.NONE, false, false, null, null, false);
        //*/
        System.out.println("TEMPO TOTALE SEQUENZIALE= "+(System.currentTimeMillis()-t0));
        long t1=System.currentTimeMillis();
       //* 
        RegenerativeSteadyStateAnalysis<DeterministicEnablingState> analysisp = ParallelizedCompute
                .parallelizedCompute(test.getPN(), new DeterministicEnablingState(test.getInitialMarking(),
                        test.getPN()), sb, new SteadyStatePostProcessor(), new FIFOPolicy(),
                        MarkingCondition.NONE, false, false, null, null, false);
        //*/
		System.out.println("TEMPO TOTALE PARALLELO= "+(System.currentTimeMillis()-t1));
		
	}

}
