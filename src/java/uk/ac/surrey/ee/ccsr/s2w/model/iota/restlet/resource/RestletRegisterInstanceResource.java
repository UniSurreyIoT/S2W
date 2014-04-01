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
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response.S2WRegistrationResponse;
import uk.ac.surrey.ee.ccsr.s2w.probengine.handler.FoldInHandler;
import uk.ac.surrey.ee.ccsr.s2w.spatial.Geohash;

/**
 * Resource which has only one representation.
 */
public class RestletRegisterInstanceResource extends ServerResource {

    public ConfigParameters cp;

    @Post
    public Representation handleRequest(Representation entity) throws ResourceException, IOException {


        String regDescription = entity.getText();
        String objType = (String) getRequest().getResourceRef().getPath();
        String objId = (String) getRequest().getAttributes().get("objectID");

        String resultJsonString = register(objType, objId, regDescription);
        StringRepresentation result = new StringRepresentation(resultJsonString);
        result.setMediaType(MediaType.APPLICATION_JSON);

        return result;
    }

    public String register(String objType, String objId, String regDescription) {

        ServletContext sc = (ServletContext) getContext().getServerDispatcher().getContext().getAttributes()
                .get("org.restlet.ext.servlet.ServletContext");

        boolean stored = false;
        boolean indexed = false;

        if (objType.contains(ResourceConfigParams.objType)) {
            cp = new ResourceConfigParams(sc);
        } else if (objType.contains(EntityConfigParams.objType)) {
            cp = new EntityConfigParams(sc);
        } else if (objType.contains(ServiceConfigParams.objType)) {
            cp = new ServiceConfigParams(sc);
        } else {

            S2WRegistrationResponse regResponse = new S2WRegistrationResponse(objId, cp.getObjType(), stored, indexed, null, "object type unknown");
            return new Gson().toJson(regResponse);

        }

        StringReader srToStore = new StringReader(regDescription);
        StringReader srToExtract = new StringReader(regDescription);

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);

        try {
            sdbAH.storeRDFtoSDB(srToStore);
            stored = true;
        } catch (Exception e) {
            //return new StringRepresentation(param.unSuccessPubMsg);
            stored = false;
        }


        //System.out.println("extract concepts from NEW instances(s)");
        OntModel modelToRegister = ModelFactory.createOntologyModel();
        Model ontologyModel = FileManager.get().loadModel(cp.getONTOLOGY_LOCAL_URL());
        modelToRegister.addSubModel(ontologyModel);

        System.out.println("read into Jena model");
        modelToRegister.read(srToExtract, null);
        //modelToRegister.write(System.out, "RDF/XML-ABBREV");

        double hasLatValue = 0;
        double hasLonValue = 0;
        String hasAttributeTypeVal = "";
        String associatedService = "";

        StmtIterator iterM = modelToRegister.listStatements();
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

        Individual objectIndividual = modelToRegister.getIndividual(cp.getOntoUri() + objId);
        String hasGeohashValue = "";
        try {
            System.out.println("RDF Resource is: " + cp.getOntoUri() + objId);
            // double hasLatValue = Double.parseDouble(objectIndividual.getPropertyValue(modelToRegister.getProperty(cp.getOntoUri() + "hasLatitude")).asLiteral().getValue().toString());
            System.out.println("hasLatitude: " + hasLatValue);
            //double hasLonValue = Double.parseDouble(objectIndividual.getPropertyValue(modelToRegister.getProperty(cp.getOntoUri() + "hasLongitude")).asLiteral().getValue().toString());
            System.out.println("hasLongitude: " + hasLonValue);
            Geohash e = new Geohash();
            hasGeohashValue = e.encode(hasLatValue, hasLonValue);
            System.out.println("Geohash: " + hasGeohashValue);
            OntProperty pGeohash = modelToRegister.createOntProperty(cp.getOntoUri() + "hasGeohash");
            objectIndividual.addProperty(pGeohash, modelToRegister.createTypedLiteral(hasGeohashValue));
        } catch (Exception npe) {
            npe.printStackTrace();

            S2WRegistrationResponse rr = new S2WRegistrationResponse(objId, cp.getObjType(), stored, indexed, null, npe.getMessage());
            System.out.println("ERROR");
            return new  Gson().toJson(rr);
        }


        if (cp.getObjType().equalsIgnoreCase(EntityConfigParams.objType)) {
            System.out.println("creating association");
            //OntProperty pHasAttributeType = modelToRegister.getOntProperty(cp.getOntoUri() + "hasAttributeType");
            //String hasAttributeTypeVal = objectIndividual.getProperty(pHasAttributeType).getObject().asLiteral().getString();
            System.out.println("Feature: " + hasAttributeTypeVal);
            OntProperty pAssoc = modelToRegister.createOntProperty(cp.getOntoUri() + "isAssociatedToService");
            IotaAssociationGenerator iotaAssocGen = new IotaAssociationGenerator(cp);
            associatedService = iotaAssocGen.getAssociationWithService(hasGeohashValue, hasAttributeTypeVal);
            objectIndividual.addProperty(pAssoc, modelToRegister.createTypedLiteral(associatedService));
        }

        modelToRegister.write(System.out, "RDF/XML-ABBREV");

        /*----------------------------------------------------------------------*/
        //fold-in description
        String errorMessage = "OK";
        try {
            FoldInHandler foldInH = new FoldInHandler(cp);
            foldInH.foldInModel(modelToRegister);
            indexed = true;
        } catch (Exception exc) {
            //exc.printStackTrace();
            errorMessage = exc.getMessage();
            System.out.println("WARNING -> " + errorMessage);
            indexed = false;
        }

        sdbAH.getStore().close();

        S2WRegistrationResponse rr = new S2WRegistrationResponse(objId, cp.getObjType(), stored, indexed, associatedService, errorMessage);

        return new  Gson().toJson(rr);

    }
}
