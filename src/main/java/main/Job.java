package main;

public class Job<S>{

	private S internalObject;
	
	public Job(S obj){
		internalObject = obj;
	}
	
	public S getInternalObject(){
		return internalObject;
	}
	
	@Override
	public String toString(){
		return internalObject.toString();
	}
	
	
}
