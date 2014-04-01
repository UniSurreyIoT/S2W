package uk.ac.surrey.ee.ccsr.s2w.probengine.handler;

import java.util.HashMap;
import java.util.Vector;


import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.*;

import com.hp.hpl.jena.rdf.model.Model;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RetrainAfterTrain implements Runnable {

    
    public ConfigParameters cp;
    public Thread trainThread;
   // public static HashMap<String, Vector<String>> hMapFoldInQueue;

    public RetrainAfterTrain(Thread trainThread) {

        this.trainThread = trainThread;
    }

    @Override
    public void run() {

        System.out.println("waiting for training thread to finish!");
        try {
            trainThread.join();
            trainThread.start();
            
        } catch (InterruptedException ex) {
            Logger.getLogger(RetrainAfterTrain.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        

    }
}
