/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.surrey.ee.ccsr.s2w.linkeddata;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;



/**
 *
 * @author Payam
 */
public class GeoLocationMapper {

    String resourceURI = "";
    String lat = "";
    String lng = "";

    public void setGeoLocationMapperURI (String uri) {
        resourceURI = uri;
    }

    public String getLat () {
        return lat;
    }

    public String getLng () {
        return lng;
    }

    protected ResultSet runDBPQuery (String queryStr){

        Query query = QueryFactory.create(queryStr);

        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results=null;
        try {
            results = qexec.execSelect();
        }
        finally {
            qexec.close() ;
            return results;
        }
    }

    
    protected boolean isPointEmpty() {
        boolean flag = false;
        if (lat ==null || lat.length()==0)
                flag = true;
        if (lng ==null || lng.length()==0)
                flag = true;

        return flag;
    }
    
    public void getDirectLocationMap (String resource) {

         String queryStr = "Prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n" +
                            "select ?lat ?lng \n" +
                            "where {  \n" +
                            "<" + resource + ">" + " geo:lat ?lat.   \n" +
                            "<" + resource + ">" + " geo:long ?lng. \n" +
                            "} \n"  +
                            "limit 1";
        ResultSet rs = runDBPQuery(queryStr);
        for ( ; rs.hasNext() ; ) {
            QuerySolution soln = rs.nextSolution() ;
            lat = soln.get("lat").toString();
            lng = soln.get("lng").toString();
        }
    }


    public void getRedirectLocationMap (String resource) {

        
        String queryStr =  " prefix dp: <http://dbpedia.org/property/> \n" +
                           " Prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n" +
                           " select ?lat ?lng \n" +
                           " where { \n" +
                           " <" + resource + ">" + " dp:redirect ?anyRes.  \n" +
                           " ?anyRes geo:lat ?lat.  \n" +
                           " ?anyRes geo:long ?lng.  \n" +
                           " } \n" +
                            "limit 1";
        ResultSet rs = runDBPQuery(queryStr);
        for ( ; rs.hasNext() ; ) {
            QuerySolution soln = rs.nextSolution() ;
            lat = soln.get("lat").toString();
            lng = soln.get("lng").toString();
        }

    }

    public void getDirectCityLocationMap (String resource) {

        String queryStr = "prefix dp: <http://dbpedia.org/property/> \n" + 
                          "Prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>  \n" +
                          "select ?lat ?lng \n" + 
                          "where { \n" +
                          "<" + resource + ">" + " dp:city  ?anyRes. \n" +
                          "?anyRes dp:latitude ?lat.  \n" +
                          "?anyRes dp:longitude ?lng.  \n" +
                          "}"+
                            "limit 1";
        
        ResultSet rs = runDBPQuery(queryStr);
        for ( ; rs.hasNext() ; ) {
            QuerySolution soln = rs.nextSolution() ;
            lat = soln.get("lat").toString();
            lng = soln.get("lng").toString();
        }

    }

    public void getRedirectCityLocationMap (String resource) {


        String queryStr =  "prefix dp: <http://dbpedia.org/property/> \n" +
                           "Prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> \n" +
                           "select ?lat ?lng \n" +
                           "where { \n" +
                           "<" + resource + ">" + " dp:redirect ?redirRes. \n" +
                           "?redirRes dp:city  ?anyRes. \n" +
                           "?anyRes dp:latitude ?lat.  \n" +
                           "?anyRes dp:longitude ?lng.  \n" +
                           "}"+
                            "limit 1";
        ResultSet rs = runDBPQuery(queryStr);
        for ( ; rs.hasNext() ; ) {
            QuerySolution soln = rs.nextSolution() ;
            lat = soln.get("lat").toString();
            lng = soln.get("lng").toString();
        }
        
    }

    public boolean getGeoPoint (String resource){

        getDirectLocationMap(resource);
        if (!(isPointEmpty()))
                return true;

        getRedirectLocationMap(resource);
        if (!(isPointEmpty()))
                return true;

        getDirectCityLocationMap(resource);
        if (!(isPointEmpty()))
                return true;

        getRedirectCityLocationMap(resource);
        if (!(isPointEmpty()))
                return true;

        return false;
    }

}
