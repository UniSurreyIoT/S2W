/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.probengine.servlet;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.FoldInHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.ModelTraining;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.RuntimeETHandler;

/**
 *
 * @author te0003
 */


public class ETContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
         // start the thread
        
        
		System.out.println("intializing registration servlet");
		System.out.println("context-path: "+ sce.getServletContext().getContextPath());
		System.out.println("real-path: "+ sce.getServletContext().getRealPath(File.separator));	
		
		ServletContext context = sce.getServletContext();
		
		FoldInHandler.setFTRatio= Double.parseDouble((context.getInitParameter("retrain_threshold")));
		System.out.println("retrain_threshold set to: "+ FoldInHandler.setFTRatio);		

		RuntimeETHandler.hMapFoldInQueue=new HashMap<>();
		RuntimeETHandler.hMapFoldInQueue.put("Resource", new Vector<String>());
		RuntimeETHandler.hMapFoldInQueue.get("Resource").ensureCapacity(1000);
		RuntimeETHandler.hMapFoldInQueue.put("Entity", new Vector<String>());
		RuntimeETHandler.hMapFoldInQueue.get("Entity").ensureCapacity(1000);
		RuntimeETHandler.hMapFoldInQueue.put("Service", new Vector<String>());
		RuntimeETHandler.hMapFoldInQueue.get("Service").ensureCapacity(1000);	

		ConfigParameters cp = new ResourceConfigParams(context);
//		cp.setRetrainEngineThread(new Thread( new RuntimeETHandler(cp)));
                ResourceConfigParams.retrainEngineThread= new Thread( new RuntimeETHandler(cp));
		ModelTraining mt = new ModelTraining();
		System.out.println("intializing training for: "+ cp.getMysqlDbUrl());
		mt.initialiseTrainingSequence(cp);
		
		cp = new EntityConfigParams(context);
//		RuntimeETHandler.entityRetrain = new Thread( new RuntimeETHandler(cp));
                EntityConfigParams.retrainEngineThread= new Thread( new RuntimeETHandler(cp));
		System.out.println("intializing training for: "+ cp.getMysqlDbUrl()); 
		mt.initialiseTrainingSequence(cp);
		
		cp = new ServiceConfigParams(context);	
//		RuntimeETHandler.serviceRetrain = new Thread( new RuntimeETHandler(cp));
                ServiceConfigParams.retrainEngineThread= new Thread( new RuntimeETHandler(cp));
		System.out.println("intializing training for: "+ cp.getMysqlDbUrl());
		mt.initialiseTrainingSequence(cp);
		
		System.out.println("intializing training DONE!\n");
        
    }

    public void contextDestroyed(ServletContextEvent sce) {
         // stop the thread
        
        System.out.println("Context Listener for Startup Engine Training Destroyed");

    }
}
