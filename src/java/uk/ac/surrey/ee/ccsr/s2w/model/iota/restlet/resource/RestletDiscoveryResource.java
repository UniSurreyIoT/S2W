/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import com.google.gson.Gson;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.ClientAcceptFormatter;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2W_ETStatusResponse;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.NonLogicMatchmaking;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.ObjectDataStructure;

/**
 * Resource which has only one representation.
 */
public class RestletDiscoveryResource extends ServerResource {

    Parameters param = new Parameters();

    @Get
    public Representation discoveryStatus() {

        ServletContext context = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");

        String objType = (String) getRequest().getResourceRef().getPath();
        System.out.println(objType);


        boolean resourceIndex = false;
        boolean entityIndex = false;
        boolean serviceIndex = false;

        ConfigParameters cp = new ResourceConfigParams(context);
        Path path = Paths.get(cp.getLdaModel());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            resourceIndex = true;
        }
        cp = new EntityConfigParams(context);
        path = Paths.get(cp.getLdaModel());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            entityIndex = true;
        }
        cp = new ServiceConfigParams(context);
        path = Paths.get(cp.getLdaModel());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            serviceIndex = true;
        }

        S2W_ETStatusResponse rr = new S2W_ETStatusResponse(resourceIndex, entityIndex, serviceIndex);
        //return new StringRepresentation(new Gson().toJson(rr));
        StringRepresentation result = new StringRepresentation(new Gson().toJson(rr));
        result.setMediaType(MediaType.APPLICATION_JSON);
        return result;

    }

    @Post
    public Representation handleRequest(Representation entity) throws IOException {

        String queryString = entity.getText();
        String objType = (String) getRequest().getResourceRef().getPath();
        String queryType = (String) getQuery().getFirstValue("queryType"); //template, sparql
        String format = (String) getQuery().getFirstValue("resultFormat");
        MediaType mt = getClientInfo().getAcceptedMediaTypes().get(0).getMetadata();

        String result = discover(objType, queryType, queryString, format);

        StringRepresentation resultInFormat = new StringRepresentation(result);

        ClientAcceptFormatter rdfFormat = new ClientAcceptFormatter();
        resultInFormat = rdfFormat.formatRdf(resultInFormat, format, mt);

        return resultInFormat;

    }

    public String discover(String objType, String queryType, String queryString,  String format) throws IOException {

        ServletContext context = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes().get("org.restlet.ext.servlet.ServletContext");
        System.out.println(context.getRealPath(""));
        System.out.println(context.getContextPath());

        ConfigParameters cParams = null;
        if (objType.contains(ResourceConfigParams.objType)) {
            cParams = new ResourceConfigParams(context);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cParams = new EntityConfigParams(context);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cParams = new ServiceConfigParams(context);
        } else {
            return param.invalidRepoMsg;
        }

        Path path = Paths.get(cParams.getIndex());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {

            StringReader sr = new StringReader(queryString);

            Model mQuery = ModelFactory.createDefaultModel();
            mQuery.read(sr, cParams.getOntoUri());

            JenaModelMgmt crmInstance = new JenaModelMgmt(cParams);
            ArrayList<ObjectDataStructure> obsForDiscovery = crmInstance.getConceptsFromModel(mQuery);
            System.out.println("size is: " + obsForDiscovery.size());
            System.out.println(obsForDiscovery.get(0).getAllConcepts().toString());
            NonLogicMatchmaking nlmQ = new NonLogicMatchmaking(cParams);
            ArrayList<String> descriptionIDs = nlmQ.matchToQuery(obsForDiscovery.get(0)); //only one allowed in query

            SdbAccessHandler sdbHandler = new SdbAccessHandler(cParams);
            Model mInStore = sdbHandler.loadAllModelInstancesFromSdb();
            String resultsInFormat = crmInstance.getRdfInstancesInFormat(mInStore, descriptionIDs, format);

            sdbHandler.getStore().close();

            return resultsInFormat;
        } else {
            String statusMessage = discoveryStatus().getText();
            return statusMessage;
        }


    }
}
