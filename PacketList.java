package ee402;

import java.text.NumberFormat;
import java.util.ArrayList;

//This class will store an array of Packets, we will have 5 of them (for 5 Clients)
//because we need to not lose any information, to make the Lowest CPU Usage, Maximum CPU Usage, and Average CPU Usage
public class PacketList {

	private ArrayList<Packet> list = null;
	
	public PacketList(){
		this.list = new ArrayList<Packet>();
	}
	
	public void add(Packet packet){this.list.add(packet);} //Add a packet at the end of the array
	public Packet get(int x){return this.list.get(x);}
	public int getSize(){return this.list.size();}
	public void clear(){this.list.clear();}
	public boolean isEmpty(){return this.list.isEmpty();} //Check if the array is empty
	
	public float getAverage(boolean isReadingCPU){
		float f=0, average=0;
		for (int i=0; i<this.list.size(); i++){
			if(isReadingCPU == true){
				f += this.list.get(i).getCpuUsage();
			}else f+= this.list.get(i).getTemperature();	
		}
		average = f/(this.list.size());
		
		//We only want 2 decimals
		NumberFormat m = NumberFormat.getInstance();
		m.setMaximumFractionDigits(2);
		m.setMinimumFractionDigits(2);
		
		String t = null;
		t = m.format(average);
		average = Float.valueOf(t);
		
		return average;
	}
	//Get the minimum value of the array
	public int getMin(boolean isReadingCPU){
		int min= Integer.MAX_VALUE;
		for (int i=0; i<this.list.size(); i++){
			if(isReadingCPU == true){
				if(min > this.list.get(i).getCpuUsage()) min = this.list.get(i).getCpuUsage();
			}else if(min > this.list.get(i).getTemperature()) min = this.list.get(i).getTemperature();
		}
		return min;
	}
	//Get the maximum value of the array
	public int getMax(boolean isReadingCPU){
		int max= Integer.MIN_VALUE;
		for (int i=0; i<this.list.size(); i++){
			if(isReadingCPU == true){
				if(max < this.list.get(i).getCpuUsage()) max = this.list.get(i).getCpuUsage();
			}else if(max < this.list.get(i).getTemperature()) max = this.list.get(i).getTemperature();
		}
		return max;
	}
}
	
