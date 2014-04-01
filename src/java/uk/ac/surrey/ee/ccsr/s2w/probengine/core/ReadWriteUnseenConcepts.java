package uk.ac.surrey.ee.ccsr.s2w.probengine.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;


public class ReadWriteUnseenConcepts {
	
	ArrayList<String> unSeenConcepts;
	ConfigParameters cp;
	
	public ReadWriteUnseenConcepts(ConfigParameters cParams) {
		// TODO Auto-generated constructor stub
		
		this.cp=cParams;
		
		try {
			//FileInputStream f_inA = new FileInputStream ("Unseen.data");
			FileInputStream f_inA = new FileInputStream (cp.getUnseenConcepts());
			ObjectInputStream obj_inA = new ObjectInputStream (f_inA);
			
			
			// Read an object.
			Object objA = obj_inA.readObject ();
			
			unSeenConcepts = (ArrayList<String>) objA;
			
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
		
		if (unSeenConcepts == null)
			unSeenConcepts= new ArrayList<String>();
	}
	
	public ArrayList<String> getUnseenConceptList(){
		return unSeenConcepts;
	}

	public void overwriteUnseenConceptList(ArrayList<String> newList){
		try {
			
		    //FileOutputStream f_outA = new FileOutputStream ("Unseen.data");
			FileOutputStream f_outA = new FileOutputStream (cp.getUnseenConcepts());
		    
		    // Use an ObjectOutputStream to send object data to the
		    // FileOutputStream for writing to disk.
		    ObjectOutputStream obj_outA = new ObjectOutputStream (f_outA);

		    obj_outA.writeObject (newList);
	
		    obj_outA.close();
		    f_outA.close();
	
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
