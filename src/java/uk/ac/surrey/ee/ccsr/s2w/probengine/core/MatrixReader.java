package uk.ac.surrey.ee.ccsr.s2w.probengine.core;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;


/*
 * Class Matrix Reader
 * 
 * Written by Gilbert Cassar 05/12/2012
 * 
 * This class reads the files STM1.data and STM2.data and stores the Number to Concept Map, ConceptList, ServiceByConceptArray and LDAInput2.
 * It also provides method to access these stored datatypes.
 * 
 */

public class MatrixReader implements Serializable {

	Hashtable<Integer, String> NumberToConceptMap;
	ArrayList<String> ConceptList;
	double [][] ServiceByConceptArray;
	int[][] LDAInput2;
	HashMap<Integer,ObjectDataStructure> objectRegistryMap;

	public MatrixReader(ConfigParameters cp) {
		try {
//			FileInputStream f_inA = new FileInputStream ("STM1.data");
			FileInputStream f_inA = new FileInputStream (cp.getSTM1());
			ObjectInputStream obj_inA = new ObjectInputStream (f_inA);
			
//			FileInputStream f_inB = new FileInputStream ("STM2.data");
			FileInputStream f_inB = new FileInputStream (cp.getSTM2());
			ObjectInputStream obj_inB = new ObjectInputStream (f_inB);

			// Read an object.
			Object objA = obj_inA.readObject ();
			Object objB = obj_inB.readObject ();
			
			NumberToConceptMap = (Hashtable<Integer, String>) objA;
			ServiceByConceptArray = (double[][]) objB;
			
			objA = obj_inA.readObject ();
			objB = obj_inB.readObject ();
			
			ConceptList = (ArrayList<String>)objA;
			LDAInput2 = (int[][])objB;
			
			objA = obj_inA.readObject();
			
			objectRegistryMap = (HashMap<Integer,ObjectDataStructure>)objA;

			obj_inA.close();
			f_inA.close();
			
			obj_inB.close();
			f_inB.close();
			
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
	
	public int[][] getLDAInput2() {
		return LDAInput2;
	}
	
	public int getD() {
		return ServiceByConceptArray.length;
	}
	
	public double[][] getServiceTransactionMatrix() {
		return ServiceByConceptArray;
	}
	
	public int getW() {
		return ConceptList.size();
	}
	
	public ArrayList<String> getConceptList() {
		return ConceptList;
	}
	
	
	public HashMap<Integer,ObjectDataStructure> getObjectRegistryMap(){
		return objectRegistryMap;
	}
	
	
	public static void main(String[] args) 
	{
		ConfigParameters cp = new ResourceConfigParams();
		MatrixReader test = new MatrixReader(cp);
		
		System.out.println("The number of concetps extracted is: " + test.getW());
		
		//System.out.println(test.ConceptList);
		ArrayList<String> docTokens = test.ConceptList;
		
		/*
		int[][] docTokens = test.getLDAInput2();
		
		for(int j = 0; j < test.getD(); j++) {
			System.out.print("Service " + j + ": ");
			for(int i = 0; i < docTokens[j].length ; i++) {
				System.out.print(docTokens[j][i] + " ");
			}
			System.out.println();
		}
		*/
		
		
		//List<CharSequence> docTokens = test.getLDAInput();
		/*
		for(int j = 0; j < test.getW(); j++) {
			System.out.print("Service " + j + ": ");
			System.out.print(docTokens.get(j));
			System.out.println();
		}
		*/
		System.out.println(test.objectRegistryMap);
	}
}
