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
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author Payam
 */
public class UnpublishAllObjectsServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Parameters param = new Parameters();

        String repoSelect = request.getParameter("reposelect");
        String dbType = "";
        if (repoSelect.equalsIgnoreCase(EntityConfigParams.objType)) {
            dbType = param.dbEntityUrl;
        } else if (repoSelect.equalsIgnoreCase(ServiceConfigParams.objType)) {
            dbType = param.dbServiceUrl;
        } else {
            dbType = param.dbResourceUrl;
        }

        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc;
        storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);

        Model model = SDBFactory.connectDefaultModel(store);
        model.removeAll();

        out.println("<div>");
        out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
        out.println("<hr>");
        out.println("<br>");

        out.println("<div>");
        out.println("All Objects unpublished in this repository!<br>");
        out.println("<br>");
        out.println("Click <a href=\"/S2W/publish/Resource.html\">here</a> to publish a resource;<br>");
        out.println("Click <a href=\"/S2W/publish/Entity.html\">here</a> to publish an entity;<br>");
        out.println("Click <a href=\"/S2W/query/QueryEndpoint.html\">here</a> to make a query<br>");
        out.println("Click <a href=\"/S2W/map/MapOverlay.html\">here</a> to locate an object on a map;<br>");
        out.println("Click <a href=\"/S2W\">here</a> to go back to the home page.<br>");
        out.println("</div>");

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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(PublishObjectFormServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(PublishObjectFormServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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
