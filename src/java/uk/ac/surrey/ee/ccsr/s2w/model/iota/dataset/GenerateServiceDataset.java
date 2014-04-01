/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.dataset;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.spatial.Geohash;

/**
 *
 * @author te0003
 */
public class GenerateServiceDataset {

    public GenerateServiceDataset() {
    }
    
    public String ONT_URL = "http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#";
    public String SOURCE_FILE = "web/IoTA-Models/ServiceModel.owl";
    public String DATASET_FILE = "web/dataset/iot-a/service/dataset.txt";
    
    public static void main(String[] args) throws FileNotFoundException, IOException{
    
        GenerateServiceDataset gsd = new GenerateServiceDataset();
        ConfigParameters cp = new ServiceConfigParams();
        
        OntModel ontInstances = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        Model resourceOnt = FileManager.get().loadModel(gsd.SOURCE_FILE);
        ontInstances.addSubModel(resourceOnt);
        
        OntClass classType = ontInstances.getOntClass(gsd.ONT_URL + "Service");
        
        FileReader file = new FileReader(new File(gsd.DATASET_FILE));
        BufferedReader reader = new BufferedReader(file);
        reader.readLine();
        for (String line; (line = reader.readLine()) != null;) {
            
            String[] values = line.split("\t");
            String id = values[0];
            String name = values[1];
            double lat = Double.parseDouble(values[2]);
            double lon = Double.parseDouble(values[3]);
            String output = values[4];
            
            Geohash e = new Geohash();
        String hasGeohashValue = e.encode(lat, lon);
        System.out.println("Geohash: "+hasGeohashValue);        
            
            Individual r = classType.createIndividual(gsd.ONT_URL + id);
            OntProperty ontProp= ontInstances.getOntProperty(gsd.ONT_URL +"hasName");
            r.addProperty(ontProp, ontInstances.createTypedLiteral(name));
            ontProp = ontInstances.getOntProperty(gsd.ONT_URL +"hasLatitude");
            r.addProperty(ontProp, ontInstances.createTypedLiteral(lat));
            ontProp = ontInstances.getOntProperty(gsd.ONT_URL +"hasLongitude");
            r.addProperty(ontProp, ontInstances.createTypedLiteral(lon));
            ontProp = ontInstances.getOntProperty(gsd.ONT_URL +"hasOutput");
            r.addProperty(ontProp, ontInstances.createTypedLiteral(output));
            ontProp = ontInstances.createOntProperty(gsd.ONT_URL + "hasGeohash");
        r.addProperty(ontProp, ontInstances.createTypedLiteral(hasGeohashValue));
            
        }
        file.close();
        
        ontInstances.write(System.out, "RDF/XML-ABBREV");
        
        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
        Model mRepo = sdbAH.loadAllModelInstancesFromSdb();
        mRepo.removeAll();
        sdbAH.storeModeltoSDB(ontInstances);
        System.out.println("load from SDB: ");
        System.out.println(sdbAH.loadAllModelInstancesFromSdb().write(System.out, "RDF/XML-ABBREV"));
        
    }
    
    
    
}
