/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.servlet;

import com.google.gson.Gson;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.jena.JenaModelMgmt;
import uk.ac.surrey.ee.ccsr.s2w.jena.sdb.SdbAccessHandler;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.NonLogicMatchmaking;
import uk.ac.surrey.ee.ccsr.s2w.probengine.core.ObjectDataStructure;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.RestReqApplication;

/**
 *
 * @author te0003
 */
public class IotaDiscoverWebFormServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet IotaDiscoverWebFormServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet IotaDiscoverWebFormServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
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
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();

        Map<String, String[]> paramMap = request.getParameterMap();
        String descType = paramMap.get("desctype")[0];
        String objId = paramMap.get("hasID")[0];
        ConfigParameters cp;

        switch (descType) {
            case ResourceConfigParams.objType:
                cp = new ResourceConfigParams(context);
                break;
            case EntityConfigParams.objType:
                cp = new EntityConfigParams(context);
                break;
            case ServiceConfigParams.objType:
                cp = new ServiceConfigParams(context);
                break;
            default:
                throw new IllegalArgumentException("Invalid description type: " + descType);
        }

        Path path = Paths.get(cp.getIndex());
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {

            WebFormToInstance foi = new WebFormToInstance(cp);
            Model mQuery = ModelFactory.createDefaultModel();
            mQuery.add(foi.createJenaModel(paramMap));

            JenaModelMgmt crmInstance = new JenaModelMgmt(cp, objId);
            ObjectDataStructure obsForDiscovery = crmInstance.getConceptsFromInstance(mQuery, null);
            System.out.println("concepts are: " + obsForDiscovery.getAllConcepts().toString());

            NonLogicMatchmaking nlmQ = new NonLogicMatchmaking(cp);
            ArrayList<String> descriptionIDs = nlmQ.matchToQuery(obsForDiscovery); //only one allowed in query

            ArrayList<Map> resultLinks = new ArrayList<>();

            String jsonResult = "";
            for (int i = 0; i < descriptionIDs.size(); i++) {
                String descriptionLink = context.getContextPath()
                        + RestReqApplication.restletPath
                        + RestReqApplication.lookupPrefix
                        + cp.getDescTypeLinkSuffix()
                        + "/" + descriptionIDs.get(i);
                WebFormResultLinks frl = new WebFormResultLinks(descriptionLink);
                resultLinks.add(frl.getFormatLinks());
            }

            jsonResult = new Gson().toJson(resultLinks);
            System.out.println(jsonResult);
            out.println(jsonResult);
            out.close();
        } else {

            try {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Search Index for " + cp.getObjType().toUpperCase() + " has not yet been created");
            } finally {
                out.close();
            }

        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
