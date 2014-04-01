/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.resource;

import com.google.gson.Gson;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.ServletContext;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.associations.IotaAssociationGenerator;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.config.old.RestfulIfParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WDatasetRegResponse;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WRegistrationResponse;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.FoldInHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.RetrainAfterTrain;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.RuntimeETHandler;
import uk.ac.surrey.ee.ccsr.s2w.spatial.Geohash;

/**
 *
 * @author te0003
 */
public class RestletRegisterDatasetResource extends ServerResource {

    public ConfigParameters cp;

    @Post
    public Representation registerDescription(Representation entity) throws ResourceException, IOException {

        ServletContext sc = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes()
                .get("org.restlet.ext.servlet.ServletContext");
        String regDescription = entity.getText();
        String objType = (String) getRequest().getResourceRef().getPath();
        //String objId = (String) getRequest().getAttributes().get("objectID");

        boolean stored = false;
        boolean indexed = false;
        String errorMessage ="";

        if (objType.contains(ResourceConfigParams.objType)) {
            cp = new ResourceConfigParams(sc);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cp = new EntityConfigParams(sc);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cp = new ServiceConfigParams(sc);
        } else {
            S2WRegistrationResponse rr = new S2WRegistrationResponse("dataset", cp.getObjType(), stored, indexed, null, "object type unknown");
            return new StringRepresentation(new Gson().toJson(rr));
        }

        StringReader srToStore = new StringReader(regDescription);

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);

        try {
            sdbAH.storeRDFtoSDB(srToStore);
            stored = true;
        } catch (Exception e) {
            //return new StringRepresentation(param.unSuccessPubMsg);
            stored = false;
        }

        Thread objectRetrain = cp.getRetrainEngineThread();
        Thread retrainAfterTrain = cp.getRetrainAfterTrain();

        if (!objectRetrain.isAlive()) {
            System.out.println("Re-training started");
            objectRetrain = new Thread(new RuntimeETHandler(cp)); // <----starts run() in THIS class
            objectRetrain.setPriority(Thread.MAX_PRIORITY);
            objectRetrain.start();
            indexed=true;
            errorMessage="undergoing index retrain";
            
        } else if (!retrainAfterTrain.isAlive()) {
            retrainAfterTrain = new Thread(new RetrainAfterTrain(objectRetrain));
            retrainAfterTrain.start();
            indexed=true;
            errorMessage="queued for index retrain";
        }
        
        S2WDatasetRegResponse rr = new S2WDatasetRegResponse(cp.getObjType(), stored, indexed, errorMessage);        
        StringRepresentation result = new StringRepresentation(new Gson().toJson(rr));
        result.setMediaType(MediaType.APPLICATION_JSON);
        
        sdbAH.getStore().close();
        
        return result;

    }
}
