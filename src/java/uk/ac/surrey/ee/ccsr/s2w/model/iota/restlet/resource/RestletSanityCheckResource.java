/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import java.io.IOException;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Resource which has only one representation.
 */
public class RestletSanityCheckResource extends ServerResource {

    

    @Get
    public Representation getDescription() {

        //get servlet context for getting files
        String result = "<sanityCheck>\n"
                + "  <name>IoT Discovery</name>\n"
                + "  <type>Sanity Check</type>\n"
                + "  <version>Version: 2.3.0.SNAPSHOT</version>\n"
                + "  </sanityCheck>";      
        
        return new StringRepresentation(result, MediaType.TEXT_XML);
    }
    
    @Post
    public Representation postTest(Representation entity) throws ResourceException, IOException {
        
        String message = entity.getText();
        
        return new StringRepresentation(message);
    }
}