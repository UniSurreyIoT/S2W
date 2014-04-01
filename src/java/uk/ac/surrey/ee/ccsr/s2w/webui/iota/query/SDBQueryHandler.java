package uk.ac.surrey.ee.ccsr.s2w.webui.iota.query;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Payam
 */

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import com.hp.hpl.jena.sdb.store.LayoutType;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;




public class SDBQueryHandler {

    public ResultSet runSDBQuery (String query) {
        Parameters param = new Parameters();

        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc = sdbHandler.generateStoreDescSdb(param.dbResourceUrl, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);

        ResultSet rs=null;
        Dataset ds = DatasetStore.create(store) ;
        QueryExecution qe = QueryExecutionFactory.create(query, ds) ;
        try {
            rs = qe.execSelect() ;
            //ResultSetFormatter.out(rs)
        } finally {
            qe.close();
            return rs;
        }
    }
}
