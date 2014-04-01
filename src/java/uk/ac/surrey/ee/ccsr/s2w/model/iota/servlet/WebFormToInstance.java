/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.servlet;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.HashMap;
import java.util.Map;
import uk.ac.surrey.ee.ccsr.s2w.associations.IotaAssociationGenerator;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.spatial.Geohash;

/**
 *
 * @author te0003
 */
public class WebFormToInstance {

    //public String inputFilename = "/IoTA-Models/ResourceModel.owl";
    public String ONT_URL = "";  //"http://www.surrey.ac.uk/ccsr/ontologies/ResourceModel.owl";
    protected String ONT_FILE = "";// web/IoTA-Models/ResourceModel.owl";
    public static Map<String, String[]> hMap;// = new Map<String, String[]>();
    public ConfigParameters cp;

    static {
        hMap = new HashMap<String, String[]>();
    }

    public WebFormToInstance() {}

    public WebFormToInstance(ConfigParameters cParams) {

        this.ONT_URL = cParams.getOntoUri();
        this.ONT_FILE = cParams.getONTOLOGY_LOCAL_URL();
        this.cp=cParams;

    }

    public OntModel createJenaModel(Map<String, String[]> formParams) {

        OntModel ontInstance = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        Model resourceOnt = FileManager.get().loadModel(ONT_FILE);
        ontInstance.addSubModel(resourceOnt);

        OntClass classType = null;
        if (formParams.containsKey("class")) {
            classType = ontInstance.getOntClass(ONT_URL + formParams.get("class")[0]); //class should be hidden field in VE/service form
            System.out.println("Class type is: " + ONT_URL + formParams.get("class")[0]);
        } else {
            System.out.println("No class found");
            return null;
        }

        Individual objectIndividual = ontInstance.createIndividual(ONT_URL + formParams.get("hasID")[0], classType); //use "resourceID" as ID field for entity/service forms

        ExtendedIterator<OntProperty> iter = ontInstance.listAllOntProperties();
        while (iter.hasNext()) {
            OntProperty ontProp = iter.next();
            System.out.println(ontProp.getLocalName());
            if (formParams.containsKey(ontProp.getLocalName())) {
                objectIndividual.addProperty(ontProp, ontInstance.createTypedLiteral(formParams.get(ontProp.getLocalName())[0]));
            }
        }
        
        //get geohash and association
        
        double hasLatValue = Double.parseDouble(objectIndividual.getPropertyValue(ontInstance.getProperty(ONT_URL + "hasLatitude")).asLiteral().getValue().toString());
        System.out.println("hasLatitude: "+hasLatValue);
        double hasLonValue = Double.parseDouble(objectIndividual.getPropertyValue(ontInstance.getProperty(ONT_URL + "hasLongitude")).asLiteral().getValue().toString());
        System.out.println("hasLongitude: "+hasLonValue);
        Geohash e = new Geohash();
        String hasGeohashValue = e.encode(hasLatValue, hasLonValue);
        System.out.println("Geohash: "+hasGeohashValue);
        OntProperty pGeohash = ontInstance.createOntProperty(ONT_URL + "hasGeohash");
        objectIndividual.addProperty(pGeohash, ontInstance.createTypedLiteral(hasGeohashValue));
        
        if (cp.getObjType().equalsIgnoreCase(EntityConfigParams.objType)){
        
        OntProperty pHasAttributeType = ontInstance.getOntProperty(ONT_URL+"hasAttributeType");
        String hasAttributeTypeVal = objectIndividual.getProperty(pHasAttributeType).getObject().asLiteral().getString();
        System.out.println("Feature: "+hasAttributeTypeVal);
        OntProperty pAssoc = ontInstance.createOntProperty(ONT_URL + "isAssociatedToService");
        IotaAssociationGenerator iotaAssocGen = new IotaAssociationGenerator(cp);
        String associatedService = iotaAssocGen.getAssociationWithService(hasGeohashValue, hasAttributeTypeVal);        
        objectIndividual.addProperty(pAssoc, ontInstance.createTypedLiteral(associatedService));
        }

        ontInstance.write(System.out, "RDF/XML-ABBREV");

        return ontInstance;

    }


    public static void main(String[] args) {

        ConfigParameters cp = new EntityConfigParams();
        WebFormToInstance ri = new WebFormToInstance(cp);
        
        //hMap.put("class", "OnDeviceResource"); 
//        hMap.put("class", new String[]{"ResourceService"});
        hMap.put("class", new String[]{"VirtualEntity"});
//        hMap.put("hasID", new String[]{"Resource_1"});
        hMap.put("hasID", new String[]{"VirtualEntity_1"});
        hMap.put("hasAttributeType", new String[]{"http://purl.oclc.org/NET/ssnx/qu/quantity#temperature"});     
//        hMap.put("isHostedOn", "PloggBoard_49_BA_01_light");
//        hMap.put("hasType", "Sensor");
//        hMap.put("hasName", "lightsensor49_BA_01");
//        hMap.put("isExposedThroughService", "http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#49_BA_01_light_sensingService");
//        hMap.put("hasTag", "light sensor 49,1st,BA,office");
        hMap.put("hasLatitude", new String[]{"51.243455"});
//        hMap.put("hasGlobalLocation", "http://www.geonames.org/2647793/");
//        hMap.put("hasResourceID", "Resource_53_BA_power_sensor");
//        hMap.put("hasLocalLocation", "http://www.surrey.ac.uk/ccsr/ontologies/LocationModel.owl#U49");
        hMap.put("hasAltitude", new String[]{""});
        hMap.put("hasLongitude", new String[]{"-0.588088"});
//        hMap.put("hasTimeOffset", "20");

        //ri.ONT_URL = "http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#";
        //ri.ONT_URL = "http://www.surrey.ac.uk/ccsr/ontologies/VirtualEntityModel.owl#";
        //ri.ONT_FILE = "web/IoTA-Models/VirtualEntityModel.owl";
        ri.createJenaModel(hMap);

    }
    
    //    public void createJenaModel() {
//
//        OntModel ontInstance = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
//        Model resourceOnt = FileManager.get().loadModel(ONT_FILE);
//        ontInstance.addSubModel(resourceOnt);
//
//        OntClass classType = ontInstance.getOntClass(NS + "OnDeviceResource");
//
//        OntProperty hasName = ontInstance.getDatatypeProperty(NS + "hasName");
//        OntProperty hasResourceID = ontInstance.getDatatypeProperty(NS + "hasResourceID");
//        OntProperty hasTag = ontInstance.getDatatypeProperty(NS + "hasTag");
//        OntProperty isExposedThroughService = ontInstance.getDatatypeProperty(NS + "isExposedThroughService");
//        OntProperty hasLocalLocation = ontInstance.getDatatypeProperty(NS + "hasLocalLocation");
//        OntProperty hasGlobalLocation = ontInstance.getDatatypeProperty(NS + "hasGlobalLocation");
//        OntProperty hasLatitude = ontInstance.getDatatypeProperty(NS + "hasLatitude");
//        OntProperty hasLongitude = ontInstance.getDatatypeProperty(NS + "hasLongitude");
//        OntProperty hasAltitude = ontInstance.getDatatypeProperty(NS + "hasAltitude");
//        OntProperty hasTimeOffset = ontInstance.getDatatypeProperty(NS + "hasTimeOffset");
//        //OntProperty isHostedOn = ontInstance.getOntProperty(NS + "isHostedOn");
//        OntProperty isHostedOn = ontInstance.getObjectProperty(NS + "isHostedOn");
//        System.out.println(isHostedOn.getRange());
//
//        Individual objectInstance = ontInstance.createIndividual(NS + "Resource01", classType);
//
//        objectInstance.addProperty(isHostedOn, ontInstance.createTypedLiteral("PloggBoard_49_BA_01_light"));
//        objectInstance.addProperty(hasTimeOffset, ontInstance.createTypedLiteral("5"));
//        objectInstance.addProperty(hasAltitude, ontInstance.createTypedLiteral("41"));
//        objectInstance.addProperty(hasLongitude, ontInstance.createTypedLiteral("-0.57"));
//        objectInstance.addProperty(hasLatitude, ontInstance.createTypedLiteral("51.23"));
//        objectInstance.addProperty(hasGlobalLocation, ontInstance.createTypedLiteral("http://www.geonames.org/2647793/"));
//        objectInstance.addProperty(hasLocalLocation, ontInstance.createTypedLiteral("http://www.surrey.ac.uk/ccsr/ontologies/LocationModel.owl#U49"));
//        objectInstance.addProperty(isExposedThroughService, ontInstance.createTypedLiteral("http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#49_BA_01_light_sensingService"));
//        objectInstance.addProperty(hasTag, ontInstance.createTypedLiteral("power"));
//        objectInstance.addProperty(hasResourceID, ontInstance.createTypedLiteral("Resource_53_BA_power_sensor"));
//        objectInstance.addProperty(hasName, ontInstance.createTypedLiteral("lightsensor49_BA_01"));
//
//        ontInstance.write(System.out, "RDF/XML-ABBREV");
//        System.out.println(classType.getSuperClass().toString());
//        System.out.println(ontInstance.getNsPrefixMap());
//        //System.out.println(isHostedOn.set);
//
//
//    }
}
