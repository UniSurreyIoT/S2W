package uk.ac.surrey.ee.ccsr.s2w.jena;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.util.FileManager;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.ObjectDataStructure;

public class JenaModelMgmt {

    /**
     * @param args
     */
    static String objectUri = "";
    public Store store;
    public ConfigParameters cp;

    //public CRUDRdfModel() {}
    public JenaModelMgmt(ConfigParameters cParams) {

        this.cp = cParams;
    }

    public JenaModelMgmt(ConfigParameters cParams, String objUri) { // for instances

        this.cp = cParams;
        objectUri = cp.getOntoUri() + objUri;
    }

    public static String getObjectUri() {
        return objectUri;
    }

    public static void setObjectUri(String objectUri) {
        JenaModelMgmt.objectUri = objectUri;
    }

    public Model loadModelFromFile(String inputFilename) {
        // TODO Auto-generated method stub
        System.out.println("load from file: " + inputFilename);
        Model m = FileManager.get().loadModel(inputFilename);

        return m;
    }

    // READ
    public Model getRdfInstanceFromModel(Model m) {

        System.out.println("Getting instance: " + objectUri);
        OntModel mInstance = ModelFactory.createOntologyModel();
        mInstance.setNsPrefixes(m.getNsPrefixMap());
        Resource res = m.getResource(objectUri);
        System.out.println(res.getLocalName());
        StmtIterator iter = res.listProperties();
        mInstance.add(iter);

        return mInstance;

    }

    public Map<String, String> getMappingFromInstance(Model m) {
        // TODO Auto-generated method stub	

        Resource res = m.getResource(objectUri);
        //System.out.println("ID:");

        Map<String, String> mPropertiesMap = new HashMap<>();

        String descID = res.getLocalName();
        //System.out.println(descID);
        mPropertiesMap.put("hasID", descID);
        StmtIterator iter = res.listProperties();

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            // get predicate
            String triplePredicate = stmt.getPredicate().getLocalName()
                    .toString();
            //System.out.print(triplePredicate + "\t");
            // get object
            String tripleObject = "";
            if (stmt.getObject().isLiteral()) { // for formatting
                tripleObject = stmt.getObject().asLiteral().getValue().toString();
                mPropertiesMap.put(triplePredicate, tripleObject);
                //System.out.print(tripleObject + "\t");
            } else {
                tripleObject = stmt.getObject().toString();
                mPropertiesMap.put(triplePredicate, tripleObject);
            }
            //System.out.println();
        }
        //System.out.println();        
        return mPropertiesMap;
    }

    public String getRdfInstanceInFormat(Model m, String format) {
        // TODO Auto-generated method stub
        System.out.println("Getting " + format + " for instance: " + objectUri);
        OntModel mInstance = ModelFactory.createOntologyModel();
        mInstance.setNsPrefixes(m.getNsPrefixMap());

        Resource res = m.getResource(objectUri);
        System.out.println(res.getLocalName());
        StmtIterator iter = res.listProperties();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            String triplePredicate = stmt.getPredicate().getLocalName()
                    .toString();
            if (triplePredicate.equalsIgnoreCase("hasGeohash")) {
                continue;
            }
            mInstance.add(stmt);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mInstance.write(bos, format);
        String resultString = bos.toString();
        System.out.println(resultString);

        return resultString;
    }

    public String getRdfInstancesInFormat(Model m, ArrayList<String> descriptionIDs, String format) {
        // TODO Auto-generated method stub

        OntModel model2 = ModelFactory.createOntologyModel();
        model2.setNsPrefixes(m.getNsPrefixMap());

        for (int i = 0; i < descriptionIDs.size(); i++) {
            String descriptionID = cp.getOntoUri() + descriptionIDs.get(i);
            System.out.println("Getting " + format + " for instance: " + descriptionID);
            Resource res = m.getResource(descriptionID);
            System.out.println(res.getLocalName());
            StmtIterator iter = res.listProperties();
            model2.add(iter);

        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();


        model2.write(bos, format);
        String xmlString = bos.toString();
        System.out.println(xmlString);

        return xmlString;
    }

    public String getRdfModelInFormat(Model m, String format) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        m.write(bos, format);
        String xmlString = bos.toString();
        System.out.println(xmlString);

        return xmlString;
    }

    public ObjectDataStructure getConceptsFromInstance(Model m, Resource res) {
        // TODO Auto-generated method stub		

        if (res == null) {
            res = m.getResource(objectUri);
            System.out.println("ID:" + objectUri);
        }

        String descID = res.getLocalName();
        //System.out.println(descID);
        ObjectDataStructure objDataStruct = new ObjectDataStructure(descID);

        StmtIterator iter = res.listProperties();

//        System.out.println("CONCEPTS:");
        ArrayList<String> concepts = new ArrayList<String>();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            // get predicate
            String triplePredicate = stmt.getPredicate().getLocalName()
                    .toString();
//            System.out.print(triplePredicate + "\t");
            // get object
            String tripleObject = "";
            if (stmt.getObject().isLiteral()) { // for formatting
                tripleObject = stmt.getObject().asLiteral().getValue()
                        .toString();
                concepts.add(triplePredicate + "_" + tripleObject);
//                System.out.print(triplePredicate + "_" + tripleObject + "\t");
                concepts.add(tripleObject);
//                System.out.print(tripleObject + "\t");
            } else {
                tripleObject = stmt.getObject().toString();
                concepts.add(triplePredicate + "_" + tripleObject);
//                System.out.print(triplePredicate + "_" + tripleObject + "\t");
                concepts.add(tripleObject);
//                System.out.print(tripleObject + "\t");
            }

//            System.out.println();
        }
//        System.out.println();
        objDataStruct.setAllConcepts(concepts);
        return objDataStruct;

    }

    public ArrayList<ObjectDataStructure> getConceptsFromModel(Model m) {
        // TODO Auto-generated method stub

        String objTypeUniqueProperty = cp.getObjTypeUniqueProperty();

        Property idPrp = m.getProperty(objTypeUniqueProperty);

        ResIterator rIter = m.listSubjectsWithProperty(idPrp); // Get all Resources with this unique property

        ArrayList<ObjectDataStructure> objDataStructs = new ArrayList<>();
        //int i = 0;
        while (rIter.hasNext()) {

            //System.out.println("Instance no.: " + ++i);
            //System.out.println("ID:");
            Resource res = rIter.nextResource(); // iterate through resources

            ObjectDataStructure objDataStruct = getConceptsFromInstance(m, res);

            objDataStructs.add(objDataStruct);
        }
        return objDataStructs;

    }

    // UPDATE
    public boolean updateRdfInstanceInSDB(Model mainModel, StringReader instanceString) {
        // TODO Auto-generated method stub

        SdbAccessHandler sdbHandler = new SdbAccessHandler(cp);

        if (deleteRdfInstance(mainModel)) {
            if (sdbHandler.storeRDFtoSDB(instanceString)) {
                sdbHandler.getStore().close();
                return true;
            }
        }
        //getRdfInstanceInFormat(mainModel, null);
        sdbHandler.getStore().close();
        return false;

    }

    public boolean updateRdfInstanceInSDB(Model mainModel, Model instanceModel) {
        // TODO Auto-generated method stub

        SdbAccessHandler sdbHandler = new SdbAccessHandler(cp);

        if (deleteRdfInstance(mainModel)) {
            if (sdbHandler.storeModeltoSDB(instanceModel)) {
                sdbHandler.getStore().close();
                return true;
            }
        }
        //getRdfInstanceInFormat(mainModel, null);
        sdbHandler.getStore().close();
        return false;

    }

    public void updateRdfInstanceInFile(Model mainModel, Model instance) {
        // TODO Auto-generated method stub
        deleteRdfInstance(mainModel);
        mainModel.add(instance);
        //getRdfInstanceInFormat(mainModel, null);
    }

    // DELETE
    public boolean deleteRdfInstance(Model m) {
        // TODO Auto-generated method stub
        System.out.println("check if available");
        getRdfInstanceInFormat(m, null);
        Resource res = m.getResource(objectUri);
        if (m.contains(res, null) == true) {
            res.removeProperties();
            getRdfInstanceInFormat(m, null);
            System.out.println("Properties deleted for instance: " + objectUri);
            return true;
        } else {
            System.out.println("Properties not found for instance: " + objectUri);
        }
        return false;
    }

    public static void main(String[] args) {

        ConfigParameters cp = new ResourceConfigParams();
        String dbUrl = cp.getMysqlDbUrl(); // dbUrl could be different
        // resources if distributed
        String namespace = cp.onto_URI;
        String objectId = "Resource_49_BA_01_light_sensor";
        String objectURI = namespace + objectId;
        String inputFILENAME = "owl-samples/resources_200.owl";
        // String updateInstanceFILENAME =
        // "ontologies/resources_adhocj_200--1_instance.owl";

        // CRUDRdfModel cff = new CRUDRdfModel(dbUrl); // RESOURCE DB
        JenaModelMgmt cff = new JenaModelMgmt(cp, objectURI); // RESOURCE DB
        ConfigParameters cParams = new ResourceConfigParams();
        cParams.password_mysql = "root";
        cParams.username_mysql = "root";

        SdbAccessHandler sdbAH = new SdbAccessHandler(cParams);
        Model mainModel = sdbAH.loadAllModelInstancesFromSdb();

        //Model mainModel = cff.loadModelFromFile(inputFILENAME);
        // Model instance = cff.loadModelFromFile(updateInstanceFILENAME);
        // InputStream instanceIStream=null;
        // try {
        // instanceIStream = new FileInputStream(updateInstanceFILENAME);
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // cff.getRdfInstanceInXml(mainModel);
//		ArrayList<String> descriptionIDs = new ArrayList<>();
//		descriptionIDs.add(objectUri);
//		descriptionIDs.add(namespace + "Resource_36_BA_01_power_sensor");
//		cff.getRdfInstancesInFormat(mainModel, descriptionIDs, null);
        String result = cff.getRdfInstanceInFormat(mainModel, "RDF/JSON");
        System.out.println(result);
        // cff.getConceptsFromInstance(mainModel, null);
        // ArrayList<ObjectDataStructure> obs = cff.getConceptsFromModel(
        // mainModel, namespace); // implemented but need data structure
        // for
        // cff.updateRdfInstanceInSDB(mainModel, namespace, instanceIStream);
        // cff.updateRdfInstanceInFile(mainModel, instance);
        // cff.deleteRdfInstance();

    }
}
