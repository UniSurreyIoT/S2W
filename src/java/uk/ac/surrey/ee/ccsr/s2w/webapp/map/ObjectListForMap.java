/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webapp.map;

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
import javax.servlet.ServletContext;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.RestReqApplication;

/**
 *
 * @author te0003
 */
public class ObjectListForMap {
    
    public ServletContext context = OverlayMapsDemoServlet.context;
    public ConfigParameters cp;
    
    
    public  ObjectListForMap(ConfigParameters cParams){
        
        this.cp= cParams;
    
    }
    
     protected String getObjectList() {

        //ServletContext context = OverlayMapsDemoServlet.context;

        String queryString = "";

        try {
            queryString = org.apache.commons.io.FileUtils.readFileToString(new File(cp.getSparqSelectAll_template()));
        } catch (IOException e) {
        }
        
            SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
            Dataset ds = DatasetStore.create(sdbAH.getStore());

            System.out.println("sparql filepath:"+cp.getSparqSelectAll_template());
            System.out.println("sparql: \n"+queryString);

        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, ds);

        String xmlString = "";

        //extract element values from the query response
        try {
            ResultSet rs = qe.execSelect();
            for (; rs.hasNext();) {
                
                QuerySolution soln = rs.nextSolution();

                String uri = "";
                try {
                    RDFNode n = soln.getResource("uri");
                    uri = n.toString();//.asLiteral().getValue().toString();
                    System.out.println("uri: " + uri);
                } catch (NullPointerException e) {
                    System.out.println("uri is empty");
                    continue;
                }
                
                String hasName = "";
                try {
                    RDFNode n = soln.getLiteral("hasName");
                    hasName = n.asLiteral().getValue().toString();
                    System.out.println("hasName: " + hasName);
                } catch (NullPointerException e) {
                    System.out.println("hasName is empty");
                    continue;
                }
                
                String hasLatitude = "";                
                try {
                    RDFNode n = soln.getLiteral("hasLatitude");//.asLiteral().getValue().toString();
                    hasLatitude = n.asLiteral().getValue().toString();
                    System.out.println("hasLatitude: " + hasLatitude);
                } catch (NullPointerException e) {
                    System.out.println("hasLatitude is empty");
                    continue;
                }
                
                String hasLongitude = "";
                try {
                    RDFNode n = soln.getLiteral("hasLongitude");
                    hasLongitude = n.asLiteral().getValue().toString();
                    System.out.println("hasLongitude: " + hasLongitude);
                } catch (NullPointerException e) {
                    System.out.println("hasLongitude is empty");
                    continue;
                }
                
                String hasAltitude = "";
                try {
                    RDFNode n = soln.getLiteral("hasAltitude");
                    hasAltitude = n.asLiteral().getValue().toString();
                    System.out.println("hasAltitude: " + hasAltitude);
                } catch (NullPointerException e) {
                    System.out.println("hasAltitude is empty");
                    //continue;
                }
                
                String hasType = "";
                try {
                    RDFNode n = soln.getLiteral("hasType");
                    hasType = n.asLiteral().getValue().toString();
                    System.out.println("hasType: " + hasType);
                } catch (NullPointerException e) {
                    System.out.println("hasType is empty");
                    //continue;
                }
                
                String hasGlobalLocation = "";
                try {
                    RDFNode n = soln.getLiteral("hasGlobalLocation");
                    hasGlobalLocation = n.asLiteral().getValue().toString();
                    System.out.println("hasGlobalLocation: " + hasGlobalLocation);
                } catch (NullPointerException e) {
                    System.out.println("hasGlobalLocation is empty");
                    //continue;
                }
                
                String hasLocalLocation = "";
                try {
                    RDFNode n = soln.getLiteral("hasLocalLocation");
                    hasLocalLocation = n.asLiteral().getValue().toString();
                    System.out.println("hasLocalLocation: " + hasLocalLocation);
                } catch (NullPointerException e) {
                    System.out.println("hasLocalLocation is empty");
                    //continue;
                }
                
                String hasTag = "";
                try {
                    RDFNode n = soln.getLiteral("hasTag");
                    hasTag = n.asLiteral().getValue().toString();
                    System.out.println("hasTag: " + hasTag);
                } catch (NullPointerException e) {
                    System.out.println("hasTag is empty");
                    //continue;
                }
                
                String[] hasID = uri.split("#");
                String htmlText = "";

                try{
                 htmlText = setHTMLText(uri, hasType, hasGlobalLocation, hasLocalLocation, hasTag, hasID[1]);
                }
                catch(ArrayIndexOutOfBoundsException ae){
                    //in case no ID is found
                    ae.printStackTrace();
                    continue;
                }
                String markerSetNumber ="";
                if (cp.getObjType().equalsIgnoreCase(EntityConfigParams.objType))
                    markerSetNumber="2";

                xmlString = xmlString
                        + "<marker"+markerSetNumber
                        + " lat=\"" + hasLatitude + "\" "
                        + "lng=\"" + hasLongitude + "\" "
                        + "html=\"" + htmlText + "\" "
                        + "label=\"" + hasName + "\""
                        + "/> \n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.out.println("EXIT");
            qe.close();
            ds.close();
            sdbAH.getStore().close();
            System.out.println(xmlString);
            return xmlString;
        }
    }
     
     public String setHTMLText(String uri, String type, String dbpLocation, String localLocation, String tag, String id) {

        String br = "&lt;br&gt;";
        String imageText = "";

        String htmlText = "&lt;small&gt;" + "Object description URI: " + br + createHyperlinkString(uri, uri);
        htmlText = htmlText + br + "Object type: " + br + createHyperlinkString(type, type);
        htmlText = htmlText + br + "Location URI (local ontology): " + br + createHyperlinkString(localLocation, localLocation);
        htmlText = htmlText + br + "Linked-data location URI: " + br + createHyperlinkString(dbpLocation, dbpLocation);
        htmlText = htmlText + br + "Linked-data tag: " + br + createHyperlinkString(tag, tag);
//        htmlText = htmlText + br + br + lookUpSourceText(uri);
        //System.out.println("ID is: " + id);
        htmlText = htmlText + br + br + lookUpSourceText(id);
//        htmlText = htmlText + br + br + "Sensor status is:" + br + imageText + br + br + "&lt;/small&gt;";
        htmlText = htmlText + br + "&lt;/small&gt;";

        return htmlText;
    }
     
     public String createHyperlinkString(String txt, String link) {

        txt = "&lt;a href='" + link + "'&gt;" + txt + "&lt;/a&gt;";

        return txt;
    }
     
     public String lookUpSourceText(String objId) {

        String link = "";
        
        link = context.getContextPath() 
                    + RestReqApplication.restletPath 
                    + RestReqApplication.lookupPrefix 
                    + cp.getDescTypeLinkSuffix() 
                    + "/" + objId;        
       
        String lookUpText = " Get " + cp.getObjType() + " description (" + createHyperlinkString("here", link) + ")";

        return lookUpText;
    }

    
}
