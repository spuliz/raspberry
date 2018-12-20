package ee402;

import java.io.*;

public class GetTemperatureService {
 
    private static String TEMP0_PATH = " /sys/class/thermal/thermal_zone0/temp";
    
    public static void main(String[] args) {
        
        if(args.length!=1) {
            System.out.println("Incorrect Usage - use:\n\t BasicLEDExample On \n\t BasicLEDExample Off");
            System.exit(0);
        }
        try{
            if (args[0].equalsIgnoreCase("On") || 
                            args[0].equalsIgnoreCase("Off")){
                BufferedWriter bw = new BufferedWriter ( 
                        new FileWriter (TEMP0_PATH));
                bw.write("none");
                bw.close();
                bw = new BufferedWriter ( 
                        new FileWriter (TEMP0_PATH));
                bw.write(args[0].equalsIgnoreCase("On")? "1":"0");
                bw.close();
            }
            else {
                System.out.println("Invalid command");
            }
        }
        catch(IOException e){
            System.out.println("Failed to access the Beaglebone LEDs");
        }
    }
 
}
