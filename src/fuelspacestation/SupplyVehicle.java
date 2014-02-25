package fuelspacestation;

/**
 * A vehicle that delivers fuel to and consumes fuel from a FuelStation
 * 
 * @author Mikael Tenhunen
 */
public class SupplyVehicle extends SpaceVehicle implements Runnable, Vehicle {
    private int contentType;
    private int tankCapacity;   //supply vehicles always deliver full tank capacity
                                //of one of the fuel kinds
    private int nPayload;
    private int qPayload;

    public SupplyVehicle(int N, int Q, FuelStation pitStop, int vehicleId) {
        super(N, Q, pitStop, vehicleId);
    }
    
    public SupplyVehicle(int N, int Q, FuelStation pitStop, int vehicleId, 
            int contentType, int tankCapacity) {
        super(N, Q, pitStop, vehicleId);
        this.contentType = contentType;
        this.tankCapacity = tankCapacity;
    }    
    
    /**
     * Repeatedly fills cargo tank with the content type of this supply vehicle,
     * randomizes the vehicle's own supplies of fuel,
     * delivers cargo and tries to dock and refuel
     */
    @Override
    public void run() {
        while(true) {
            if (contentType == Main.NITROGEN) {
                nPayload = tankCapacity;
                qPayload = 0;
            }
            else {
                qPayload = tankCapacity;
                nPayload = 0;                
            } 
            randomizeFuelState();
            try {
                System.out.println(vehicleId + " traveling...");
                Thread.sleep(Main.random(Main.MINTRAVELTIME, Main.MAXTRAVELTIME));
            } catch (InterruptedException ex) {
                System.out.println(vehicleId 
                        + " was interrupted while traveling through space");
            }
            pitStop.deliver(this);
            pitStop.dockAndRefuel(this);
//            System.out.println(vehicleId + " trying to deliver");
//            pitStop.deliver(this);
//            System.out.println(vehicleId + " trying to dock and refuel");
//            pitStop.dockAndRefuel(this);
        }
    }

    /**
     * @return type of cargo (Main.NITROGEN or Main.QUANTUMFLUID)
     */
    public int getContentType() {
        return contentType;
    }
    
    /**
     * @return capacity of cargo tank
     */
    public int getTankCapacity() {
        return tankCapacity;
    }

    /**
     * @return amount of nitrogen in cargo tank
     */
    public int getnPayload() {
        return nPayload;
    }

    /**
     * @param nPayload amount of nitrogen in cargo tank
     */
    public void setnPayload(int nPayload) {
        this.nPayload = nPayload;
    }

    /**
     * @return amount of quantum fluid in cargo tank
     */    
    public int getqPayload() {
        return qPayload;
    }

    /**
     * @param qPayload amount of quantum fluid in cargo tank
     */
    public void setqPayload(int qPayload) {
        this.qPayload = qPayload;
    }
}
