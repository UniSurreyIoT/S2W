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
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import uk.ac.surrey.ee.ccsr.s2w.associations.IotaAssociationGenerator;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.config.old.RestfulIfParameters;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WUpdateResponse;
import uk.ac.surrey.ee.ccsr.s2w.spatial.Geohash;

/**
 * Resource which has only one representation.
 */

public class RestletUpdateResource extends ServerResource {

    Parameters param = new Parameters();
    RestfulIfParameters riParam = new RestfulIfParameters();
    ConfigParameters cp;

    @Put
    public Representation putDescription(Representation entity) throws ResourceException, IOException {

        

        String objId = (String) getRequest().getAttributes().get("objectID");
        String description = entity.getText();
        String objType = (String) getRequest().getResourceRef().getPath();
               
        String resultInJson = update(objType, objId, description);      

        StringRepresentation result = new StringRepresentation(resultInJson);
        result.setMediaType(MediaType.APPLICATION_JSON);
        
        return result;

    }
    
    public String update(String objType, String objId, String description){
        
        boolean stored = false;
        
          ServletContext sc = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes()
                .get("org.restlet.ext.servlet.ServletContext");

        if (objType.contains(ResourceConfigParams.objType)) {
            cp = new ResourceConfigParams(sc);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cp = new EntityConfigParams(sc);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cp = new ServiceConfigParams(sc);
        } else {
            S2WUpdateResponse ur = new S2WUpdateResponse(objId, cp.getObjType(), stored, null, "object type unknown");
            return new Gson().toJson(ur);
        }

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
        StringReader descToUpdate = new StringReader(description);

        OntModel modelToUpdate = ModelFactory.createOntologyModel();
        Model ontologyModel = FileManager.get().loadModel(cp.getONTOLOGY_LOCAL_URL());

        modelToUpdate.addSubModel(ontologyModel);

        System.out.println("read into Jena model");
        modelToUpdate.read(descToUpdate, null);
        modelToUpdate.write(System.out, "RDF/XML-ABBREV");

        double hasLatValue = 0;
        double hasLonValue = 0;
        String hasAttributeTypeVal = "";

        StmtIterator iterM = modelToUpdate.listStatements();
        while (iterM.hasNext()) {
            Statement stmt = iterM.nextStatement();  // get next statement
            String predicate = stmt.getPredicate().getLocalName();   // get the predicate            
            if (predicate.equalsIgnoreCase("hasLatitude")) {
                String object = stmt.getObject().asLiteral().getValue().toString();
                System.out.println("hasLatitude: " + object);      // get the object
                hasLatValue = Double.parseDouble(object);
            } else if (predicate.equalsIgnoreCase("hasLongitude")) {
                String object = stmt.getObject().asLiteral().getValue().toString();
                System.out.println("hasLongitude: " + object);      // get the object
                hasLonValue = Double.parseDouble(object);
            } else if (predicate.equalsIgnoreCase("hasAttributeType")) {
                String object = stmt.getObject().toString();
                System.out.println("hasAttributeType: " + object);      // get the object
                hasAttributeTypeVal = object;

            }
        }


        Individual objectIndividual = modelToUpdate.getIndividual(cp.getOntoUri() + objId);

        //double hasLatValue = Double.parseDouble(objectIndividual.getPropertyValue(modelToUpdate.getProperty(cp.getOntoUri() + "hasLatitude")).asLiteral().getValue().toString());
        System.out.println("hasLatitude: " + hasLatValue);
        //double hasLonValue = Double.parseDouble(objectIndividual.getPropertyValue(modelToUpdate.getProperty(cp.getOntoUri() + "hasLongitude")).asLiteral().getValue().toString());
        System.out.println("hasLongitude: " + hasLonValue);
        Geohash e = new Geohash();
        String hasGeohashValue = e.encode(hasLatValue, hasLonValue);
        System.out.println("Geohash: " + hasGeohashValue);
        OntProperty pGeohash = modelToUpdate.createOntProperty(cp.getOntoUri() + "hasGeohash");
        objectIndividual.addProperty(pGeohash, modelToUpdate.createTypedLiteral(hasGeohashValue));

        String associatedService = "";
        if (cp.getObjType().equalsIgnoreCase(EntityConfigParams.objType)) {

            //OntProperty pHasAttributeType = modelToUpdate.getOntProperty(cp.getOntoUri() + "hasAttributeType");
            //String hasAttributeTypeVal = objectIndividual.getProperty(pHasAttributeType).getObject().asLiteral().getString();
            System.out.println("Feature: " + hasAttributeTypeVal);
            OntProperty pAssoc = modelToUpdate.createOntProperty(cp.getOntoUri() + "isAssociatedToService");
            IotaAssociationGenerator iotaAssocGen = new IotaAssociationGenerator(cp);
            associatedService = iotaAssocGen.getAssociationWithService(hasGeohashValue, hasAttributeTypeVal);
            objectIndividual.addProperty(pAssoc, modelToUpdate.createTypedLiteral(associatedService));
        }
        String errorMessage = "OK";
        try {
            JenaModelMgmt jmm = new JenaModelMgmt(cp, objId);
            Model ontology = sdbAH.loadAllModelInstancesFromSdb();
            stored = jmm.updateRdfInstanceInSDB(ontology, modelToUpdate);
        } catch (Exception exc) {
            //exc.printStackTrace();
            errorMessage = exc.getMessage();
            System.out.println("WARNING -> " + errorMessage);
            stored = false;
        }
        
        sdbAH.getStore().close();

        S2WUpdateResponse rr = new S2WUpdateResponse(objId, cp.getObjType(), stored, associatedService, errorMessage);
        
        return new Gson().toJson(rr);
    
    }
}