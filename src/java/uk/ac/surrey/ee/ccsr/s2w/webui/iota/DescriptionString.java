/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.store.LayoutType;
import java.io.ByteArrayOutputStream;
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author t-74
 */
public class DescriptionString {
    
    public static void main (String[] args){
    
        DescriptionString ds = new DescriptionString();
        ds.getDescriptionString("r1", "resource", "RDF/XML-ABBREV");
    }

    public DescriptionString() {
    }

    public String getDescriptionString(String objectId, String repoSelect, String format) {

        Parameters param = new Parameters();
        String dbType = "";
        String objNs = "";
        String modelType = "";

        if (repoSelect.equalsIgnoreCase("Entity")) {
            dbType = param.dbEntityUrl;
            objNs = param.ontoEntityURI;
            modelType = "EntityModel";

        } else if (repoSelect.equalsIgnoreCase("Service")) {
            dbType = param.dbServiceUrl;
            objNs = param.ontoServiceURI;
            modelType = "ServiceModel";
        } else {
            dbType = param.dbResourceUrl;
            objNs = param.ontoResourceURI;
            modelType = "ResouceModel";
        }

        System.out.println(objNs);

        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc;
        storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);

        Model model = SDBFactory.connectDefaultModel(store);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        model.write(bos, format);
        String xmlString = bos.toString();
        System.out.println("\n"+xmlString+"\n");
        String descPrefix = "<rdf:Description rdf:about=\"" + objNs;
        String endPrefix = "\">";
        
        String startTag = descPrefix + objectId + endPrefix;
        String endTag = "</rdf:Description>";
        String descCut = "";

        System.out.println(startTag+"\n");

        int descIndex = xmlString.indexOf(startTag);

        try {
            descCut = xmlString.substring(descIndex);
        } catch (Exception e) {
            return "no object found with this ID";
        }

        String[] descSplit = descCut.split(endTag);
        String description = descSplit[0];
        description = description + endTag;
        //description = description.replaceAll("j.0", modelType);
        
        System.out.println("result is:");
        System.out.println(description);
        System.out.println("done");

        return description;
    }
}
