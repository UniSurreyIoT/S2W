/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.webui.iota.query;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import java.net.URL;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import uk.ac.surrey.ee.ccsr.s2w.config.old.Parameters;

/**
 *
 * @author Payam
 */
public class LocalQuery extends HttpServlet {

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
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String sparqlQueryString = "";
        Parameters param = new Parameters();

        String protocol= "http://";
        String localAddr = "localhost:";
        int portNumber = request.getLocalPort();
        

        try {
            String query = request.getParameter("query");
            String ontoLink = request.getParameter("ontoLink");
            String limit = request.getParameter("limit");

            if (limit == null) {
                limit = "10"; //default value
            }
            if (ontoLink == null) {
                ontoLink = protocol+localAddr+portNumber+param.localOnto;
            }

            sparqlQueryString = setSPARQLQuery(query, limit);


            Model model = ModelFactory.createDefaultModel();

            URL url = new URL(ontoLink);
            InputStream in = url.openStream();



            model.read(in, ontoLink); // null base URI, since model URIs are absolute

            // For debugging
            //model.write(System.out);

            Query sparql = QueryFactory.create(sparqlQueryString);
            QueryExecution qe = QueryExecutionFactory.create(sparql, model);
            ResultSet rs = qe.execSelect();
            StringBuffer xmlData = ResultSetToXMLString(rs);
            out.print(xmlData);


        } finally {
            out.close();
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

    ResultSet runSPARQLQuery(String url, String query) throws IOException {


        // Create an empty in-memory model and populate it from the graph
        Model model = ModelFactory.createDefaultModel();

        URL myUrl = new URL(url);
        InputStream in = myUrl.openStream();



        model.read(in, url); // null base URI, since model URIs are absolute

        // For debugging
        //model.write(System.out);

        Query sparql = QueryFactory.create(query);
        QueryExecution qe = QueryExecutionFactory.create(sparql, model);
        ResultSet rs = qe.execSelect();
        // For debugging
        //ResultSetFormatter.out(System.out, rs);
        String p = ResultSetFormatter.asXMLString(rs);
        ResultSet queryResults = rs;
        qe.close();
        in.close();
        return (queryResults);

    }

    String setSPARQLQuery(String keyword, String limit) {
        //String keyword = "BB";
        //int limit =10;

        String sparqlQueryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + " prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + " SELECT ?entity  ?label WHERE { \n"
                + " ?entity rdfs:label ?label. \n";
        sparqlQueryString = sparqlQueryString + "FILTER (regex(?label, \"" + keyword + "\")). \n";

        sparqlQueryString = sparqlQueryString + "} \n"
                + " LIMIT " + limit;

        return sparqlQueryString;
    }

    StringBuffer ResultSetToXMLString(ResultSet results) {

        StringBuffer str = new StringBuffer();
        String dbpediaEntity = "";
        String dbpediaLabel = "";
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
        str.append("<tags> \n");

        try {
            for (; results.hasNext();) {
                try {
                    QuerySolution soln = results.nextSolution();
                    dbpediaEntity = soln.get("entity").toString();
                    dbpediaLabel = soln.get("label").toString();
                } catch (Exception e) {
                    dbpediaEntity = null;
                    dbpediaLabel = null;
                }
                if (dbpediaEntity != null && dbpediaLabel != null) {
                    dbpediaLabel = dbpediaLabel.substring(0, dbpediaLabel.indexOf("^^"));
                    if (dbpediaEntity.length() > 0 && dbpediaLabel.length() > 0) {
                        //dbpediaEntity = filterNonAscii(dbpediaEntity);
                        //dbpediaLabel = filterNonAscii(dbpediaLabel);
                        str.append("<tag> \n");
                        str.append("<entity>").append(dbpediaEntity).append("</entity> \n");
                        str.append("<label>").append(dbpediaLabel).append("</label> \n");
                        str.append("</tag> \n");
                    }
                }
            }
            str.append("</tags>");
            return str;
        } catch (Exception e) {
            str.append("</tags>");
            return str;
        }
    }

    public static String filterNonAscii(String inString) {
        // adapted from http://www.velocityreviews.com/forums/t140837-convert-utf-8-to-ascii.html
        // Create the encoder and decoder for the character encoding
        Charset charset = Charset.forName("US-ASCII");
        CharsetDecoder decoder = charset.newDecoder();
        CharsetEncoder encoder = charset.newEncoder();
        // This line is the key to removing "unmappable" characters.
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
        String result = inString;

        try {
            // Convert a string to bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(inString));

            // Convert bytes in a ByteBuffer to a character ByteBuffer and then to a string.
            CharBuffer cbuf = decoder.decode(bbuf);
            result = cbuf.toString();
        } catch (CharacterCodingException cce) {
            String errorMessage = "Exception during character encoding/decoding: " + cce.getMessage();
            System.err.print(errorMessage);
        }

        return result;
    }
}
