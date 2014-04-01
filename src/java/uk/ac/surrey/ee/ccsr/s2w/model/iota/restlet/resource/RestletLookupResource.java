/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.config.old.RestfulIfParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.ClientAcceptFormatter;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WRegistrationResponse;

/**
 * Resource which has only one representation.
 */
public class RestletLookupResource extends ServerResource {

    Parameters param = new Parameters();
    RestfulIfParameters riParam = new RestfulIfParameters();
    ConfigParameters cp;

    @Get
    public Representation handleRequest() {

        String objId = (String) getRequest().getAttributes().get("objectID");
        String objType = (String) getRequest().getResourceRef().getPath();
        String format = (String) getQuery().getFirstValue("resultFormat");

        String result = lookup(objType, objId, format);

        StringRepresentation resultInFormat = new StringRepresentation(result);
        MediaType mt = getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();
        System.out.println("Request Accepts is: " + mt);
        ClientAcceptFormatter rdfFormat = new ClientAcceptFormatter();
        resultInFormat = rdfFormat.formatRdf(resultInFormat, format, mt);

        return resultInFormat;
    }

    public String lookup(String objType, String objId, String format) {

        boolean stored = false;
        boolean indexed = false;

        if (objType.contains(ResourceConfigParams.objType)) {
            cp = new ResourceConfigParams();
        } else if (objType.contains(EntityConfigParams.objType)) {
            cp = new EntityConfigParams();
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cp = new ServiceConfigParams();
        } else {
            S2WRegistrationResponse rr = new S2WRegistrationResponse(objId, cp.getObjType(), stored, indexed, null, "object type unknown");
            String result = new Gson().toJson(rr);
            return result;            
        }

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
        Model repo = sdbAH.loadAllModelInstancesFromSdb();
        JenaModelMgmt jmm = new JenaModelMgmt(cp, objId);
        String result = "";
        if (objId.equalsIgnoreCase("ALL")) {
            result = jmm.getRdfModelInFormat(repo, format);
        } else {
            result = jmm.getRdfInstanceInFormat(repo, format);
        }

        sdbAH.getStore().close();

        return result;

    }
}