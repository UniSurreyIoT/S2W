/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import com.hp.hpl.jena.sdb.store.LayoutType;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.Entity;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.EntityQuery;
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.Resource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.ResourceQuery;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.XMLDocumentHandler;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;

/**
 *
 * @author t-74
 */
public class DescriptionString2 {
    
    public static void main (String[] args){
    
        Parameters param = new Parameters();
        String xsltFilePath = "web/repo/ResourceOnto.xsl";
        DescriptionString2 ds = new DescriptionString2();
        ds.getDescriptionString("r1", "resource",xsltFilePath);
    }

    public DescriptionString2() {
    }

    public String getDescriptionString(String objectId, String repoSelect, String xsltFilePath) {

        //PrintWriter out = response.getWriter();
        Parameters param = new Parameters();
        String dbType = "";
        String qStatement = "";

        if (repoSelect.equalsIgnoreCase("Entity")) {
            dbType = param.dbEntityUrl;
            //           objNs = param.ontoEntityURI;
            Entity entity = new Entity(objectId);
            EntityQuery eq = new EntityQuery();
            qStatement = eq.constructQuery(entity);

        } else if (repoSelect.equalsIgnoreCase("Service")) {
            dbType = param.dbServiceUrl;
            //           objNs = param.ontoServiceURI;

        } else {
            dbType = param.dbResourceUrl;
//            objNs = param.ontoResourceURI;
            Resource resource = new Resource(objectId);
            ResourceQuery rq = new ResourceQuery();
            qStatement = rq.constructQuery(resource);
        }

        System.out.println("Query: " + qStatement);
        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);
        Dataset ds = DatasetStore.create(store);
        QueryExecution qe = QueryExecutionFactory.create(qStatement, ds);

        String resultString = "";

        try {
            ResultSet rs = qe.execSelect();

            if (repoSelect.equalsIgnoreCase("entity")) {
                resultString = entResultOut(rs, xsltFilePath);
            } else if (repoSelect.equalsIgnoreCase("resource")) {
                resultString = resResultOut(rs, xsltFilePath);
            } else {
                resultString = "model type not defined";
            }


        } finally {
            qe.close();
            ds.close();
            store.close();
        }
        try {
            resultString= new Utils().prettyPrintXml(resultString);
        } catch (TransformerException ex) {
            Logger.getLogger(DescriptionString2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultString;
    }
    
//    public static String prettyFormat(String input, int indent) {
//        try {
//            Source xmlInput = new StreamSource(new StringReader(input));
//            StringWriter stringWriter = new StringWriter();
//            StreamResult xmlOutput = new StreamResult(stringWriter);
//            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//            transformerFactory.setAttribute("indent-number", indent);
//            Transformer transformer = transformerFactory.newTransformer(); 
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            //transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "rdf");
//            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
//            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            transformer.transform(xmlInput, xmlOutput);
//            return xmlOutput.getWriter().toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e); // simple exception handling, please review it
//        }
//    }

//    public static String prettyFormat(String input) {
//        return prettyFormat(input, 2);
//    }

    private String resResultOut(ResultSet rs, String xsltFilePath) {

        Resource res = new Resource();
        ArrayList element = new ArrayList();
        element = res.getElementNames();

        ArrayList text = new ArrayList();
        //String fileValue = "";
        //Utils tool = new Utils();

        for (; rs.hasNext();) {

            QuerySolution soln = rs.nextSolution();

            String name = "";
            if (soln.get("name").toString() != null) {
                name = soln.get("name").toString();
                text.add(name);
            }
            String id = "";
            if (soln.get("id").toString() != null) {
                id = soln.get("id").toString();
                text.add(id);
            }
            String tag = "";
            if (soln.get("tag").toString() != null) {
                tag = soln.get("tag").toString();
                text.add(tag);
            }
            String tagList = "";
            if (soln.get("uriTag").toString() != null) {
                tagList = soln.get("uriTag").toString();
                text.add(tagList);
            }
            String timeZone = "";
            if (soln.get("timeZone").toString() != null) {
                timeZone = soln.get("timeZone").toString();
                text.add(timeZone);
            }
            String rLinkedToValue = "";
            if (soln.get("linkedTo").toString() != null) {
                rLinkedToValue = soln.get("linkedTo").toString();
                text.add(rLinkedToValue);
            }
            String localList = "";
            if (soln.get("localLoc").toString() != null) {
                localList = soln.get("localLoc").toString();
                text.add(localList);
            }
            String locationList = "";
            if (soln.get("globalLoc").toString() != null) {
                locationList = soln.get("globalLoc").toString();
                text.add(locationList);
            }
            String lat = "";
            if (soln.get("latitude").toString() != null) {
                lat = soln.get("latitude").toString();
                text.add(lat);
            }
            String lng = "";
            if (soln.get("longitude").toString() != null) {
                lng = soln.get("longitude").toString();
                text.add(lng);
            }
            String alt = "";
            if (soln.get("altitude").toString() != null) {
                alt = soln.get("altitude").toString();
                text.add(alt);
            }
            String type = "";
            if (soln.get("type").toString() != null) {
                type = soln.get("type").toString();
                text.add(type);
            }
            String rDescription = "";
            if (soln.get("resDescID").toString() != null) {
                rDescription = soln.get("resDescID").toString();
                text.add(rDescription);
            }
            String ifType = "";
            if (soln.get("ifType").toString() != null) {
                ifType = soln.get("ifType").toString();
                text.add(ifType);
            }
            String ifLink = "";
            if (soln.get("accessIf").toString() != null) {
                ifLink = soln.get("accessIf").toString();
                text.add(ifLink);
            }
        }

        Parameters param = new Parameters();

        Source xsltSource = new StreamSource(xsltFilePath);
        String xmlString = "";
        XMLDocumentHandler docHandler = new XMLDocumentHandler();
        try {
            xmlString = docHandler.createSimpleOntology(xsltSource, element, text, "resource");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DescriptionString2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(DescriptionString2.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("xmlString:\n " + xmlString);

        return xmlString;

    }

    private String entResultOut(ResultSet rs, String xsltFilePath) {

        Entity ent = new Entity();
        ArrayList element = new ArrayList();
        element = ent.getElementNames();
        ArrayList text = new ArrayList();
        
        for (; rs.hasNext();) {
            QuerySolution soln = rs.nextSolution();
            String name = "";
            if (soln.get("name").toString() != null) {
                name = soln.get("name").toString();
                text.add(name);
            }
            String id = "";
            if (soln.get("id").toString() != null) {
                id = soln.get("id").toString();
                text.add(id);
            }
            String tag = "";
            if (soln.get("tag").toString() != null) {
                tag = soln.get("tag").toString();
                text.add(tag);
            }
            String tagList = "";
            if (soln.get("uriTag").toString() != null) {
                tagList = soln.get("uriTag").toString();
                text.add(tagList);
            }
            String timeZone = "";
            if (soln.get("timeZone").toString() != null) {
                timeZone = soln.get("timeZone").toString();
                text.add(timeZone);
            }
            String rLinkedToValue = "";
//            if (soln.get("linkedTo").toString() != null) {
//                rLinkedToValue = soln.get("linkedTo").toString();
              text.add(rLinkedToValue);
//            }
            String localList = "";
            if (soln.get("localLoc").toString() != null) {
                localList = soln.get("localLoc").toString();
                text.add(localList);
            }
            String locationList = "";
            if (soln.get("globalLoc").toString() != null) {
                locationList = soln.get("globalLoc").toString();
                text.add(locationList);
            }
            String lat = "";
            if (soln.get("latitude").toString() != null) {
                lat = soln.get("latitude").toString();
                text.add(lat);
            }
            String lng = "";
            if (soln.get("longitude").toString() != null) {
                lng = soln.get("longitude").toString();
                text.add(lng);
            }
            String alt = "";
//            if (soln.get("altitude").toString() != null) {
//                alt = soln.get("altitude").toString();
                text.add(alt);
//            }
            String eDescription = "";
//            if (soln.get("entDesc").toString() != null) {
//                eDescription = soln.get("entDesc").toString();
                text.add(eDescription);
//            }
        }
        Parameters param = new Parameters();
        Source xsltSource = new StreamSource(xsltFilePath);
        String xmlString = "";
        XMLDocumentHandler docHandler = new XMLDocumentHandler();
        try {
            xmlString = docHandler.createSimpleOntology(xsltSource, element, text, "entity");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(DescriptionString2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(DescriptionString2.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("xmlString=:" + xmlString);

        return xmlString;

    }
}
