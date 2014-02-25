package fuelspacestation;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * FuelStation holds counters for fuel and means to synchronize threads operating
 * on it.
 * 
 * @author Mikael Tenhunen
 */
public class FuelStation {
    private int N;  //max amount of nitrogen
    private int Q;  //max amount of quantum fluid
    private int V;  //max amount of vehicles docked to station
    private int nitrogen;
    private int quantumFluid;
    private int vehiclesDocked;
    private final ReentrantLock fuelLock;   //lock on fuel counters
    private final ReentrantLock vLock;      //lock on dock counter
    Condition vCond;        //condition for entering dock
    Condition fuelCond;     //condition for consuming fuel
    Condition deliverCond;  //condition for delivering fuel


    public FuelStation (int N, int Q, int V) {
        this.N = N;
        this.Q = Q;
        this.V = V;
        nitrogen = N;
        quantumFluid = Q;
        vehiclesDocked = 0;
        fuelLock = new ReentrantLock(true); //fair lock
        vLock = new ReentrantLock(true);    //fair lock
        vCond = vLock.newCondition();
        fuelCond = fuelLock.newCondition();
        deliverCond = fuelLock.newCondition();
    }

    /**
     * Refills station's supply, called by SupplyVehicle objects.
     * 
     * Makes the SupplyVehicle wait until there is enough space in station's
     * tanks to receive the fuel. 
     * 
     * @param vehicle the vehicle that is delivering fuel
     */
    public void deliver(SupplyVehicle vehicle) {
        System.out.println(vehicle.getVehicleId() + " trying to deliver fuel");
        fuelLock.lock();
        //wait for the station tanks to have enough space to deliver all fuel
        while (vehicle.getnPayload() > (N - nitrogen) 
                || vehicle.getqPayload() > (Q - quantumFluid)) {
            System.out.println(vehicle.getVehicleId() 
                    + ": waiting for station tanks to have enough space");
            try {
                deliverCond.await();
            } catch (InterruptedException ex) {
                System.out.println(vehicle.getVehicleId() 
                    + ": interrupted while waiting for station tank to have enough space");
            }        
        }
        //dock and deliver payload
        dock(vehicle);
        nitrogen += vehicle.getnPayload();
        quantumFluid += vehicle.getqPayload();
        fuelCond.signal();
        fuelLock.unlock();
        undock(vehicle);
    }
    
    /**
     * The method called by a vehicle that wants to fill its fuel supplies.
     * 
     * Makes the vehicle wait for there to be enough fuel to satisfy request.
     * While waiting for the station to have enough fuel the vehicle doesn't block
     * other vehicles from making requests. 
     * 
     * However, a vehicle whose request can be satisfied, but has to wait for an
     * available dock space, does block other vehicles from making requests.
     * 
     * @param vehicle The Vehicle that tries to refuel
     */
    public void dockAndRefuel(Vehicle vehicle) {
        int nRequest = vehicle.getNRequest();
        int qRequest = vehicle.getQRequest();
        fuelLock.lock();
        System.out.println(vehicle.getVehicleId() + " trying to dock and refuel\n"
            + "     requested n=" + nRequest + ", requested q=" + qRequest
            + "     available n=" + nitrogen + ", available q=" + quantumFluid);
        //wait, without blocking, for there to be enough fuel to satisfy request
        while(nRequest > nitrogen || qRequest > quantumFluid) {
            System.out.println(vehicle.getVehicleId() + " not enough fuel, waiting");         
            try {
                fuelCond.await();
            } catch (InterruptedException ex) {
                System.out.println(vehicle.getVehicleId() + 
                        " interrupted while waiting for fuel condition");
            }
        }
        //calling vehicle while fuelLock is locked
        dock(vehicle);
        //refuel
        System.out.println(vehicle.getVehicleId() + " refueling...");
        nitrogen -= nRequest;
        quantumFluid -= qRequest;
        deliverCond.signalAll();
        fuelLock.unlock();
        try {
            Thread.sleep(Main.random(Main.MINREFUELTIME, Main.MAXREFUELTIME));
        } catch (InterruptedException ex) {
            System.out.println(vehicle.getVehicleId() + 
                    " interrupted while refueling (sleeping)");
        }
        undock(vehicle);
    }
    
    /**
     * Called by vehicle that has its request satisfied to try to dock.
     * 
     * Makes the vehicle wait until there are free docking spaces.
     * 
     * @param vehicle to dock
     */
    private void dock(Vehicle vehicle) {
        vLock.lock();
        System.out.println(vehicle.getVehicleId() + " trying to dock");
        while(!(vehiclesDocked < V)) {
            System.out.println(vehicle.getVehicleId() + " no free docks, waiting");
            try {
                vCond.await();
            } catch (InterruptedException ex) {
                System.out.println(vehicle.getVehicleId() + 
                        " interrupted while waiting for docking space");
            }
        }
        //there is a dock available
        System.out.println(vehicle.getVehicleId() + " docking");
        vehiclesDocked++;
        vLock.unlock();
    }

    /**
     * Called to undock a vehicle. 
     * 
     * Signals a vehicle waiting for condition to enter dock
     * 
     * @param vehicle The Vehicle to undock
     */
    private synchronized void undock(Vehicle vehicle) {
        vLock.lock();
        vehiclesDocked--;
        vCond.signal();
        vLock.unlock();
        System.out.println(vehicle.getVehicleId() + 
                " undocking");
    }
}
