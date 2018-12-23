package ee402;

import java.util.ArrayList;


public class IdService {

	private ArrayList<Integer> i = null;
	
	public IdService(){
		this.i = new ArrayList<Integer>();
		this.i.add(0);
		this.i.add(1);
		this.i.add(2);
		this.i.add(3);
		this.i.add(4);
	}
	
	//Always get the lowest ID available for practical purpose
	public int getAvailable(){
		int id= Integer.MAX_VALUE, rm = 0;
		for (int j=0; j<this.i.size(); j++){
			if(id > this.i.get(j)){
				id = this.i.get(j);
				rm = j;
			}
		}
		this.i.remove(rm);
		return id;
	}
	
	public boolean isEmpty(){
		return this.i.isEmpty();
	}
	
	public void makeAvailable(int x){
		this.i.add(x);
	}
}
