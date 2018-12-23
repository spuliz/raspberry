package ee402;

import java.io.*;


public class TemperatureReader {
	
	private int temperature = 0;
	
	public TemperatureReader(){
	//Temperature reader
			String tempPath = "/sys/class/thermal/thermal_zone0/temp";
			float temporaryFloat = 0;
			try {
				BufferedReader br = new BufferedReader(new FileReader(tempPath));
				try {
					this.temperature = Integer.parseInt(br.readLine());
					temporaryFloat = (0.001f)*this.temperature;
					this.temperature = (int)temporaryFloat;
					br.close();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					System.out.println("Could not convert to integer.");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Issue while reading the file.");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.out.println("File not found.");
			}
	}
	
	public int getTemperature(){return this.temperature;}
}