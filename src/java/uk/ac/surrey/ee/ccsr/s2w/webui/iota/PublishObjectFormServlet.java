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
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ServiceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.XMLDocumentHandler;
import uk.ac.surrey.ee.ccsr.s2w.util.Utils;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;
import uk.ac.surrey.ee.ccsr.s2w.webui.iota.query.SDBHandler;

/**
 *
 * @author Payam
 */
public class PublishObjectFormServlet extends HttpServlet {

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
        //param.ttlFile = context.getRealPath(param.ttlFile);
        String dbType = "";
        String xslFile = "";

        ArrayList element = new ArrayList();
        ArrayList text = new ArrayList();
        String fileValue = "";
        Utils tool = new Utils();

        try {

            //http://www.jguru.com/faq/view.jsp?EID=1045507
            MultipartParser mp = new MultipartParser(request, 1 * 1024 * 50); // 500 KB
            Part part;

            /**
             * *********************get type of object************************
             */
            part = mp.readNextPart();
            ParamPart paramName1 = (ParamPart) part;
            String descType = paramName1.getStringValue();

            out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:large;\">Description published:");
            out.println("<br><br>");
            out.println("<div>");
            out.println("<table border=\"3\">");
            descType.toUpperCase();

            /**
             * *********************read description elements and values*********************
             */
            while ((part = mp.readNextPart()) != null) {

                String name = part.getName();
                if (part.isParam()) {
                    // it's a parameter part
                    ParamPart paramName = (ParamPart) part;
                    String value = paramName.getStringValue();
                    element.add(paramName.getName());
                    text.add(value);
                    //out.println("param; name=" + name + ", value=" + value +"\n");
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
                        fileValue = tool.convertStreamToString(in);
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

            String xmlString = "";
            String ontoUri = "";
            Source xsltSource = null;

            if (descType.equalsIgnoreCase(EntityConfigParams.objType)) {
                dbType = param.dbEntityUrl;
                ontoUri = param.ontoEntityURI;
                xsltSource = new StreamSource(context.getRealPath(param.xsltEntityFile));
                //EntityXMLDocumentHandler docHandler = new EntityXMLDocumentHandler();
                //xmlString = docHandler.createSimpleOntology(xsltSource, element, text);

            } else if (descType.equalsIgnoreCase(ServiceConfigParams.objType)) {
                dbType = param.dbServiceUrl;
                ontoUri = param.ontoServiceURI;
                xsltSource = new StreamSource(context.getRealPath(param.xsltServiceFile));
                //ServiceXMLDocumentHandler docHandler = new ServiceXMLDocumentHandler();
                //xmlString = docHandler.createSimpleOntology(xsltSource, element, text);

            } else if (descType.equalsIgnoreCase(ResourceConfigParams.objType)) {
                dbType = param.dbResourceUrl;
                ontoUri = param.ontoResourceURI;
                xsltSource = new StreamSource(context.getRealPath(param.xsltResourceFile));
                //XMLDocumentHandler docHandler = new XMLDocumentHandler();
                //xmlString = docHandler.createSimpleOntology(xsltSource, element, text);
            } else {
                response.sendError(response.SC_BAD_REQUEST, "model type not defined");
                return;
            }

            XMLDocumentHandler docHandler = new XMLDocumentHandler();
            xmlString = docHandler.createSimpleOntology(xsltSource, element, text, descType);
            SDBHandler sdbHandler = new SDBHandler();

            /**
             * *********************publish updated model*********************
             */
            sdbHandler.SDBStore(dbType, param.dbuser, param.dbpass, param.ttlFile, xmlString, ontoUri);

            // out.println("Here's the xml:\n\n" + xmlString);            
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"StyleSheet\" href=\"/S2W/CSS/background.css\" type=\"text/css\">");
            out.println("<link rel=\"StyleSheet\" href=\"/S2W/CSS/input-formatting.css\" type=\"text/css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<div><!--#include virtual=\"/S2W/reusable/menu-header2.html\" --></div>");
            out.println("<div>");
            out.println("<span style= \"font-weight: bold;font-family: Calibri;font-size:medium;\"/>");
            out.println("<hr>");
            out.println("<br>");
            out.println("RDF data is created successfully...<br>");
            out.println("<br>");
            out.println("Click <a href=\"/S2W/publish/Resource.html\">here</a> to go to publish a resource;<br>");
            out.println("Click <a href=\"/S2W/publish/Entity.html\">here</a> to go to publish an entity;<br>");
            out.println("Click <a href=\"/S2W/query/QueryEndpoint.html\">here</a> to make a query<br>");
            out.println("Click <a href=\"/S2W/map/MapOverlay.html\">here</a> to locate an object on a map;<br>");
            out.println("Click <a href=\"/S2W\">here</a> to go back to the home page.<br>");
            out.println("</div>");
            out.println("<div><!--#include virtual=\"/S2W/reusable/logo-footer.html\" --></div>");
            out.println("</body>");
            out.println("</html>");

            ///end of Debug data

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
            Logger.getLogger(PublishObjectFormServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PublishObjectFormServlet.class.getName()).log(Level.SEVERE, null, ex);
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
