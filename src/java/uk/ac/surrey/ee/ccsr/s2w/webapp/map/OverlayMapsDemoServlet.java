/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.surrey.ee.ccsr.s2w.webapp.map;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.surrey.ee.ccsr.s2w.config.ConfigParameters;
import uk.ac.surrey.ee.ccsr.s2w.config.EntityConfigParams;
import uk.ac.surrey.ee.ccsr.s2w.config.ResourceConfigParams;

/**
 *
 * @author Payam
 */
public class OverlayMapsDemoServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    public static ServletContext context;
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();        
        context= getServletContext();        
        try {
            String result = generateMapXML();  //get the html doc that includes the metadata about sensor(s)/entity
            out.print(result);
        } finally { 
            out.close();
        }
    }    

    protected String generateMapXML() {
        ConfigParameters cp = new ResourceConfigParams(context);
        ObjectListForMap olfm = new ObjectListForMap(cp);
        String resultList = "<markers>"; //element header used by g-maps api
        resultList+=olfm.getObjectList();
        cp = new EntityConfigParams(context);
        olfm = new ObjectListForMap(cp);
        resultList+=olfm.getObjectList();        
        resultList+="</markers>";
        System.out.println("this is final result:"+resultList);
        return (resultList);
    }
    
    
    
    protected String generateSampleMapsXML(){
        String output = "<markers>" +
        "<marker lat=\"51.243056\" lng=\"-0.589444\" html=\"University of Surrey&lt;br&gt;Test\" label=\"Info Human Sensor\"/>"+
        //"<marker lat=\"43.91892\" lng=\"-78.89231\" html=\"Some stuff to display in the&lt;br&gt;Second Info Window\" label=\"Marker Two\"/>" +
        //"<marker lat=\"43.82589\" lng=\"-79.10040\" html=\"Some stuff to display in the&lt;br&gt;Third Info Window\" label=\"Marker Three\"/>" +
        //"</markers>";
         
        "<marker2 lat=\"51.243056\" lng=\"-0.589444\" html=\"University of Surrey&lt;br&gt;Test\" label=\"tarek\"/>" +
//        //"<marker lat=\"43.91892\" lng=\"-78.89231\" html=\"Some stuff to display in the&lt;br&gt;Second Info Window\" label=\"Marker Two\"/>" +
//        //"<marker lat=\"43.82589\" lng=\"-79.10040\" html=\"Some stuff to display in the&lt;br&gt;Third Info Window\" label=\"Marker Three\"/>" +
        "</markers>";
        System.out.println(output);
        return output;
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
