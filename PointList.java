package ee402;

import java.util.ArrayList;

//Array of points needed to create a visualization of CPU temperature values 
public class PointList {

	private ArrayList<Integer> list = null;
	
	public PointList(){
		this.list = new ArrayList<Integer>();
	}
	
	public boolean isEmpty(){return this.list.isEmpty();}
	public void clear(){this.list.clear();}
	public void add(int point){this.list.add(point);}
	public int get(int x){return this.list.get(x);}
	public int getSize(){return this.list.size();}

}
