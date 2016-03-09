package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.util.Set;

import it.unifi.oris.sirio.models.stpn.Regeneration;
import it.unifi.oris.sirio.petrinet.Marking;

public class RegenerativeMarkingsJob extends Job{

	private Set<Marking> sometimesRegenerativeMarkings;
	private Set<Marking> sometimesNotRegenerativeMarkings;
	private boolean whichOne;
	private Marking m;
	
	public RegenerativeMarkingsJob(Set<Marking> sometimesRegenerativeMarkings,
									Set<Marking> sometimesNotRegenerativeMarkings,
									boolean whichOne,
									Marking m){
		this.m=m;
		this.sometimesNotRegenerativeMarkings=sometimesNotRegenerativeMarkings;
		this.sometimesRegenerativeMarkings=sometimesRegenerativeMarkings;
		this.whichOne=whichOne;
		this.type=6;
	}
	
	@Override
	protected Job executeJob() {
		// TODO Auto-generated method stub
		
		if(whichOne){
			sometimesRegenerativeMarkings.add(m);
		}else{
		    sometimesNotRegenerativeMarkings.add(m);
		}
		
		return null;
	}

}
