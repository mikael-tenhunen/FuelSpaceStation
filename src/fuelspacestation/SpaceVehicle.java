package fuelspacestation;

/**
 * A vehicle that consumes fuel from a FuelStation
 * 
 * @author Mikael Tenhunen
 */
public class SpaceVehicle implements Runnable, Vehicle { 
    protected int N;  //nitrogen tank capacity
    protected int Q;  //quantum fluid tank capacity
    protected int nitrogen;
    protected int quantumFluid;
    protected final FuelStation pitStop;
    protected int vehicleId;
    
    public SpaceVehicle(int N, int Q, FuelStation pitStop, int vehicleId) {
        this.N = N;
        this.Q = Q;
        this.pitStop = pitStop;
        this.vehicleId = vehicleId;
    }
    
    /**
     * Randomizes fuel supplies of this vehicle
     */
    protected void randomizeFuelState() {
        nitrogen = Main.random(1, N);
        quantumFluid = Main.random(1, Q);
    }

    /**
     * Repeatedly randomizes fuel state, travels and tries to dock and refuel
     * at a FuelStation
     */
    @Override
    public void run() {
        while(true) {
            randomizeFuelState();
            try {
                System.out.println(vehicleId + " traveling...");
                Thread.sleep(Main.random(Main.MINTRAVELTIME, Main.MAXTRAVELTIME));
            } catch (InterruptedException ex) {
                System.out.println(vehicleId 
                        + " was interrupted while traveling through space");
            }
            pitStop.dockAndRefuel(this);
        }
    }
    
    /**
     * @return id of this vehicle
     */
    public int getVehicleId() {
        return vehicleId;
    }
    
    /**
     * @return nitrogen capacity
     */
    public int getN() {
        return N;
    }

    /**
     * @return quantum fluid capacity
     */
    public int getQ() {
        return Q;
    }

    /**
     * @return current nitrogen supply
     */
    public int getNitrogen() {
        return nitrogen;
    }

    /**
     * @param amount nitrogen to add to supply
     */
    public void addNitrogen(int amount) {
        nitrogen += amount;
    }

    /**
     * @return current quantum fluid supply
     */
    public int getQuantumFluid() {
        return quantumFluid;
    }

    /**
     * @param amount quantum fluid to add to supply
     */
    public void addQuantumFluid(int amount) {
        quantumFluid += amount;
    }  
    
    /**
     * @return amount of nitrogen needed to fill to capacity
     */
    public int getNRequest() {
        return N - nitrogen;
    }
    
    /**
     * @return amount of quantum fluid needed to fill to capacity
     */
    public int getQRequest() {
        return Q - quantumFluid;
    }
}
