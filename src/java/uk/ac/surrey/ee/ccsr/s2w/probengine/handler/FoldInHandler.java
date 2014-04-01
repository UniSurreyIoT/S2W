package uk.ac.surrey.ee.ccsr.s2w.probengine.handler;

import java.util.ArrayList;
import java.util.Vector;

import com.hp.hpl.jena.rdf.model.Model;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.LDAFoldIn;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.ObjectDataStructure;

public class FoldInHandler {

    public ConfigParameters cp;
    public static double setFTRatio = 0.1; // CONFIG PARAMETER
    public static double currentFTRatio = 0;

    public FoldInHandler(ConfigParameters cParams) {

        this.cp = cParams;
    }

    public void foldInModel(Model modelToRegister) throws Exception {

        Path path = Paths.get(cp.getIndex());
        Thread objectRetrain = cp.getRetrainEngineThread();

        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            JenaModelMgmt cff = new JenaModelMgmt(cp);
            System.out.println("extract concepts from model");
            ArrayList<ObjectDataStructure> obsForRegister = cff.getConceptsFromModel(modelToRegister);
            System.out.println("size of Concepts Array: " + obsForRegister.size());


            if (!objectRetrain.isAlive()) {
                // fold in description
                System.out.println("fold in descriptions");
                for (int i = 0; i < obsForRegister.size(); i++) {

                    LDAFoldIn lFoldIn = new LDAFoldIn(cp);
                    System.out.println(obsForRegister.get(i).getAllConcepts().toString());
                    lFoldIn.publishNewResource(obsForRegister.get(i));

                    currentFTRatio = lFoldIn.getNewtoOldConceptsRatio();
                    System.out.println("currentFTRatio is:" + currentFTRatio);

                    if (currentFTRatio > setFTRatio) {
                        System.out.println("currentFTRatio is higher that set FT ratio");
                        System.out.println("Re-training started");
                        objectRetrain = new Thread(new RuntimeETHandler(cp)); // <----starts run() in THIS class
                        objectRetrain.setPriority(Thread.MAX_PRIORITY);
                        objectRetrain.start();
                        currentFTRatio = 0;
                        break;
                    }
                }
            } else {
                // extract ID and store in vector
                System.out.println("training is already running, so queue descriptions for folding-in");
                Vector<String> descriptionIDsForFoldIn = RuntimeETHandler.hMapFoldInQueue.get(cp.objType);
                for (int i = 0; i < obsForRegister.size(); i++) {
                    String descID = obsForRegister.get(i).getObjectID();
                    descriptionIDsForFoldIn.add(descID);
                }
                RuntimeETHandler.hMapFoldInQueue.put(cp.getObjType(), descriptionIDsForFoldIn);
            }
        } else {
            if (!objectRetrain.isAlive()) {
                System.out.println("Re-training started");
                objectRetrain = new Thread(new RuntimeETHandler(cp)); // <----starts run() in THIS class
                objectRetrain.setPriority(Thread.MAX_PRIORITY);
                objectRetrain.start();                
            }
            throw new Exception("Fold-in failed: Index not yet created");
        }


    }
}
