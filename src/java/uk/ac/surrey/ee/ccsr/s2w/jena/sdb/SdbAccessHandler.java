package uk.ac.surrey.ee.ccsr.s2w.jena.sdb;

import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnectionDesc;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;
import java.io.InputStream;
import java.io.StringReader;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;

public class SdbAccessHandler {

    /**
     * @param args
     */
    ConfigParameters cp;
    public Store store;

    public Store getStore() {
        return store;
    }

    public SdbAccessHandler(ConfigParameters cParams) {

        this.cp = cParams;
        
        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash, DatabaseType.MySQL);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);

        String jdbcURL = cp.getMysqlDbUrl();			//"jdbc:mysql://localhost:3306/resourcedb"; //TODO CONFIG PARAMETER
        String mySqlUsername = cp.getUsername_mysql();	//TODO CONFIG PARAMETER
        String mySqlpassword = cp.getPassword_mysql();	//TODO CONFIG PARAMETER

        //CONNECT THIS WAY
        SDBConnectionDesc sdbConnDesc = SDBConnectionDesc.blank();
        sdbConnDesc.setJdbcURL(jdbcURL);
        sdbConnDesc.setUser(mySqlUsername);
        sdbConnDesc.setPassword(mySqlpassword);
        storeDesc.connDesc = sdbConnDesc;
        this.store = SDBFactory.connectStore(storeDesc); 
    }

    public synchronized Model loadAllModelInstancesFromSdb() {
        // TODO Auto-generated method stub
        System.out.println("load from SDB using: " + cp.getMysqlDbUrl());
        //Store store = getSdbStore();
        Model m = SDBFactory.connectDefaultModel(store);

        return m;
    }
    
   

    public synchronized void storeRDFtoSDB(InputStream isRegister) {
        // TODO Auto-generated method stub

        store.getLoader().setChunkSize(5000); //
        store.getLoader().setUseThreading(false); // Don't thread
        store.getLoader().startBulkUpdate();

        Model model = SDBFactory.connectDefaultModel(store);
        model.read(isRegister, cp.getOntoUri());
        model.notifyEvent(GraphEvents.startRead);
        try {
            store.getLoader().finishBulkUpdate();
            store.getLoader().close();
            store.close();
        } catch (Exception e) {
            System.err.print(e.toString());
        } finally {
            model.notifyEvent(GraphEvents.finishRead);
        }


    }

    public synchronized boolean storeRDFtoSDB(StringReader strRegister) {
        // TODO Auto-generated method stub

        store.getLoader().setChunkSize(5000); //
        store.getLoader().setUseThreading(false); // Don't thread
        store.getLoader().startBulkUpdate();

        Model model = SDBFactory.connectDefaultModel(store);
        model.read(strRegister, cp.getOntoUri());
        model.notifyEvent(GraphEvents.startRead);
        try {
            store.getLoader().finishBulkUpdate();
            store.getLoader().close();
            store.close();
            return true;
        } catch (Exception e) {
            System.err.print(e.toString());
            return false;
        } finally {
            model.notifyEvent(GraphEvents.finishRead);
            model.close();
            store.close(); //check!
        }

    }

    public synchronized boolean storeModeltoSDB(Model modelToRegister) {
        // TODO Auto-generated method stub

        store.getLoader().setChunkSize(5000); //
        store.getLoader().setUseThreading(false); // Don't thread
        store.getLoader().startBulkUpdate();

        Model model = SDBFactory.connectDefaultModel(store);
        model.add(modelToRegister);
        model.notifyEvent(GraphEvents.startRead);
        try {
            store.getLoader().finishBulkUpdate();
            store.getLoader().close();
            store.close();
            return true;
        } catch (Exception e) {
            System.err.print(e.toString());
            return false;
        } finally {
            model.notifyEvent(GraphEvents.finishRead);

        }


    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}
