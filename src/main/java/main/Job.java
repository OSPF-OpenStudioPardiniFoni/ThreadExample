package main;

public class Job {

	private Double innerData;
	private boolean type;
	// true = BIG
	// false = SMALL
	
	protected boolean getType(){
		return type;
	}
	
	protected Double getInnerData(){
		return innerData;
	}
	
	public Job(Double val, boolean type){
		this.innerData=val;
		this.type=type;
	}
	
	@Override
	public String toString(){
		return new String(this.getInnerData().toString());
	}
	
}
