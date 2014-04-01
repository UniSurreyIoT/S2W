/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author t-74
 */
public class ObjectDescriptionServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String objectId = request.getParameter("objectID");
        String repoSelect = request.getParameter("reposelect");

        Parameters param = new Parameters();
        
        ServletContext context = getServletConfig().getServletContext();
        String xsltFilePath = "";

        if (repoSelect.equalsIgnoreCase(EntityConfigParams.objType)) {
            xsltFilePath = context.getRealPath(param.xsltEntityFile);
        } else if (repoSelect.equalsIgnoreCase(ResourceConfigParams.objType)) {
            xsltFilePath = context.getRealPath(param.xsltResourceFile);
        } else if (repoSelect.equalsIgnoreCase(ServiceConfigParams.objType)) {
            xsltFilePath = context.getRealPath(param.xsltServiceFile);
        }else{
            out.println(param.invalidRepoMsg);
            return;
        }

        //DescriptionString ds = new DescriptionString();
        DescriptionString2 ds = new DescriptionString2();
        String description = ds.getDescriptionString(objectId, repoSelect, xsltFilePath);
        //String description = ds.getDescriptionString(objectId, repoSelect);
        out.println(description);

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
