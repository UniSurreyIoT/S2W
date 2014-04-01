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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.XMLDocumentHandler;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;

/**
 *
 * @author Payam
 */
public class UpdateDescriptionForm2 extends HttpServlet {

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

        String dbType = "";
        String ontoUri = "";
        String objectId = "";
        String objUrl = "";

        ArrayList element = new ArrayList();
        ArrayList text = new ArrayList();
        String fileValue = "";
        Utils tool = new Utils();

        try {

            //http://www.jguru.com/faq/view.jsp?EID=1045507
            MultipartParser mp = new MultipartParser(request, 1 * 1024 * 50); // 500 KB
            Part part;

            /***********************get type of object*************************/
            part = mp.readNextPart();
            ParamPart paramName1 = (ParamPart) part;
            String descType = paramName1.getStringValue();

            /***************************get object ID**************************/
            part = mp.readNextPart();
            ParamPart paramName2 = (ParamPart) part;
            objectId = paramName2.getStringValue();
            element.add(paramName2.getName());
            text.add(objectId);

            out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:large;\">Description updated:");
            out.println("<br><br>");
            out.println("<div>");
            out.println("<table border=\"3\">");
            descType.toUpperCase();

            /*****************read all other metadata*************************/
            while ((part = mp.readNextPart()) != null) {

                String name = part.getName();
                if (part.isParam()) {
                    // it's a parameter part
                    ParamPart paramName = (ParamPart) part;
                    String value = paramName.getStringValue();
                    element.add(paramName.getName());
                    text.add(value);
                    out.println("<tr>");
                    out.println("<td>");
                    out.println("<span style= \"font-weight: normal;font-family: Calibri;font-size:medium;\">" + name + ": </span>");
                    out.println("</td>");
                    out.println("<td>");
                    out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:bold;\"></span>" + value + "\n");
                    out.println("</td>");
                    out.println("</tr>");
                } else if (part.isFile()) {
                    // it's a file part
                    FilePart filePart = (FilePart) part;
                    String fileName = filePart.getFileName();
                    if (fileName != null && fileName.length() > 0) {
                        InputStream in = filePart.getInputStream();
                        fileValue = Utils.convertStreamToString(in);
                    }
                }
            }
            out.println("</table>");
            out.println("</div>");

            String rdfLink = tool.RDFLink(fileValue);
            if (rdfLink != null && rdfLink.length() > 0) {
                element.add((String) "RDFData");
                text.add(rdfLink);
            }
            System.out.println("******");
            System.out.println(rdfLink.toString());
            System.out.println("******");

            Source xsltSource = null;

            if (descType.equalsIgnoreCase("entity")) {
                dbType = param.dbEntityUrl;
                ontoUri = param.ontoEntityURI;
                objUrl = param.ontoEntityURI + objectId;
                xsltSource = new StreamSource(context.getRealPath(param.xsltEntityFile));

            } else if (descType.equalsIgnoreCase("service")) {
                dbType = param.dbServiceUrl;
                ontoUri = param.ontoServiceURI;
                objUrl = param.ontoServiceURI + objectId;
                xsltSource = new StreamSource(context.getRealPath(param.xsltServiceFile));

            } else {
                dbType = param.dbResourceUrl;
                ontoUri = param.ontoResourceURI;
                objUrl = param.ontoResourceURI + objectId;
                xsltSource = new StreamSource(context.getRealPath(param.xsltResourceFile));
            }

            /******************form RDF XML representation*********************/
            String hmn ="";
            hmn = "1";
            String xmlString = "";
            xmlString = "bb";
            XMLDocumentHandler docHandler = new XMLDocumentHandler();
            xmlString = docHandler.createSimpleOntology(xsltSource, element, text, descType);

            SDBHandler sdbHandler = new SDBHandler();

            /***********************delete old model***************************/
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

            /***********************publish updated model**********************/
            sdbHandler.SDBStore(dbType, param.dbuser, param.dbpass, param.ttlFile, fileValue, ontoUri);

            /*******************print out result page with links***************/
            out.println("<div>");
            out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
            out.println("<hr>");
            out.println("<br>");
            out.println("RDF data has been updated successfully...<br>");
            out.println("<br>");
            out.println("Click <a href=\"/S2W/publish/Resource.html\">here</a> to go to publish a resource;<br>");
            out.println("Click <a href=\"/S2W/publish/Entity.html\">here</a> to go to publish an entity;<br>");
            out.println("Click <a href=\"/S2W/query/QueryEndpoint.html\">here</a> to make a query<br>");
            out.println("Click <a href=\"/S2W/map/MapOverlay.html\">here</a> to locate an object on a map;<br>");
            out.println("Click <a href=\"/S2W\">here</a> to go back to the home page.<br>");
            out.println("</div>");

        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {
            out.close();
            //XBasicDataSource xbasic = new XBasicDataSource();
            //xbasic.close();
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateDescriptionForm2.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(UpdateDescriptionForm2.class.getName()).log(Level.SEVERE, null, ex);
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
