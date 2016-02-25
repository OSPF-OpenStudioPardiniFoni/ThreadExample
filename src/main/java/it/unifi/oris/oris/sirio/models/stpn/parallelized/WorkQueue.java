package it.unifi.oris.oris.sirio.models.stpn.parallelized;

import java.util.LinkedList;

public class WorkQueue {
	private LinkedList<Job> bigWork;
	
	public WorkQueue(){
		bigWork=new LinkedList<Job>();
	}
	
	public void clearList(){
		bigWork.clear();
	}
	
	public boolean isEmpty(){
		return bigWork.isEmpty();
	}
	
	public WorkQueue(Job first){
		bigWork=new LinkedList<Job>();
		bigWork.addLast(first);
	}
	
	public void push(Job d){
		bigWork.addLast(d);
	}
	
	public LinkedList<Job> getInnerList(){
		return bigWork;
	}
	
	public Job pop(){
		if(bigWork.isEmpty()){
				return null;
		}
		return bigWork.removeFirst();
	}
	
	public void addAll(WorkQueue minionsQueues){
		bigWork.addAll(minionsQueues.getInnerList());
	}
	
	public int size(){
		return bigWork.size();
	}
}
