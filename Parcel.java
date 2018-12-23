package ee402;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Parcel implements Serializable {

	private int temperature = 0;
	private String dateTime = null;

	public Parcel(){
		this.temperature = (new TemperatureReader()).getTemperature();
		this.dateTime = (new DateTimeService()).getDateAndTime();
	}


	public int getTemperature(){return this.temperature;}
	public String getTimeDate(){return this.dateTime;}
}