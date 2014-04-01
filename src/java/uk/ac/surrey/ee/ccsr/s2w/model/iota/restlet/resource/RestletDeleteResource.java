/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;
import javax.servlet.ServletContext;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WDeleteResponse;

/**
 * Resource which has only one representation.
 */
public class RestletDeleteResource extends ServerResource {

//    Parameters param = new Parameters();
//    RestfulIfParameters riParam = new RestfulIfParameters();
    ConfigParameters cp;
    
     @Delete
    public Representation handleRequest() {

        String objId = (String) getRequest().getAttributes().get("objectID");
        String objType = (String) getRequest().getResourceRef().getPath();

        String resultInJson = delete(objType, objId);

        StringRepresentation result = new StringRepresentation(resultInJson);
        result.setMediaType(MediaType.APPLICATION_JSON);
        return result;

    }

    public String delete(String objType, String objId) {

        ServletContext context = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");

        boolean deleted = false;

        if (objType.contains(ResourceConfigParams.objType)) {
            cp = new ResourceConfigParams(context);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cp = new EntityConfigParams(context);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cp = new ServiceConfigParams(context);
        } else {
            S2WDeleteResponse dr = new S2WDeleteResponse(objId, cp.getObjType(), deleted, "object type unknown");           
            return new Gson().toJson(dr);
        }

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
        Model model = sdbAH.loadAllModelInstancesFromSdb();
        JenaModelMgmt jmm = new JenaModelMgmt(cp, objId);

        deleted = jmm.deleteRdfInstance(model);

        sdbAH.getStore().close();

        S2WDeleteResponse dr = new S2WDeleteResponse(objId, cp.getObjType(), deleted, "OK");

        return new Gson().toJson(dr);


    }

   
}