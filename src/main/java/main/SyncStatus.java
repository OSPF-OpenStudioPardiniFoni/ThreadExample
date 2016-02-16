package main;

import java.util.LinkedList;

class SyncStatus{

	private Boolean[] statusArray;

	public SyncStatus(int dim){
		statusArray=new Boolean[dim];
		for(int i=0; i<statusArray.length; i++) statusArray[i]=Boolean.FALSE;
	}

	public synchronized void waitForIdles(){
		try{
			wait(2000);
		}catch(InterruptedException e){}
	}

	public synchronized void setIdle(int id){
		statusArray[id]=Boolean.FALSE;
		notify();
	}

	public synchronized void setRunning(int id){
		statusArray[id]=Boolean.TRUE;
	}

	public synchronized boolean areAllIdle(){
		boolean ret = true;
		for(int i=0; i<statusArray.length; i++){ ret = ret && (!statusArray[i].booleanValue()); }
		return ret;
	}

	public synchronized LinkedList<Integer> getIdleList(){
		LinkedList<Integer> ret = new LinkedList<Integer>();
		boolean empty = true;
		for(int i=0; i<statusArray.length; i++){
			if(statusArray[i]==Boolean.FALSE){
				ret.addLast(Integer.valueOf(i));
				empty=false;
			}
				
		}
		if(empty){
			return null;
		}else{
			return ret;
		}
	}

}


