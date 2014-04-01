/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;

/**
 *
 * @author Payam
 */
public class UpdateDescriptionFileServlet extends HttpServlet {

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
            throws ServletException, IOException, SQLException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        ServletContext context = getServletConfig().getServletContext();

        Parameters param = new Parameters();
        param.ttlFile = context.getRealPath(param.ttlFile);

        String dbType = "";
        String ontoUri = "";

        /**
         * *********************get type of object************************
         */
        MultipartParser mp = new MultipartParser(request, 1 * 1024 * 50); // 500 KB
        Part part;

        part = mp.readNextPart();
        ParamPart paramName1 = (ParamPart) part;
        String descType = paramName1.getStringValue();

        /**
         * *************************get object ID*************************
         */
        part = mp.readNextPart();
        ParamPart paramName2 = (ParamPart) part;
        String objectId = paramName2.getStringValue();
        String objUrl = "";

        if (descType.equalsIgnoreCase(EntityConfigParams.objType)) {
            dbType = param.dbEntityUrl;
            ontoUri = param.ontoEntityURI;
            objUrl = param.ontoEntityURI + objectId;
        } else if (descType.equalsIgnoreCase(ServiceConfigParams.objType)) {
            dbType = param.dbServiceUrl;
            ontoUri = param.ontoServiceURI;
            objUrl = param.ontoServiceURI + objectId;
        } else if (descType.equalsIgnoreCase(ResourceConfigParams.objType)) {
            dbType = param.dbResourceUrl;
            ontoUri = param.ontoResourceURI;
            objUrl = param.ontoResourceURI + objectId;
        } else {
            out.println("<div>");
            out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
            out.println("<hr>");
            out.println("<br>");
            out.println(param.invalidRepoMsg + "...<br>");
            out.println("<br>");
            out.println("Click <a href=\"/S2W/publish/ResourcePubForm.html\">here</a> to go to publish a resource;<br>");
            out.println("Click <a href=\"/S2W/publish/EntityPubForm.html\">here</a> to go to publish an entity;<br>");
            out.println("Click <a href=\"/S2W/query/QueryEndpoint.html\">here</a> to make a query<br>");
            out.println("Click <a href=\"/S2W/map/MapOverlay.html\">here</a> to locate an object on a map;<br>");
            out.println("Click <a href=\"/S2W\">here</a> to go back to the home page.<br>");
            out.println("</div>");
            return;
        }

        /**
         * ***************read all other metadata************************
         */
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


        /**
         * *********************delete old model**************************
         */
        StoreDesc storeDesc;
        storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);

        Model model = SDBFactory.connectDefaultModel(store);
        Resource res = model.getResource(objUrl);

        out.println("<div>");
        out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
        out.println("<hr>");
        out.println("<br>");

        if (model.contains(res, null) == true) {
            res.removeProperties();
            System.out.println("Object unpublished!");
            out.println("<div>");
            out.println("Object unpublished!<br>");
        } else {
            out.println("No object found by this ID.<br>");
        }

        /**
         * *********************publish updated model*********************
         */
        sdbHandler.SDBStore(dbType, param.dbuser, param.dbpass, param.ttlFile, fileValue, ontoUri);

        /**
         * *****************print out result page with links**************
         */
        out.println("<div>");
        out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
        out.println("<hr>");
        out.println("<br>");
        //out.println("RDF data is created successfully...<br>");
        out.println("<br>");
        out.println("Click <a href=\"/S2W/publish/Resource.html\">here</a> to go to publish a resource;<br>");
        out.println("Click <a href=\"/S2W/publish/Entity.html\">here</a> to go to publish an entity;<br>");
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateDescriptionFileServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateDescriptionFileServlet.class.getName()).log(Level.SEVERE, null, ex);
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
