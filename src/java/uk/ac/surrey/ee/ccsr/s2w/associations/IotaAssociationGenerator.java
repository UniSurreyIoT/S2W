/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.associations;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import java.io.File;
import java.io.IOException;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;

/**
 *
 * @author te0003
 */
public class IotaAssociationGenerator {
    
    public ConfigParameters cp;
    
    public IotaAssociationGenerator(ConfigParameters cParams){
    
        this.cp=cParams;
    }
    
    public String getAssociationWithService(String entityGeoHVal, String entityFeatVal){
        
        String queryString = "";
    
        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File(cp.getSparqlAssoc_template()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //String entityGeoHVal = "gcped8eksk42"; //gcped8eksk42
       // String entityFeatVal = "http://purl.oclc.org/NET/ssnx/qu/quantity#temperature";
        int proximity = 7;  //room proximity ~ 9,  building ~ 8 
        
        entityGeoHVal=entityGeoHVal.substring(0, proximity);
                
        queryString = queryString.replaceAll("geohash", entityGeoHVal); 
        queryString = queryString.replaceAll("output", entityFeatVal);
        
        System.out.println(queryString);
        ConfigParameters cpService = new ServiceConfigParams();
        SdbAccessHandler sdbAH = new SdbAccessHandler(cpService);
        Dataset dataset = DatasetStore.create(sdbAH.getStore());

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, dataset);

        String uri = "";
        if (qe.getQuery().isSelectType()) {
            try {

                ResultSet rs = qe.execSelect();
                for ( ; rs.hasNext() ; )
    {
                QuerySolution soln = rs.nextSolution();
                
                try {
                    RDFNode n = soln.getResource("uri");
                    uri = n.toString();//.asLiteral().getValue().toString();
                     System.out.println();
                    System.out.println("uri: " + uri);
                } catch (NullPointerException e) {
                    System.out.println("uri is empty");                    
                }
    }
            } finally {
                qe.close();
                dataset.close();
                sdbAH.getStore().close();
            }           
        }
        
        return uri;        
        
    }
    
}
