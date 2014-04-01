package uk.ac.surrey.ee.ccsr.s2w.probengine.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;



public class ModelReader {
	
	double[][] pwz;
	short NumTopics;
	int numSamples;
	double[] clusterAssignments;
	
	public ModelReader(ConfigParameters cp) {
		// TODO Auto-generated constructor stub
		try {
//			FileInputStream f_inA = new FileInputStream ("LDA_Model.data");
			FileInputStream f_inA = new FileInputStream (cp.getLdaModel());
			ObjectInputStream obj_inA = new ObjectInputStream (f_inA);
			
			Object objA = obj_inA.readObject ();
			pwz = (double[][]) objA;
			
			objA = obj_inA.readObject ();
			numSamples = (int) objA;
    	
			objA = obj_inA.readObject ();
			NumTopics = (short) objA;
			
			objA = obj_inA.readObject ();
			clusterAssignments = (double[]) objA;
			
			obj_inA.close();
			f_inA.close();
			
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double[][] getPwz() {
		return pwz;
	}
	
	public short getNumTopics() {
		return NumTopics;
	}
	
	public int getNumSamples() {
		return numSamples;
	}
	
	public double[] getclusterAssignments(){
		return clusterAssignments;
	}
}
