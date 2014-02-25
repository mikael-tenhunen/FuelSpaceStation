package fuelspacestation;

/**
 * Common operations of all vehicles
 * 
 * @author Mikael Tenhunen
 */
public interface Vehicle {
    public int getVehicleId();
    public int getN();
    public int getQ();
    public int getNitrogen();
    public void addNitrogen(int nitrogen);
    public int getQuantumFluid();
    public void addQuantumFluid(int quantumFluid);
    public int getNRequest();
    public int getQRequest();
}
