package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import it.unifi.oris.sirio.analyzer.SuccessionProcessor;
import it.unifi.oris.sirio.analyzer.log.AnalysisMonitor;
import it.unifi.oris.sirio.analyzer.policy.EnumerationPolicy;
import it.unifi.oris.sirio.math.OmegaBigDecimal;
import it.unifi.oris.sirio.models.stpn.factory.RegenerativeComponentsFactory;
import it.unifi.oris.sirio.petrinet.MarkingCondition;
import it.unifi.oris.sirio.petrinet.PetriNet;

public class RegenerativeComponentsFactoryAndPetriNetMaker {

	private SuccessionProcessor postProcessor;
	private EnumerationPolicy enumerationPolicy;
	private MarkingCondition absorbingCondition;
	private AnalysisMonitor monitor;
	
	private PetriNet petriNet;
	
	public RegenerativeComponentsFactoryAndPetriNetMaker(
			SuccessionProcessor postProcessor,
			EnumerationPolicy enumerationPolicy,
			MarkingCondition absorbingCondition,
			AnalysisMonitor monitor,
			PetriNet petriNet
			){
		this.postProcessor = postProcessor;
		this.enumerationPolicy = enumerationPolicy;
		this.absorbingCondition = absorbingCondition;
		this.monitor = monitor;
		
		this.petriNet = petriNet;
		
	}
	
	// restituisce una copia del RegenerativeComponentsFactory
	protected RegenerativeComponentsFactory getFactoryCopy(){
			return new RegenerativeComponentsFactory(
					false,
					null,
					null,
					true,
					this.postProcessor,
					this.enumerationPolicy,
					OmegaBigDecimal.POSITIVE_INFINITY,
					this.absorbingCondition,
					null,
					0,
					this.monitor);

	}
	
	// restituisce una copia della PN
	protected PetriNet getPetriNetCopy(){
		return PetriNet.getCopyOf(this.petriNet);
	}
	
	protected MarkingCondition getAbsorbingCondition(){
		return this.absorbingCondition;
	}
}
