package ee402;

import java.util.ArrayList;

//Through this class we are creating an array for each client

public class ParcelList {

	private ArrayList<Parcel> list = null;
	
	public ParcelList(){
		this.list = new ArrayList<Parcel>();
	}
	
	public void add(Parcel Parcel){this.list.add(Parcel);} //Add a Parcel at the end of the array
	public Parcel get(int x){return this.list.get(x);}
	public int getSize(){return this.list.size();}
	public void clear(){this.list.clear();}
	public boolean isEmpty(){return this.list.isEmpty();} //Check if the array is empty
	
	public float getAverage(){
		float f=0, average=0;
		for (int i=0; i<this.list.size(); i++){
				f += this.list.get(i).getTemperature();
		}
		average = f/(this.list.size());
		
		return average;
	}
	//Maximum value of the array
	public int getMin(){
		int max= Integer.MAX_VALUE;
		for (int i=0; i<this.list.size(); i++){
			if(max > this.list.get(i).getTemperature()) max = this.list.get(i).getTemperature();
		}
		return max;
	}
	
	//Minimum value of the array
	public int getMax(){
		int min= Integer.MIN_VALUE;
		for (int i=0; i<this.list.size(); i++){
			if(min < this.list.get(i).getTemperature()) min = this.list.get(i).getTemperature();
		}
		return min;
	}
}
	
