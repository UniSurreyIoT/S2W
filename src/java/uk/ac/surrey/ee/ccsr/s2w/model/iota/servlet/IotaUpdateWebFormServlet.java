/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.servlet;

import com.google.gson.Gson;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import java.io.IOException;
import java.io.PrintWriter;
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
import uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.RestReqApplication;

/**
 *
 * @author te0003
 */
public class IotaUpdateWebFormServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        PrintWriter out = response.getWriter();
//        try {
//            /* TODO output your page here. You may use following sample code. */
//            out.println("<!DOCTYPE html>");
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet IotaUpdateWebFormServlet</title>");            
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<h1>Servlet IotaUpdateWebFormServlet at " + request.getContextPath() + "</h1>");
//            out.println("</body>");
//            out.println("</html>");
//        } finally {            
//            out.close();
//        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       // processRequest(request, response);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();

        String objId = request.getParameter("id");
        String descType = request.getParameter("desctype");
        //System.out.println(request.get);
        //out.println(parameter);

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

        SdbAccessHandler sdbAH = new SdbAccessHandler(cp);
        Model repo = sdbAH.loadAllModelInstancesFromSdb();
        JenaModelMgmt jmm = new JenaModelMgmt(cp, objId);
        Model mInstance = jmm.getRdfInstanceFromModel(repo);
        
        Map<String, String> webFormMap = jmm.getMappingFromInstance(mInstance);

        String jsonResult = new Gson().toJson(webFormMap);
        
        sdbAH.getStore().close();

        try {
            out.println(jsonResult);
        } finally {
            out.close();
        }

        
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
        
        WebFormToInstance foi = new WebFormToInstance(cp);
        OntModel mInstance = foi.createJenaModel(paramMap);

        SdbAccessHandler sdbHandler = new SdbAccessHandler(cp);
        Model mRepo = sdbHandler.loadAllModelInstancesFromSdb();
        
        JenaModelMgmt jmm = new JenaModelMgmt(cp, objId);
        
        jmm.updateRdfInstanceInSDB(mRepo, mInstance);
        
        String descriptionLink = context.getContextPath() 
                    + RestReqApplication.restletPath 
                    + RestReqApplication.lookupPrefix 
                    + cp.getDescTypeLinkSuffix() 
                    + "/" + objId;
            System.out.println("URL prefix: " + descriptionLink);
            WebFormResultLinks frl = new WebFormResultLinks(descriptionLink);
            String jsonResult = new Gson().toJson(frl.getFormatLinks());
            
            

            try {
                out.println(jsonResult);
            } finally {
                out.close();
                sdbHandler.getStore().close();
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
