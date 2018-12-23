package ee402;

import java.io.Serializable;
import java.net.*;

@SuppressWarnings("serial")
public class Packet implements Serializable {

	private int cpuUsage = 0;
	private int temperature = 0;
	private String dateTime = null;
	private String hostName = null;

	public Packet(){
		
//		this.cpuUsage = (new CpuUsageReader()).getCpuUsage();
		this.temperature = (new TemperatureReader()).getTemperature();
		this.dateTime = (new DateTimeService()).getDateAndTime();
		try {
			this.hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.out.println("Host name not found");
			e.printStackTrace();
		}
	
	}


	public int getCpuUsage(){return this.cpuUsage;}
	public int getTemperature(){return this.temperature;}
	public String getTimeDate(){return this.dateTime;}
	public String getHostName(){return this.hostName;}	
}