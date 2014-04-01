/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.coap;

import uk.ac.surrey.ee.ccsr.s2w.coap.resources.*;
import ch.ethz.inf.vs.californium.server.Server;
import java.io.File;
import java.util.concurrent.Executors;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import uk.ac.surrey.ee.ccsr.s2w.config.*;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.RestReqApplication;

/**
 *
 * @author te0003
 */
public class CoapServerContextListener implements ServletContextListener {

    public Server server;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // start the thread

        System.out.println("intializing CoAP server");
        System.out.println("context-path: " + sce.getServletContext().getContextPath());
        System.out.println("real-path: " + sce.getServletContext().getRealPath(File.separator));

        server = new Server();
        server.setExecutor(Executors.newScheduledThreadPool(4));

        //test
        server.add(new HelloWorldResource("hello"));

        //s2w resources
        
        //register
        server.add(new CoapRegisterResource("repository" + RestReqApplication.registerPrefix + ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapRegisterResource("repository" + RestReqApplication.registerPrefix + EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapRegisterResource("repository" + RestReqApplication.registerPrefix + ServiceConfigParams.descTypeLinkSuffix));
        //lookup
        server.add(new CoapLookupResource("repository"+ RestReqApplication.lookupPrefix+ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapLookupResource("repository"+ RestReqApplication.lookupPrefix+EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapLookupResource("repository"+ RestReqApplication.lookupPrefix+ServiceConfigParams.descTypeLinkSuffix));
        //update
        server.add(new CoapUpdateResource("repository"+ RestReqApplication.updatePrefix+ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapUpdateResource("repository"+ RestReqApplication.updatePrefix+EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapUpdateResource("repository"+ RestReqApplication.updatePrefix+ServiceConfigParams.descTypeLinkSuffix));        
        //delete
        server.add(new CoapDeleteResource("repository"+ RestReqApplication.deletePrefix+ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapDeleteResource("repository"+ RestReqApplication.deletePrefix+EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapDeleteResource("repository"+ RestReqApplication.deletePrefix+ServiceConfigParams.descTypeLinkSuffix));
        //sparql
        server.add(new CoapSparqlResource("repository"+ RestReqApplication.queryPrefix+ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapSparqlResource("repository"+ RestReqApplication.queryPrefix+EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapSparqlResource("repository"+ RestReqApplication.queryPrefix+ServiceConfigParams.descTypeLinkSuffix));
        //discover
        server.add(new CoapDiscoveryResource("repository"+ RestReqApplication.discoverPrefix+ResourceConfigParams.descTypeLinkSuffix));
        server.add(new CoapDiscoveryResource("repository"+ RestReqApplication.discoverPrefix+EntityConfigParams.descTypeLinkSuffix));
        server.add(new CoapDiscoveryResource("repository"+ RestReqApplication.discoverPrefix+ServiceConfigParams.descTypeLinkSuffix));

        server.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // stop the thread

        server.stop();
        System.out.println("Context Listener for CoAP server Destroyed");

    }
}
