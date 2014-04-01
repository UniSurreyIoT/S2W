/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota.query;

import com.hp.hpl.jena.query.*;
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
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.Entity;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.EntityQuery;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.Resource;
import uk.ac.surrey.ee.ccsr.s2w.model.iota.old.ResourceQuery;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author Payam
 */
public class QueryFormHandlerServlet extends HttpServlet {

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

        PrintWriter out = response.getWriter();
        Parameters param = new Parameters();
        //System.out.println("hello! ");
        String format = request.getParameter("format");
        String repoSelect = request.getParameter("desctype");
        String dbType = "";
        String qStatement = "";
        //repoSelect = "resource";
        //System.out.println("hello: "+repoSelect);

        if (repoSelect.equalsIgnoreCase(EntityConfigParams.objType)) {
            Entity entity = new Entity(request);
            EntityQuery eq = new EntityQuery();
            qStatement = eq.constructQuery(entity);
            dbType = param.dbEntityUrl;
        } else if (repoSelect.equalsIgnoreCase(ResourceConfigParams.objType)) {
            Resource resource = new Resource(request);
            ResourceQuery rq = new ResourceQuery();
            qStatement = rq.constructQuery(resource);
            dbType = param.dbResourceUrl;
        } else {
            response.sendError(response.SC_BAD_REQUEST, "model type not defined");
        }
        //System.out.println(dbType);
        System.out.println("Query: " + qStatement);
        //Query query = QueryFactory.create(qStatement) ;
        //query.isSelectType();
        SDBHandler sdbHandler = new SDBHandler();
        StoreDesc storeDesc = sdbHandler.generateStoreDescSdb(dbType, param.dbuser, param.dbpass);
        storeDesc.setLayout(LayoutType.LayoutTripleNodesIndex);
        Store store = SDBFactory.connectStore(storeDesc);
        Dataset ds = DatasetStore.create(store);
        QueryExecution qe = QueryExecutionFactory.create(qStatement, ds);

        try {
            ResultSet rs = qe.execSelect();

            if (format.equalsIgnoreCase("xml")) {
                out.println(ResultSetFormatter.asXMLString(rs));
            } else if (format.equalsIgnoreCase("txt")) {
                out.println(ResultSetFormatter.asText(rs));
            } else if (format.equalsIgnoreCase("table")) {
                if (repoSelect.equalsIgnoreCase("entity")) {
                    out.println(entResultOut(rs));
                } else if (repoSelect.equalsIgnoreCase("resource")) {
                    out.println(resResultOut(rs));
                }
            } else {
                response.sendError(response.SC_BAD_REQUEST, "model type not defined");
            }
        } finally {
            qe.close();
            ds.close();
            store.close();
        }
    }

    private String resResultOut(ResultSet rs) {

        String htmlbody = "<html><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>"
                + "<div style=\"text-align: right;\"><span style=\"text-align: right;\"><a href=\"/S2W/\"><img alt=\"\" src=\"/S2W/images/home-icon.png\" style=\"width: 50px; height: 50px;\" border=\"0\" />"
                + "</a></span></div><div style=\"text-align: left;\"><table style=\"text-align: left; margin-left: auto; margin-right: auto; height: 73px;"
                + " width: 96%;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">"
                + "<tbody><tr><td style=\"text-align: left; width: 361px; height: 69px;\"></td>"
                + "<td style=\"width: 596px; height: 69px;\"><small></small><h1 style=\"text-align: center;\"><small><span class=\"\"><big style=\"font-weight: bold;"
                + "font-family: Calibri;\">QUERY RESULT</big></span></small></h1><small></small>"
                + "</td><td style=\"text-align: right; width: 361px; height: 69px;\">"
                + "</td></tr></tbody></table><br></div><table style=\"width: 96%; text-align: left; margin-left: auto; margin-right: auto;\" border=\"1\" cellpadding=\"2\""
                + "cellspacing=\"2\"><tbody><tr>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">Name</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">ID</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">Type</th>"
                + "<th style=\"text-align: center; color: white; width: 217px; background-color: rgb(153, 0, 0); font-family: Calibri;\">Linked-Data Tag</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); width: 158px; font-family: Calibri;\">Global Location</th>"
                + "<td style=\"text-align: center; width: 126px; background-color: rgb(153, 0, 0); color: white; font-weight: bold; font-family: Calibri;\">Interface Type</td>"
                + "<td style=\"text-align: center; width: 345px; background-color: rgb(153, 0, 0); color: white; font-weight: bold; font-family: Calibri;\">Inteface Link</td>"
                + "</tr>";

        for (; rs.hasNext();) {

            QuerySolution soln = rs.nextSolution();

            String id = "";
            if (soln.get("id").toString() != null) {
                id = soln.get("id").toString();
            }
            //String name = soln.get("name").toString();
            String name = "";
            if (soln.get("name").toString() != null) {
                name = soln.get("name").toString();
            }
            //String type = soln.get("type").toString();
            String type = "";
            if (soln.get("type").toString() != null) {
                type = soln.get("type").toString();
            }
            String ldUri = "";
            if (soln.get("uriTag").toString() != null) {
                ldUri = soln.get("uriTag").toString();
            }
            //String globalLocation = soln.get("globalLocation").toString();
            String globalLocation = "";
            if (soln.get("globalLoc").toString() != null) {
                globalLocation = soln.get("globalLoc").toString();
            }
            //String localLocation = soln.get("localLocation").toString();
            String localLocation = "";
            if (soln.get("localLoc").toString() != null) {
                localLocation = soln.get("localLoc").toString();
            }
            //String tag = soln.get("tag").toString();
            String tag = "";
            if (soln.get("tag").toString() != null) {
                tag = soln.get("tag").toString();
            }
            //String lat = soln.get("latitude").toString();
            String lat = "";
            if (soln.get("latitude").toString() != null) {
                lat = soln.get("latitude").toString();
            }
            //String lng = soln.get("longitude").toString();
            String lng = "";
            if (soln.get("longitude").toString() != null) {
                lng = soln.get("longitude").toString();
            }

            String ifType = "";
            if (soln.get("ifType").toString() != null) {
                ifType = soln.get("ifType").toString();
            }

            String ifLink = "";
            if (soln.get("accessIf").toString() != null) {
                ifLink = soln.get("accessIf").toString();
            }

            htmlbody +=
                    "<tr>"
                    + "<td style=\"text-align: center;font-family: Calibri;\">" + name + "</a></td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + id + "\">" + id + "</td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + type + "\">" + type + "</td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + ldUri + "\">" + ldUri + "</td>"
                    + "<td style=\"text-align: center; width: 158px;font-family: Calibri;\"><a href=\"" + globalLocation + "\">" + globalLocation + "</td>"
                    + "<td style=\"width: 126px; text-align: center;font-family: Calibri;\"><a href=\"" + ifType + "\">" + ifType + "</td>"
                    + "<td style=\"width: 345px; text-align: center;font-family: Calibri;\"><a href=\"" + ifLink + "\">" + ifLink + "</td>"
                    + "</tr>";
        }

        htmlbody += " </tbody></table>"
                + "<div style=\"text-align: center;\">"
                + "<br><br><br><img style=\"width: 600px; height: 60px; text-align: center; clear: both\" alt=\"Contributer logos\" src=\"/S2W/images/contributer-logos.png\"><br />"
                + "<br /><small><small><span style=\"font-family: Verdana;\">Copyright @Centre for Communication Systems Research, University of Surrey, 2011.</span></small></small> <span style=\"font-weight: bold;\"></span>"
                + "</div></body></html>";

        return htmlbody;

    }

    private String entResultOut(ResultSet rs) {

        String htmlbody = "<html><head><title></title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>"
                + "<div style=\"text-align: right;\"><span style=\"text-align: right;\"><a href=\"/S2W/\"><img alt=\"\" src=\"/S2W/images/home-icon.png\" style=\"width: 50px; height: 50px;\" border=\"0\" />"
                + "</a></span></div><div style=\"text-align: left;\"><table style=\"text-align: left; margin-left: auto; margin-right: auto; height: 73px;"
                + " width: 96%;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\">"
                + "<tbody><tr><td style=\"text-align: left; width: 361px; height: 69px;\"></td>"
                + "<td style=\"width: 596px; height: 69px;\"><small></small><h1 style=\"text-align: center;\"><small><span class=\"\"><big style=\"font-weight: bold;"
                + "font-family: Calibri;\">QUERY RESULT</big></span></small></h1><small></small>"
                + "</td><td style=\"text-align: right; width: 361px; height: 69px;\">"
                + "</td></tr></tbody></table><br></div><table style=\"width: 96%; text-align: left; margin-left: auto; margin-right: auto;\" border=\"1\" cellpadding=\"2\""
                + "cellspacing=\"2\"><tbody><tr>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">Name</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">ID</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); font-family: Calibri;\">Tag</th>"
                + "<th style=\"text-align: center; color: white; width: 217px; background-color: rgb(153, 0, 0); font-family: Calibri;\">Linked-Data Tag</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); width: 158px; font-family: Calibri;\">Global Location</th>"
                + "<th style=\"text-align: center; color: white; background-color: rgb(153, 0, 0); width: 158px; font-family: Calibri;\">Local Location</th>"
                + "</tr>";

        for (; rs.hasNext();) {

            QuerySolution soln = rs.nextSolution();

            String id = "";
            if (soln.get("id").toString() != null) {
                id = soln.get("id").toString();
            }
            //String name = soln.get("name").toString();
            String name = "";
            if (soln.get("name").toString() != null) {
                name = soln.get("name").toString();
            }
            //String tag = soln.get("tag").toString();
            String tag = "";
            if (soln.get("tag").toString() != null) {
                tag = soln.get("tag").toString();
            }

            String ldUri = "";
            if (soln.get("uriTag").toString() != null) {
                ldUri = soln.get("uriTag").toString();
            }
            //String globalLocation = soln.get("globalLocation").toString();
            String globalLocation = "";
            if (soln.get("globalLoc").toString() != null) {
                globalLocation = soln.get("globalLoc").toString();
            }
            //String localLocation = soln.get("localLocation").toString();
            String localLocation = "";
            if (soln.get("localLoc").toString() != null) {
                localLocation = soln.get("localLoc").toString();
            }

            //String lat = soln.get("latitude").toString();
            String lat = "";
            if (soln.get("latitude").toString() != null) {
                lat = soln.get("latitude").toString();
            }
            //String lng = soln.get("longitude").toString();
            String lng = "";
            if (soln.get("longitude").toString() != null) {
                lng = soln.get("longitude").toString();
            }

            htmlbody +=
                    "<tr>"
                    + "<td style=\"text-align: center;font-family: Calibri;\">" + name + "</a></td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + id + "\">" + id + "</td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + tag + "\">" + tag + "</td>"
                    + "<td style=\"text-align: center;font-family: Calibri;\"><a href=\"" + ldUri + "\">" + ldUri + "</td>"
                    + "<td style=\"text-align: center; width: 158px;font-family: Calibri;\"><a href=\"" + globalLocation + "\">" + globalLocation + "</td>"
                    + "<td style=\"text-align: center; width: 158px;font-family: Calibri;\"><a href=\"" + localLocation + "\">" + localLocation + "</td>"
                    + "</tr>";
        }

        htmlbody += " </tbody></table>"
                + "<div style=\"text-align: center;\">"
                + "<br><br><br><img style=\"width: 600px; height: 60px; text-align: center; clear: both\" alt=\"Contributer logos\" src=\"/S2W/images/contributer-logos.png\"><br />"
                + "<br /><small><small><span style=\"font-family: Verdana;\">Copyright @Centre for Communication Systems Research, University of Surrey, 2011.</span></small></small> <span style=\"font-weight: bold;\"></span>"
                + "</div></body></html>";

        return htmlbody;

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

        processRequest(request, response);

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

        processRequest(request, response);

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
