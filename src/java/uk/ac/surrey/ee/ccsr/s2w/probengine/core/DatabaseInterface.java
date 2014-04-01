package uk.ac.surrey.ee.ccsr.s2w.probengine.core;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.DatabaseClosedException;
import com.db4o.query.Predicate;
import java.io.File;
import java.util.List;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;

public class DatabaseInterface {

	//String DB4OFILENAME = "C:/DiscoveryEngine/index.db4o";
	String DB4OFILENAME = "";
	ObjectContainer db;

	public DatabaseInterface(ConfigParameters cp){
		
		this.DB4OFILENAME= cp.getIndex();

	}


	public void storeEntry(ObjectIndex serviceIndex){
		// accessDb4o
		db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME); 
		try {
			// do something with db4o
			db.store(serviceIndex);
		} finally {
			db.close();
		}
	}
	
	public void deleteDB(){
		new File(DB4OFILENAME).delete();
	}
	
	public List<ObjectIndex> getID(final double[] theLatentFactors){
		
		List<ObjectIndex> resultsList = null;
		
//		db = Db4oEmbedded.openFile(Db4oEmbedded
//				.newConfiguration(), DB4OFILENAME);
		try {
			// do something with db4o
			resultsList = db.query(new Predicate<ObjectIndex>() {
				public boolean match(ObjectIndex req) {

					double[] thisLatentFactor = req.getLatentFactors(); 
					if(thisLatentFactor == theLatentFactors){
						return true;
					}
					else{
						return false;
					}
				}
			});
		} catch(DatabaseClosedException e){
			
		}
		return resultsList;
	}

	public ObjectSet<ObjectIndex> getAllEntries(){
		ObjectSet<ObjectIndex> result = null;
		double[] lfactors = null;
		
		db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DB4OFILENAME);
		try {
			// do something with db4o
			result = db.queryByExample(ObjectIndex.class);
		} catch(DatabaseClosedException e){
			
		}
		
		return result;
	}
	
	public double[][] getPzd(){
		ObjectSet<ObjectIndex> serviceIndices = getAllEntries();
		double[][] pzd = new double[serviceIndices.size()][];
		int count = 0;
		for(ObjectIndex si : serviceIndices){
			pzd[count] = si.getLatentFactors();
			count++;
		}
		//db.close();///////
		return pzd;
	}

	public void closeDb(){

//		db = Db4oEmbedded.openFile(Db4oEmbedded
//				.newConfiguration(), DB4OFILENAME);
		db.close();
	}
	public static void main(String[] args) 
	{
		ConfigParameters cp = new ResourceConfigParams();
		DatabaseInterface dbi = new DatabaseInterface(cp); 
	}

}
