/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.testing;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.servlet.WebFormToInstance;

/**
 *
 * @author te0003
 */
public class TestSparqlMod {
    
    public String SPARQL_FILE = "web/sparql-templates/iot-a/entity/sparql-select-assoc.txt";
    
    public TestSparqlMod(){
    
    }
    
    public static void main(String[] args){
        
        String queryString = "";

        ConfigParameters cParams = new ServiceConfigParams();
        cParams.password_mysql = "root";
        cParams.username_mysql = "root";
        
        TestSparqlMod tsm = new TestSparqlMod();

        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File(tsm.SPARQL_FILE));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        String entityGeoHVal = "gcped8eksk42"; //gcped8eksk42
        String entityFeatVal = "http://purl.oclc.org/NET/ssnx/qu/quantity#temperature";
        int proximity = 9;  //room proximity ~ 9,  building ~ 8 
        
        entityGeoHVal=entityGeoHVal.substring(0, proximity);
                
        queryString = queryString.replaceAll("geohash", entityGeoHVal); 
        queryString = queryString.replaceAll("output", entityFeatVal);
        
        System.out.println(queryString);
        
        SdbAccessHandler sdbAH = new SdbAccessHandler(cParams);
        Dataset dataset = DatasetStore.create(sdbAH.getStore());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, dataset);
        
        String format = "";

        //String queryResult = "";

        if (qe.getQuery().isSelectType()) {
            try {

                ResultSet rs = qe.execSelect();
                
                QuerySolution soln = rs.nextSolution();

                String uri = "";
                try {
                    RDFNode n = soln.getResource("uri");
                    uri = n.toString();//.asLiteral().getValue().toString();
                     System.out.println();
                    System.out.println("uri: " + uri);
                } catch (NullPointerException e) {
                    System.out.println("uri is empty");                    
                }
                
            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }           
        }
        
        
        
    }
    
    
}
