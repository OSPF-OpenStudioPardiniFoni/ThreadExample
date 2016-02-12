package main;

import java.util.LinkedList;

public class WorkQueue {
	private LinkedList<Double> bigWork;
	
	public WorkQueue(){
		bigWork=new LinkedList<Double>();
	}
	
	public void clearList(){
		bigWork.clear();
	}
	
	public boolean isEmpty(){
		return bigWork.isEmpty();
	}
	
	public WorkQueue(Double first){
		bigWork=new LinkedList<Double>();
		bigWork.addLast(first);
	}
	
	public void push(Double d){
		bigWork.addLast(d);
	}
	
	public LinkedList<Double> getInnerList(){
		return bigWork;
	}
	
	public Double pop(){
		if(bigWork.isEmpty()){
				return null;
		}
		return bigWork.removeFirst();
	}
	
	public void addAll(WorkQueue minionsQueues){
		bigWork.addAll(minionsQueues.getInnerList());
	}
}
