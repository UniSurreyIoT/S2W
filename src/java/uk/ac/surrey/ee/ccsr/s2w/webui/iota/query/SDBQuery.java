/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota.query;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import com.hp.hpl.jena.sdb.store.LayoutType;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author Payam
 */
public class SDBQuery extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Parameters param = new Parameters();

        String queryBody = request.getParameter("query");
        String format = request.getParameter("format");
        String repoSelect = request.getParameter("reposelect");
        String dbType = "";
        String queryPrefix="Prefix om:<http://www.surrey.ac.uk/ccsr/IoT-A/";

        if (format.equalsIgnoreCase("xml")) {
            response.setContentType("text/xml;charset=UTF-8");
        } else {
            response.setContentType("text/plain;charset=UTF-8");
        }

        if (repoSelect.equalsIgnoreCase("entity")) {
            dbType = param.dbEntityUrl;
            queryPrefix +="Entity";
        } else if (repoSelect.equalsIgnoreCase("service")) {
            dbType = param.dbServiceUrl;
            queryPrefix +="Service";
        } else {
            dbType = param.dbResourceUrl;
            queryPrefix +="Resource";
        }

        queryPrefix +="Model.owl#>";

        String query = queryPrefix+queryBody;

        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc;

        storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);

        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);


        Dataset ds = DatasetStore.create(store);
        QueryExecution qe = QueryExecutionFactory.create(query, ds);
        try {
            ResultSet rs = qe.execSelect();
            //ResultSetFormatter.out(rs) ;
            if (format.equalsIgnoreCase("xml")) {
                out.println(ResultSetFormatter.asXMLString(rs));
            } else {
                out.println(ResultSetFormatter.asText(rs));
            }
        } finally {
            qe.close();
            ds.close();
            store.close();
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
