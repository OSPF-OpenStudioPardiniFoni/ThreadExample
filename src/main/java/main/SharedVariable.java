package main;

public class SharedVariable {

	private int id;
	
	public SharedVariable(){
		id=-1;
	}
	
	public void setID(int i){
		id=i;
	}
	
	public int getID(){
		return id;
	}
}
