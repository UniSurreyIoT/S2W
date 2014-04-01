/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.linkeddata;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.hp.hpl.jena.query.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Payam Barnaghi p.barnaghi@surrey.ac.uk
 */
public class DBPediaTagSuggestServlet extends HttpServlet {

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
    public List<String> topicList = new ArrayList<String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        topicList.add("ALL");
        topicList.add("http://dbpedia.org/ontology/Place");
        topicList.add("http://dbpedia.org/ontology/Species");
        topicList.add("http://dbpedia.org/ontology/Organisation");
        topicList.add("http://dbpedia.org/ontology/Infrastructure");
        topicList.add("http://www.w3.org/2003/01/geo");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        ResultSet results;

        try {
            String tag = request.getParameter("query");
            String limit = request.getParameter("limit");
            String topic = request.getParameter("topic");

            //if ((limit.length() ==0)||(limit==null))
//            try {
//                limit.isEmpty();
//            } catch (NullPointerException ne) {
//                limit = "10"; //default value
//            }
            if (StringUtils.isBlank(limit)) {
                limit = "10"; //default value
            }


            if (!StringUtils.isBlank(tag)) {
                if (StringUtils.isBlank(topic)) {
                    results = dbpediaQuery(tag, limit);
                    StringBuffer xmlData = ResultSetToXMLString(results);
                    out.print(xmlData);
                } else {
                    //if (tag.length()>0) {
                    if (topicList.contains(topic)) {
                        results = dbpediaTopicQuery(tag, limit, topic);
                        StringBuffer xmlData = ResultSetToXMLString(results);
                        out.print(xmlData);
                    }else 
                        out.println("no correct tag specified");
                }
            } else {
                out.println("no correct tag specified");
            }


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

    ResultSet dbpediaQuery(String keyword, String limit) {
        ResultSet results;
        String sparqlQueryString = 
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + " prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + " SELECT ?entity  ?label WHERE { \n"
                + //" ?entity rdf:type <http://dbpedia.org/ontology/Place>. \n" +
                " ?entity rdfs:label ?label. \n"
                + " ?label <bif:contains> \"'" + keyword + "'\".  \n"
                + "} \n"
                + " LIMIT " + limit;

        Query query = QueryFactory.create(sparqlQueryString);

        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);


        try {
            results = qexec.execSelect();
        } finally {
            qexec.close();
        }
        return results;
    }

    ResultSet dbpediaTopicQuery(String keyword, String limit, String topic) {
        ResultSet results;
        String sparqlQueryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + " prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + " SELECT ?entity  ?label WHERE { \n";
        if (topic.compareTo("ALL") != 0) {
            sparqlQueryString = sparqlQueryString + " ?entity rdf:type <" + topic + ">. \n";
        }

        sparqlQueryString = sparqlQueryString + " ?entity rdfs:label ?label. \n";

        if (keyword.length() > 0) {
            if (keyword.indexOf("*") >= 0 && keyword.length() <= 4) {
                sparqlQueryString = sparqlQueryString + "FILTER (regex(?label, \"" + keyword + "\")). \n";
            } else {
                sparqlQueryString = sparqlQueryString + " ?label <bif:contains> \"'" + keyword + "'\".  \n";
            }
        }

        sparqlQueryString = sparqlQueryString + "} \n"
                + " LIMIT " + limit;

        Query query = QueryFactory.create(sparqlQueryString);

        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);


        try {
            results = qexec.execSelect();
        } finally {
            qexec.close();
        }
        return results;
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
                    dbpediaEntity = filterNonAscii(dbpediaEntity);
                    dbpediaLabel = filterNonAscii(dbpediaLabel);
                    str.append("<tag> \n");
                    str.append("<entity>").append(dbpediaEntity).append("</entity> \n");
                    str.append("<label>").append(dbpediaLabel).append("</label> \n");
                    str.append("</tag> \n");
                }
            }
            str.append("</tags>");
            return str;
        } catch (Exception e) {
            str.append("</tags>");
            return str;
        }
    }
}
