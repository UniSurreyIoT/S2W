package uk.ac.surrey.ee.ccsr.s2w.probengine.handler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.LDATraining;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.ObjectDataStructure;

import com.hp.hpl.jena.rdf.model.Model;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModelTraining {

    ArrayList<String> conceptList; //The List of All Known Concepts
    Hashtable<Integer, String> NumberToConceptMap; //A mapping of numbers to the corresponding concept name	
    HashMap<Integer, ObjectDataStructure> objectRegistryMap; //A registry containing the mapping of object number to objectDataStructure.

    public ModelTraining() {
        // TODO Auto-generated constructor stub
    }

    public void initialiseTrainingSequence(ConfigParameters cp) {

        System.out.println("delete *.data files");

        Path path = Paths.get(cp.getUnseenConcepts());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
        deleteDatFiles(path);
        path = Paths.get(cp.getSTM1());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
        deleteDatFiles(path);
        path = Paths.get(cp.getSTM2());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
        deleteDatFiles(path);
        path = Paths.get(cp.getLdaModel());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
        deleteDatFiles(path);

        ArrayList<ObjectDataStructure> trainingSet = getTrainingSet(cp); //get the training set that will be used for training
        System.out.println("number of samples: " + trainingSet.size());
        System.out.println("minimum number of samples required: " + cp.getTraining_set_min());
        if (trainingSet.size() < cp.getTraining_set_min()) {
            System.out.println("Training for database: \"" + cp.getMysqlDbUrl() + "\" ABORTED due to insufficient number of samples\n");
            return;
        }
        populateConceptList(trainingSet, cp);	//Create a list of known concepts and map them to numbers

        createSTM(trainingSet, cp);		// Create the transaction matrix

        LDATraining ldaTrain = new LDATraining(20, cp);  //Train the LDA model
        System.out.println("Training for \"" + cp.getMysqlDbUrl() + "\" is done!\n");

    }
    
    public void deleteDatFiles(Path path){
		
		try {
		    Files.delete(path);
		    System.out.println("DELETED file with path" + path.toString());
		} catch (NoSuchFileException x) {
		    System.err.format("%s: no such" + " file or directory%n", path);
		} catch (DirectoryNotEmptyException x) {
		    System.err.format("%s not empty%n", path);
		} catch (IOException x) {
		    // File permission problems are caught here.
		    System.err.println(x);
		}
		
	}

    public ArrayList<ObjectDataStructure> getTrainingSet(ConfigParameters cp) {

        SdbAccessHandler sdbHandler = new SdbAccessHandler(cp);
        Model mainModel = sdbHandler.loadAllModelInstancesFromSdb();
        JenaModelMgmt cff = new JenaModelMgmt(cp);
        ArrayList<ObjectDataStructure> obs = cff.getConceptsFromModel(mainModel);

//		System.out.println("The training set is: ");
//		System.out.println(obs);
        
        sdbHandler.getStore().close();

        return obs;
    }

    public void populateConceptList(ArrayList<ObjectDataStructure> trainingSet, ConfigParameters cp) {
        conceptList = new ArrayList<>();
        NumberToConceptMap = new Hashtable<Integer, String>();
        objectRegistryMap = new HashMap<Integer, ObjectDataStructure>();

        int conceptCounter = 0; //Used as index for mapping the concepts to numbers
        int objectCounter = 0; //Required for the objectRegistryMap

        for (ObjectDataStructure ods : trainingSet) {

            //For each objectDataStructure, read the arraylist of concets.
            //For each concept, check if the concept has already been seen and, if it has not, save it to the List of All Known Concepts.
            ArrayList<String> serviceConcepts = ods.getAllConcepts();
            for (String concept : serviceConcepts) {
                if (!conceptList.contains(concept)) {
                    //A mapping of Number to Concept Name is kept in NumbertoConceptMap.
                    NumberToConceptMap.put(conceptCounter, concept);
                    conceptList.add(concept);
                    conceptCounter++;
                }
            }
            objectRegistryMap.put(objectCounter, ods);
            objectCounter++;
        }

        try {

            //FileOutputStream f_outA = new FileOutputStream ("STM1.data");
            FileOutputStream f_outA = new FileOutputStream(cp.getSTM1());

            // Use an ObjectOutputStream to send object data to the
            // FileOutputStream for writing to disk.
            ObjectOutputStream obj_outA = new ObjectOutputStream(f_outA);

            obj_outA.writeObject(NumberToConceptMap);
            obj_outA.writeObject(conceptList);
            obj_outA.writeObject(objectRegistryMap);

            obj_outA.close();
            f_outA.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Creates the transaction matrix - requires the number of objects in order to intialise the size of the matrix.
    public void createSTM(ArrayList<ObjectDataStructure> trainingSet, ConfigParameters cp) {

        int numberOfObjects = trainingSet.size();
        double[][] serviceByConceptArray = new double[numberOfObjects][conceptList.size()];
        int[][] ldaInput = new int[numberOfObjects][];
        int i;

        int objectCounter = 0; //Required for keeping track of the object number.
        for (ObjectDataStructure ods : trainingSet) {

            ArrayList<String> serviceConcepts = ods.getAllConcepts();
            ArrayList<Integer> tempConceptCount = new ArrayList<Integer>();
            for (String concept : serviceConcepts) {
                if (conceptList.contains(concept)) {
                    i = conceptList.indexOf(concept);
                    serviceByConceptArray[objectCounter][i] += 1;
                    tempConceptCount.add(i);
                }
            }

            ldaInput[objectCounter] = new int[tempConceptCount.size()];
            for (int b = 0; b < tempConceptCount.size(); b++) {
                ldaInput[objectCounter][b] = tempConceptCount.get(b);
            }
            objectCounter++;
        }


        try {

            //FileOutputStream f_outA = new FileOutputStream ("STM2.data");
            FileOutputStream f_outA = new FileOutputStream(cp.getSTM2());

            // Use an ObjectOutputStream to send object data to the
            // FileOutputStream for writing to disk.
            ObjectOutputStream obj_outA = new ObjectOutputStream(f_outA);

            obj_outA.writeObject(serviceByConceptArray);
            obj_outA.writeObject(ldaInput);

            obj_outA.close();
            f_outA.close();


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//		ModelTraining mt = new ModelTraining();
//		mt.initialiseTrainingSequence();
    }
}
