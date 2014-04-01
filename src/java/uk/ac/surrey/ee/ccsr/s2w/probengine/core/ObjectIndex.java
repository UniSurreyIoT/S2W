package uk.ac.surrey.ee.ccsr.s2w.probengine.core;

public class ObjectIndex {
	
	private String serviceID;
    private double[] latentFactors;

	public ObjectIndex(String iD, double[] indexVector){
		serviceID = iD;
		latentFactors = indexVector;
	}
	
	public String getServiceID() {
        return serviceID;
    }
	
	public double[] getLatentFactors(){
		return latentFactors;
	}
}
