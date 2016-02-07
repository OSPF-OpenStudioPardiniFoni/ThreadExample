package main;

public class Job<J> {

	private J internalObject;
	
	public Job(J obj){
		internalObject = obj;
	}
	
	public J getInternalObject(){
		return internalObject;
	}
	
	@Override
	public String toString(){
		return internalObject.toString();
	}
	
}
