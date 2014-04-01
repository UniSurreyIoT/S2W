/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet;

import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletDeleteResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletRegisterInstanceResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletSparqlResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletSanityCheckResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletUpdateResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletDiscoveryResource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletLookupResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource.RestletRegisterDatasetResource;

public class RestReqApplication extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    
    public static final String restletPath = "/repository";
    
    public static final String registerPrefix = "/register";
    public static final String lookupPrefix = "/lookup";
    public static final String updatePrefix = "/update";
    public static final String deletePrefix = "/delete";
    public static final String queryPrefix = "/query/sparql";
    public static final String discoverPrefix = "/discover";
    
    
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
        Router router = new Router(getContext());
        
        router.attach("/getVersion", RestletSanityCheckResource.class);
        
        router.attach(registerPrefix+ResourceConfigParams.descTypeLinkSuffix, RestletRegisterDatasetResource.class);
        router.attach(registerPrefix+EntityConfigParams.descTypeLinkSuffix, RestletRegisterDatasetResource.class);
        router.attach(registerPrefix+ServiceConfigParams.descTypeLinkSuffix, RestletRegisterDatasetResource.class);
                                        
        router.attach(registerPrefix+ResourceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletRegisterInstanceResource.class);
        router.attach(registerPrefix+EntityConfigParams.descTypeLinkSuffix+"/{objectID}", RestletRegisterInstanceResource.class);
        router.attach(registerPrefix+ServiceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletRegisterInstanceResource.class);
        
        router.attach(lookupPrefix+ResourceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletLookupResource.class);
        router.attach(lookupPrefix+EntityConfigParams.descTypeLinkSuffix+"/{objectID}", RestletLookupResource.class);
        router.attach(lookupPrefix+ServiceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletLookupResource.class);

        router.attach(updatePrefix+ResourceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletUpdateResource.class);
        router.attach(updatePrefix+EntityConfigParams.descTypeLinkSuffix+"/{objectID}", RestletUpdateResource.class);
        router.attach(updatePrefix+ServiceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletUpdateResource.class);
        
        router.attach(deletePrefix+ResourceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletDeleteResource.class);
        router.attach(deletePrefix+EntityConfigParams.descTypeLinkSuffix+"/{objectID}", RestletDeleteResource.class);
        router.attach(deletePrefix+ServiceConfigParams.descTypeLinkSuffix+"/{objectID}", RestletDeleteResource.class);

        router.attach(queryPrefix+ResourceConfigParams.descTypeLinkSuffix, RestletSparqlResource.class);
        router.attach(queryPrefix+EntityConfigParams.descTypeLinkSuffix, RestletSparqlResource.class);
        router.attach(queryPrefix+ServiceConfigParams.descTypeLinkSuffix, RestletSparqlResource.class);
        
        router.attach(discoverPrefix+ResourceConfigParams.descTypeLinkSuffix, RestletDiscoveryResource.class);
        router.attach(discoverPrefix+EntityConfigParams.descTypeLinkSuffix, RestletDiscoveryResource.class);
        router.attach(discoverPrefix+ServiceConfigParams.descTypeLinkSuffix, RestletDiscoveryResource.class);
        
        router.attach(discoverPrefix+"/status", RestletDiscoveryResource.class);
        
        return router;
    }

}
