/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author Payam
 */
public class PublishObjectFileServlet extends HttpServlet {

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
        ServletContext context = getServletConfig().getServletContext();
        Parameters param = new Parameters();
        param.ttlFile = context.getRealPath(param.ttlFile);

        //String descType = request.getParameter("desctype");
        String dbType = "";
        String ontoUri = "";

        MultipartParser mp = new MultipartParser(request, 1 * 1024 * 50); // 500 KB
        Part part;

        part = mp.readNextPart();
        ParamPart paramName1 = (ParamPart) part;
        String descType = paramName1.getStringValue();

        if (descType.equalsIgnoreCase(EntityConfigParams.objType)) {
            dbType = param.dbEntityUrl;
            ontoUri = param.ontoEntityURI;
        } else if (descType.equalsIgnoreCase(ServiceConfigParams.objType)) {
            dbType = param.dbServiceUrl;
            ontoUri = param.ontoServiceURI;
        } else if (descType.equalsIgnoreCase(ResourceConfigParams.objType)){
            dbType = param.dbResourceUrl;
            ontoUri = param.ontoResourceURI;
        }else{
            out.println(param.invalidRepoMsg);
            return;
        }
       
        String fileValue = "";
        Utils tool = new Utils();

        while ((part = mp.readNextPart()) != null) {

            if (part.isFile()) {
                // it's a file part
                FilePart filePart = (FilePart) part;
                String fileName = filePart.getFileName();
                if (fileName != null && fileName.length() > 0) {
                    InputStream in = filePart.getInputStream();
                    fileValue = tool.convertStreamToString(in);
                }
            }
        }

        SDBHandler sdbHandler = new SDBHandler();
        sdbHandler.SDBStore(dbType, param.dbuser, param.dbpass, param.ttlFile, fileValue, ontoUri);

        //out.println(fileValue);
        out.println("<div>");
        out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
        out.println("<hr>");
        out.println("<br>");
        out.println("RDF data is created successfully...<br>");
        out.println("<br>");
        out.println("Click <a href=\"/S2W/publish/ResourcePubForm.html\">here</a> to go to publish a resource;<br>");
        out.println("Click <a href=\"/S2W/publish/EntityPubForm.html\">here</a> to go to publish an entity;<br>");
        out.println("Click <a href=\"/S2W/query/QueryEndpoint.html\">here</a> to make a query<br>");
        out.println("Click <a href=\"/S2W/map/MapOverlay.html\">here</a> to locate an object on a map;<br>");
        out.println("Click <a href=\"/S2W\">here</a> to go back to the home page.<br>");
        out.println("</div>");
        ///end of Debug data
//            } catch (FileUploadException ex) {
//                Logger.getLogger(PubRDFDirect.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (Exception e) {
//                e.printStackTrace(out);
//            } finally {
//                out.close();
//            }
//        }


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
            Logger.getLogger(PublishObjectFileServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PublishObjectFileServlet.class.getName()).log(Level.SEVERE, null, ex);
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
