package uk.ac.surrey.ee.ccsr.s2w.probengine.core;
import com.aliasi.cluster.LatentDirichletAllocation;
import java.util.ArrayList;
import java.util.Random;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;

public class LDAFoldIn {

	ReadWriteUnseenConcepts rwuc;
	DatabaseInterface di;
	//LDATraining ldac;
	LatentDirichletAllocation lda;
	
	short numTopics;
    
    //specify the output prob matrix, index of the p_z_d is the index of the concepts -1
    double[] pzdNew; 
    int[] fold;
    
    double[][] topicWordProbs;

    double topicPrior = 0.1;
    double wordPrior = 0.01;
    int burninEpochs = 0;
    int sampleLag = 1;
    int numSamples;
    long randomSeed = 6474835;
    
    MatrixReader MR;
    ModelReader modReader;
	//OntologyParser3 ontologyParser;
	//MaxentTagger tagger;
	
	ArrayList<String> ConceptList;
    
    public LDAFoldIn(ConfigParameters cp) {
    	
    	rwuc = new ReadWriteUnseenConcepts(cp);
    	
    	di = new DatabaseInterface(cp);
    	
    	MR = new MatrixReader(cp);
    	modReader = new ModelReader(cp);
		//ontologyParser = new OntologyParser3();
		
		ConceptList = MR.getConceptList();

		topicWordProbs = modReader.getPwz();
    	numTopics = (short) modReader.getNumTopics();
    	numSamples = modReader.getNumSamples();
    	pzdNew = new double[numTopics];
    	
    	
    	lda = new LatentDirichletAllocation(topicPrior, topicWordProbs);
    	
    	//fold = convertToLdaInput(readOWLSQuery(theRequest));
    	
    	//pzdNew = lda.bayesTopicEstimate(fold, numSamples, burninEpochs, sampleLag, new Random(randomSeed));
    	
    	/*
    	for(int k = 0; k < pzdNew.length; k++)
    		System.out.println("The value for topic " + k + " is " + pzdNew[k]);
    		*/
    }
    
    public int[] tokenizeNewDocs(ObjectDataStructure objectInterface) {
    	
    	int[] tempTokens;
    	int i;
		
    	ArrayList<String> unSeenConcepts = rwuc.getUnseenConceptList();
    	ArrayList<Integer> tempConceptCount = new ArrayList<Integer>();
    	//ontologyParser.loadOntology(serviceURL);
		
		ArrayList<String> allConcepts = objectInterface.getAllConcepts();
		for(String concept : allConcepts) {
			if(ConceptList.contains(concept)) {
				i = ConceptList.indexOf(concept);
				tempConceptCount.add(i);
			}
			
			else {
				System.out.println("the unseen concept is: "+concept);
				unSeenConcepts.add(concept);
			}
		}
		
		tempTokens = new int[tempConceptCount.size()];
		for(int b = 0; b < tempConceptCount.size(); b++) {
			tempTokens[b] = tempConceptCount.get(b);
		}
		
		rwuc.overwriteUnseenConceptList(unSeenConcepts);
		
		return tempTokens;
    }
    
    public void setpzdNew(ObjectDataStructure objectInterface) {
    	
    	fold = tokenizeNewDocs(objectInterface);
    	
    	pzdNew = lda.bayesTopicEstimate(fold, numSamples, burninEpochs, sampleLag, new Random(randomSeed));
    	
    	//for(int k = 0; k < pzdNew.length; k++)
    		//System.out.println("The value for topic " + k + " is " + pzdNew[k]);
    	
    }
    
    public void publishNewResource(ObjectDataStructure objectInterface){
    	
    	fold = tokenizeNewDocs(objectInterface);
    	pzdNew = lda.bayesTopicEstimate(fold, numSamples, burninEpochs, sampleLag, new Random(randomSeed));
    	
    	String resourceID = objectInterface.getObjectID();
    	ObjectIndex si = new ObjectIndex(resourceID, pzdNew);
    	di.storeEntry(si);
    }
    
    
    public double[] getPdNewz() {
    	return pzdNew;
    }
    
    public double getNewtoOldConceptsRatio(){
    	ArrayList<String> unSeenConcepts = rwuc.getUnseenConceptList();
    	double sizeA = unSeenConcepts.size();
    	double sizeB = ConceptList.size();
    	double ratio = sizeA/sizeB;
    	return ratio;
    }
    
    public static void main(String[] args) 
	{
    	ConfigParameters cp = new ResourceConfigParams();
    	LDAFoldIn ldafin = new LDAFoldIn(cp);
    	//ldafin.setpzdNew("http://localhost/services/1.1/amount-of-money3wheeledcar_price_service.owls");
	}
}
