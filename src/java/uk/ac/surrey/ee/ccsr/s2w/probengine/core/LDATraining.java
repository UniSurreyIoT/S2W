package uk.ac.surrey.ee.ccsr.s2w.probengine.core;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;



import com.aliasi.cluster.LatentDirichletAllocation;
import com.aliasi.symbol.MapSymbolTable;
import com.aliasi.symbol.SymbolTable;

import com.aliasi.tokenizer.TokenizerFactory;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;


public class LDATraining {
	
	DatabaseInterface databaseInterface;
	
	HashMap<Integer,ObjectDataStructure> objectRegistryMap;
	//HashMap<String, Double[]> vectorToIdMapping;
	
	MatrixReader matrixReader;
	transient LatentDirichletAllocation.GibbsSample sample;
	
	long interval;
	long beginning;
	long ending;
	
    int minTokenCount = 5;
    short numTopics;
    int numWords;
    double topicPrior = 0.1;
    double wordPrior = 0.01;
    int burninEpochs = 0;
    int sampleLag = 1;
    int numSamples;
    long randomSeed = 6474835;
    
    SymbolTable symbolTable = new MapSymbolTable();
    ArrayList<String> ConceptList;
    CharSequence[] articleTexts;
    
    int[][] docTokens;
    double[][] pwz;
    double[][] pdz;
    double[] clusterAssignments;
    
    TokenizerFactory BASE_TOKENIZER_FACTORY;
    TokenizerFactory WORMBASE_TOKENIZER_FACTORY;
    
    public LDATraining(int topics, ConfigParameters cp) {
    	
    	databaseInterface = new DatabaseInterface(cp);
    	
    	beginning = System.currentTimeMillis();
    	
    	matrixReader = new MatrixReader(cp);
    	numTopics = (short)topics;
    	numSamples = matrixReader.getD();
    	
    	ConceptList = matrixReader.getConceptList();
    	objectRegistryMap = matrixReader.getObjectRegistryMap();
    	
    	for(int b = 0; b < ConceptList.size(); b++) {
    		symbolTable.getOrAddSymbol(ConceptList.get(b));
    	}
		
		//BASE_TOKENIZER_FACTORY = new RegExTokenizerFactory("[\\x2Da-zA-Z0-9]+"); // letter or digit or hyphen (\x2D)
		//BASE_TOKENIZER_FACTORY = new RegExTokenizerFactory("[0-9]"); // letter or digit or hyphen (\x2D)
		
		
		//WORMBASE_TOKENIZER_FACTORY = wormbaseTokenizerFactory();
    	
		//docTokens = LatentDirichletAllocation.tokenizeDocuments(articleTexts,BASE_TOKENIZER_FACTORY,symbolTable,minTokenCount);
		docTokens = matrixReader.getLDAInput2();
    	
		LdaReportingHandler handler = new LdaReportingHandler(symbolTable);

		sample = LatentDirichletAllocation
        			.gibbsSampler(docTokens,
                      	numTopics,
                      	topicPrior,
                      	wordPrior,

                      	burninEpochs,
                      	sampleLag,
                      	numSamples,

                      	new Random(randomSeed),
                      	handler);
		
		ending = System.currentTimeMillis();
		interval = ending - beginning;
		//System.out.println("Algorithm Duration = " + interval + "ms");
		numWords = sample.numWords();
		
		pwz = setPwz();
		pdz = setPdz();
		clusterAssignments = getClusterAssignments();
		//handler.fullReport(sample, 50, 10, true);
		//System.out.println("Total number of concepts: " + numWords);
		/*
		for(int j = 0; j < numSamples; j++) {
			System.out.print("Service " + j + ": ");
			for(int i = 0; i < docTokens[j].length ; i++) {
				System.out.print(docTokens[j][i] + " ");
			}
			System.out.println();
		}
		
		for(int j = 0; j < symbolTable.numSymbols(); j++) {
			System.out.println("Symbol #" + j + ": " + symbolTable.idToSymbol(j));
		}
		*/
		
		try {
			
		    //FileOutputStream f_outA = new FileOutputStream ("LDA_Model.data");
			FileOutputStream f_outA = new FileOutputStream (cp.getLdaModel());
		    
		    // Use an ObjectOutputStream to send object data to the
		    // FileOutputStream for writing to disk.
		    ObjectOutputStream obj_outA = new ObjectOutputStream (f_outA);

		    obj_outA.writeObject (pwz);
		    obj_outA.writeObject (numSamples);
		    obj_outA.writeObject (numTopics);
		    obj_outA.writeObject (clusterAssignments);
	
		    obj_outA.close();
		    f_outA.close();
	
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		databaseInterface.closeDb();
    }
    
    public CharSequence[] readCorpus(List<CharSequence> serviceDescriptions) throws IOException {
	
    	int charCount = 0;
    	for (CharSequence cs : serviceDescriptions)
    		charCount += cs.length();

        //System.out.println("#articles=" + serviceDescriptions.size() + " #chars=" + charCount);

        CharSequence[] articleTexts
            = serviceDescriptions
            .<CharSequence>toArray(new CharSequence[serviceDescriptions.size()]);
        return articleTexts;
    }
    
    /*
    public TokenizerFactory wormbaseTokenizerFactory() {
        TokenizerFactory factory = BASE_TOKENIZER_FACTORY;
        //factory = new LowerCaseTokenizerFactory(factory);
        //factory = new EnglishStopTokenizerFactory(factory);
        return factory;
    }
    */
    
    
    public double getTimeDuration() {
    	return interval;
    }
    
    public double[][] setPdz() {
		//vectorToIdMapping = new HashMap<String, Double[]>();
		double[][] Pdz = new double[numSamples][numTopics];
		ObjectDataStructure objectInterface;
		double[] serviceIndex = new double[numTopics];
                
                databaseInterface.deleteDB();
                
		for (int doc = 0; doc < numSamples; ++doc) {
			objectInterface = objectRegistryMap.get(doc);
			String resourceID = objectInterface.getObjectID();
			for (int topic = 0; topic < numTopics; ++topic) {
					Pdz[doc][topic] =  sample.documentTopicProb(doc, topic);
					serviceIndex[topic] = sample.documentTopicProb(doc, topic);
			}
			//vectorToIdMapping.put(resourceID, serviceIndex);
			
			databaseInterface.storeEntry(new ObjectIndex(resourceID, serviceIndex));
			//System.out.println(vectorToIdMapping);
		}
		
		return Pdz;
	}
    
    public double[][] getPdz() {
    	return pdz;
    }
    
    public double[] getClusterAssignments() {
    	double[] Pz = new double[numSamples];
		
		for (int doc = 0; doc < numSamples; ++doc) {
			double max = 0;
			for (int topic = 0; topic < numTopics; ++topic) {
				if(max < sample.documentTopicProb(doc, topic))
				{
					max = sample.documentTopicProb(doc, topic);
					Pz[doc] =  topic;
				}
			}
		}
		return Pz;
    }
    
    public double[][] setPwz() {
    	double[][] topicWordProbs = new double[numTopics][numWords];
    	for(int topic = 0; topic < numTopics; topic++) {
    		for(int word = 0; word < numWords; word++) {
    			topicWordProbs[topic][word] = sample.topicWordProb(topic, word);
    		}
    	}
    	return topicWordProbs;
    }
    
    public double[][] getPwz() {
    	return pwz;
    }
  
    public static void main(String[] args) 
	{
    	ConfigParameters cp = new ResourceConfigParams();
		LDATraining ldac = new LDATraining(20, cp);
		
	}

}
