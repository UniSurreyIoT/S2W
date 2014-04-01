/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota.query;

/**
 *
 * @author Payam
 */
import com.hp.hpl.jena.graph.GraphEvents;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.*;

import javax.xml.parsers.*;


import com.hp.hpl.jena.rdf.arp.DOM2Model;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.sql.SDBConnectionDesc;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import org.xml.sax.InputSource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.XMLDocumentHandler;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

public class SDBHandler {

    public void SDBStore(String dbUrl, String user, String password, String ttlFile, String xmlString, String ontoURI) throws FileNotFoundException, IOException {

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            doc = StringToDOM(xmlString);
            jenaSDBUpload(dbUrl, user, password, ontoURI, xmlString);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLDocumentHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void jenaSDBUpload(String dbUrl, String user, String password, String ontoURI, String xmlString) {

        //Store store = SDBFactory.connectStore(ttlFileName) ;
        //store.getTableFormatter().create();

        // Connect model to store

//        StoreDesc storeDesc = generateStoreDescSdb(dbUrl, user, password);
//        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
//        Store store = SDBFactory.connectStore(storeDesc);
        
        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);

        SDBConnection conn = new SDBConnection(dbUrl, user, password);
        Store store = SDBFactory.connectStore(conn, storeDesc);

        store.getLoader().setChunkSize(5000); //
        store.getLoader().setUseThreading(false); // Don't thread
        store.getLoader().startBulkUpdate();
        //Store store = SDBFactory.connectStore(ttlFile);
        Model model = SDBFactory.connectDefaultModel(store);
        //model.begin();
        InputStream in = StringtoInputStream(xmlString);
        model.read(in, ontoURI);
        //model.commit();
        //Model model = SDBFactory.co
        //debug*******************************
        //store.getTableFormatter().create() ;
        //************************************        
        model.notifyEvent(GraphEvents.startRead);
        try {
            // DOM2Model createD2M = DOM2Model.createD2M(ontoURI, model);
            // createD2M.allowRelativeURIs();
            // createD2M.load(doc);
            store.getLoader().finishBulkUpdate();
            store.getLoader().close();
            store.close();
        } catch (Exception e) {
            System.err.print(e.toString());
        } finally {
            model.notifyEvent(GraphEvents.finishRead);
        }
    }

    /*
     * deprecated
     */
    public void SDBUpload(String dbUrl, String user, String password, String ontoURI, Document doc) {

        /*
         * StoreDesc storeDesc = generateStoreDescSdb(dbUrl, user, password);
         * JDBC.loadDriverMySQL(); SDBConnection conn = new SDBConnection(dbUrl,
         * user, password) ; Store store = SDBFactory.connectStore(conn,
         * storeDesc) ;
         */

//        StoreDesc storeDesc = generateStoreDescSdb(dbUrl, user, password);
//        Store store = SDBFactory.connectStore(storeDesc);
        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);

        SDBConnection conn = new SDBConnection(dbUrl, user, password);
        Store store = SDBFactory.connectStore(conn, storeDesc);


        Model model = SDBFactory.connectDefaultModel(store);


        // Commit the database transaction

        model.notifyEvent(GraphEvents.startRead);
        try {

            DOM2Model createD2M = DOM2Model.createD2M(ontoURI, model);
            createD2M.load(doc);
            model.write(System.out);
            //model.

            //store.getLoader().setChunkSize(5000); //
            store.getLoader().setUseThreading(false); // Don't thread
            store.getLoader().startBulkUpdate();
            store.getLoader().finishBulkUpdate();

            store.getLoader().close();
            //conn.close();
            store.close();

        } catch (Exception e) {
            System.err.print(e.toString());
            //conn.close();
            store.close();
        } finally {
            model.notifyEvent(GraphEvents.finishRead);
        }

    }

    /**
     * Adapted:
     * http://www.google.com/codesearch/p?hl=en#xLGJSZSNoAY/trunk/uk.ac.osswatch.simal.core/src/main/java/uk/ac/osswatch/simal/model/jena/simal/JenaDatabaseSupport.java&q=Store%20store%20=%20SDBFactory.connectStore
     * Generates the Store description that contains all parameters needed to
     * connect to the store. Only used for SDB; result can be cached to
     * reconnect to the store.
     *
     * @param dbUrl
     * @return
     */
    public StoreDesc generateStoreDescSdb(String dbUrl, String user, String password) {

        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        SDBConnectionDesc sdbConnDesc = SDBConnectionDesc.blank();
        sdbConnDesc.setJdbcURL(dbUrl);
        sdbConnDesc.setUser(user);
        sdbConnDesc.setPassword(password);

        storeDesc.connDesc = sdbConnDesc;
        return storeDesc;
    }

    public Model createResourceModel() {
        Parameters param = new Parameters();
        StoreDesc storeDesc = generateStoreDescSdb(param.dbResourceUrl, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);
        Model model = SDBFactory.connectDefaultModel(store);

        return model;
    }

    public Model createServiceModel() {
        Parameters param = new Parameters();
        StoreDesc storeDesc = generateStoreDescSdb(param.dbServiceUrl, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);
        Model model = SDBFactory.connectDefaultModel(store);

        return model;
    }

    /**
     * Adapted from http://www.java.happycodings.com/XML/code17.html
     *
     * @param domString
     * @return
     */
    public Document StringToDOM(String domString) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(domString));
            Document doc = builder.parse(is);
            return doc;
        } catch (Exception ex) {
        }

        return null;
    }

    public InputStream StringtoInputStream(String str) {
        byte[] bytes = str.getBytes();
        return new ByteArrayInputStream(bytes);
    }
}
