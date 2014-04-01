package uk.ac.surrey.ee.ccsr.s2w.probengine.core;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;


public class NonLogicMatchmaking {
	
	static final String owlFileName = "owl-samples/resources_200.owl";
	static final String queryFileName = "sparql-templates/sparql-query-051212.txt";
	
	static String queryString = null;
	
	LDAFoldIn ldafin;
	ModelReader modReader;
	//LDATraining ldac;
	//RegistryMapLoader registryLoader;
	//HashMap<String,ServiceInterface> registryMap;
	//Set<String> serviceIDs;
	
	double[][] Pdz;
	ArrayList<String> namesArray;
	int numTopics;
	int numServices;

	DatabaseInterface di;

	public NonLogicMatchmaking(ConfigParameters cp) {
		
		ldafin = new LDAFoldIn(cp);
		di = new DatabaseInterface(cp);
		//registryLoader = new RegistryMapLoader();
		//registryMap = registryLoader.getRegistryMap();
		
		//serviceIDs = registryMap.keySet();
		//namesArray = new ArrayList<String>();
		//namesArray.addAll(serviceIDs);
		//System.out.println(namesArray);
		
		modReader = new ModelReader(cp);
		

		Pdz = di.getPzd();
		numTopics = modReader.getNumTopics();
		numServices = Pdz.length;
	}
	
	public ArrayList<String> matchToQuery(ObjectDataStructure queryInterface) {
		
		HashMap<String,Double> rawServiceList = new HashMap<String,Double>();
		ldafin.setpzdNew(queryInterface);
		double[] queryPdz = ldafin.getPdNewz();
		return fullRepositoryMatch(queryPdz);
		
		//return rawServiceList;
	}
	
	public double computeAngle(double[] p, double[] q) {
		
		double angle = 0;
		double numerator = 0;
		double p2 = 0,q2 = 0;
		
		for(int n=0; n < p.length; n++)
		{
			numerator += p[n]*q[n];
			p2 += Math.pow(p[n], 2);
			q2 += Math.pow(q[n], 2);
		}
		
		double denominator = Math.sqrt(p2*q2);
		angle = numerator/denominator;
		
		return angle;
	}
	
	public ArrayList<String> fullRepositoryMatch(double[] queryZ) {
		ArrayList<String> resourceIDs = new ArrayList<String>();
		
		HashMap<String, Double> bestResults = new HashMap<String, Double>();
		Hashtable<Integer, Double> results = new Hashtable<Integer, Double>();
		
		for(int s = 0; s < numServices; s++) {
			
			double [] tmp = new double[numTopics];
			tmp = Pdz[s];
			
			results.put(s, computeAngle(queryZ, tmp));
		}
		
		double themx = 1;
		
		for(int n = 0; n < 10; n++) {
			themx = 0;
			Integer mx = 0;
			Set<Integer> sv = results.keySet();
			for(int aService : sv) {
				if(themx < results.get(aService)) {
					themx = results.get(aService);
					mx = aService;
				}
			}
			
			System.out.println("Rank #" + (n+1) + ": Resource " + mx + " with a score of " + themx);
			List<ObjectIndex> sindexes = di.getID(Pdz[mx]);
			for(ObjectIndex si : sindexes){
				resourceIDs.add(si.getServiceID());
			}
			//bestResults.put(namesArray.get(mx), themx);
			results.remove(mx);
		}
		di.closeDb();
		return resourceIDs;
	}
	
	/*
	public HashMap<String, Double> clusteredRepositoryMatch(double[] queryZ) {
		
		HashMap<String, Double> bestResults = new HashMap<String, Double>();
		Hashtable<Integer, Double> results = new Hashtable<Integer, Double>();
		HashMap<Integer, ArrayList<Integer>> assignmentsByCluster = ldac.getAssignmentsByCluster();
		int[][] generatedSort = ldac.clusterAssignments;
		ArrayList<String> FinalResults = new ArrayList<String>();
		
		ArrayList<Integer> alreadyUsed = new ArrayList<Integer>();
		while(bestResults.size() < 50){
			int max = 0;
			double theMax = 0;
			for (int topic = 0; topic < queryZ.length; topic++) {
				if(theMax < queryZ[topic] && !alreadyUsed.contains(topic))
				{
					theMax = queryZ[topic];
					max = topic;
				}
			}
			alreadyUsed.add(max);

			ArrayList<Integer> candidates = assignmentsByCluster.get(max);

			for(int s : candidates) {

				double [] tmp = new double[numTopics];
				tmp = Pdz[s];

				results.put(s, computeAngle(queryZ, tmp));
			}



			double themx = 1;
			int st = results.size();
			for(int n = 0; n < st; n++) {
				themx = 0;
				Integer mx = 0;
				Set<Integer> sv = results.keySet();
				for(int aService : sv) {
					if(themx < results.get(aService)) {
						themx = results.get(aService);
						mx = aService;
					}
				}

				//System.out.println("Rank #" + (n+1) + ": Service " + mx + "  " + namesArray.get(mx) + " with a score of " + themx + " and belonging to Cluster: " + generatedSort[mx][0] + " " + generatedSort[mx][1] + " " + generatedSort[mx][2]);
				if(!bestResults.containsKey("http://127.0.0.1/services/1.1/" + namesArray.get(mx))){
					bestResults.put("http://127.0.0.1/services/1.1/" + namesArray.get(mx), themx);
					FinalResults.add("http://127.0.0.1/services/1.1/" + namesArray.get(mx));
				}
				results.remove(mx);
			}
		}
		
		
		return bestResults;
	}
	*/
	
	 public static void main(String[] args) 
		{
		 ConfigParameters cp = new ResourceConfigParams();
	    	NonLogicMatchmaking nlm = new NonLogicMatchmaking(cp);
	    	//ResourceListParser rlp = new ResourceListParser();
	    	try {
				queryString = org.apache.commons.io.FileUtils.readFileToString(new File(queryFileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println(queryString);

			Model m = FileManager.get().loadModel(owlFileName);

			Query query = QueryFactory.create(queryString);

			QueryExecution qe = QueryExecutionFactory.create(query, m);
			ResultSet results = qe.execSelect();
			String s = ResultSetFormatter.asXMLString(results);
			//System.out.println(s);
			
			InputStream is = new ByteArrayInputStream(s.getBytes());
		//rlp.loadString(is);
	    	//ResourceDataStructure requestInterface = rlp.getResourceAtN(19);
	    	//System.out.println(requestInterface.resourceID);
	    	//nlm.matchToQuery(requestInterface);
		}
}
