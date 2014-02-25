package fuelspacestation;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class that holds constant parameters for program and starts all threads
 * 
 * @author Mikael Tenhunen
 */
public class Main {
    final static int MINTRAVELTIME = 10;
    final static int MAXTRAVELTIME = 50;  
    final static int MINREFUELTIME = 1;
    final static int MAXREFUELTIME = 10; 
    final static int VEHICLEN = 500;
    final static int VEHICLEQ = 500;
    final static int SUPPLYTANK = 10000;
    final static int STATIONTANK = 20000;
    final static int STATIONDOCKS = 4;
    final static int NITROGEN = 0;
    final static int QUANTUMFLUID = 1;
    final static int NRSPACEVEHICLES = 20;
    final static int NRSUPPLYVEHICLES = 2;
    
    public static void main(String[] args) {
        //Create vehicles and start them with executor
        ExecutorService executor = Executors.newFixedThreadPool(NRSPACEVEHICLES + NRSUPPLYVEHICLES);
        FuelStation station = new FuelStation(STATIONTANK, STATIONTANK, STATIONDOCKS);
        for (int i = 0; i < NRSPACEVEHICLES + NRSUPPLYVEHICLES; i++) {
            if (i < NRSUPPLYVEHICLES) {
                executor.execute(new SupplyVehicle(VEHICLEN, VEHICLEQ, station,
                i + 1000, i % 2, SUPPLYTANK));
            }
            else {
                executor.execute(new SpaceVehicle(VEHICLEN, VEHICLEQ, station, i));
            }
        }
    }
    
    /**
     * 
     * @param min
     * @param max
     * @return a pseudorandom integer between min and max
     */
    public static int random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
