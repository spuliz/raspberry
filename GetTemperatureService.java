package ee402;

import java.io.*;

public class GetTemperatureService {
    public static void main(String [] args) {

        String fileName = "/sys/class/thermal/thermal_zone0/temp";
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                float tempC = (Integer.parseInt(line) / 1000);
                float tempF = ((tempC / 5) * 9) + 32;
                System.out.println("Temp °C: " + tempC + " Temp °F: " + tempF);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
    }
}
