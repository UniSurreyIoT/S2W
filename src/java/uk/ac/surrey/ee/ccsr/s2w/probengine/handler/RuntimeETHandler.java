package uk.ac.surrey.ee.ccsr.s2w.probengine.handler;

import java.util.HashMap;
import java.util.Vector;


import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.*;

import com.hp.hpl.jena.rdf.model.Model;

public class RuntimeETHandler implements Runnable {

    //public static ResourceListParser rlp = new ResourceListParser();
    //static final String owlFileName = "owl-samples/resources_200.owl";
    //static final String queryFileName = "sparql-templates/sparql-query-051212.txt";
    public ConfigParameters cp;
//	public static Thread resourceRetrain;	// = new Thread(this);
//	public static Thread entityRetrain;	// = new Thread(this);
//	public static Thread serviceRetrain;	// = new Thread(this);
    public static HashMap<String, Vector<String>> hMapFoldInQueue;

    public RuntimeETHandler(ConfigParameters cParams) {

        this.cp = cParams;
    }

    @Override
    public void run() {

        System.out.println("startup engine training started!");

        ModelTraining mt = new ModelTraining();
        mt.initialiseTrainingSequence(cp);
        Vector<String> descriptionIDsForFoldIn = RuntimeETHandler.hMapFoldInQueue.get(cp.getObjType());
        // check if there are descriptions to be folded-in
        if (descriptionIDsForFoldIn != null && !(descriptionIDsForFoldIn.isEmpty())) {

            JenaModelMgmt cff = new JenaModelMgmt(cp);
            SdbAccessHandler sdbHandler = new SdbAccessHandler(cp);
            Model database = sdbHandler.loadAllModelInstancesFromSdb();

            for (int n = 0; n < descriptionIDsForFoldIn.size(); n++) {
                ObjectDataStructure obsToFoldIn = cff.getConceptsFromInstance(
                        database, null);
                LDAFoldIn ldaFoldIn = new LDAFoldIn(cp);
                ldaFoldIn.publishNewResource(obsToFoldIn);
            }

            descriptionIDsForFoldIn.removeAllElements();
            sdbHandler.getStore().close();
        }
        RuntimeETHandler.hMapFoldInQueue.put(cp.getObjType(), descriptionIDsForFoldIn);

    }
}
