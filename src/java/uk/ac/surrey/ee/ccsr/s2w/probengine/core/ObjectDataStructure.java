package uk.ac.surrey.ee.ccsr.s2w.probengine.core;

import java.io.Serializable;
import java.util.ArrayList;

public class ObjectDataStructure implements Serializable {
	
	String objectID = null;
	ArrayList<String> allConcepts = null;
	
	public ObjectDataStructure(String theObjectID){
		objectID = theObjectID;
	}
	
	public void setAllConcepts(ArrayList<String> allTheConcepts){
		allConcepts = allTheConcepts;
	}
	
	public String getObjectID(){
		return objectID;
	}

	public ArrayList<String> getAllConcepts(){
		return allConcepts;
	}
}
