/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

/**
 *
 * @author te0003
 */
public class ClientAcceptFormatter {
    
    public ClientAcceptFormatter(){}
    
    public StringRepresentation formatRdf(StringRepresentation resultInFormat, String format, MediaType mt){
        
        if (mt.equals(MediaType.ALL)) {
            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
            return resultInFormat;
        }
    
        try{
        switch (format) {
            case "RDF/XML":
                resultInFormat.setMediaType(MediaType.APPLICATION_RDF_XML);
                break;
            case "RDF/XML-ABBREV":
                resultInFormat.setMediaType(MediaType.APPLICATION_RDF_XML);
                break;
            case "RDF/JSON":
                resultInFormat.setMediaType(MediaType.APPLICATION_JSON);
                break;
            case "TURTLE":
                resultInFormat.setMediaType(MediaType.APPLICATION_RDF_TURTLE);
                break;
            case "N3":
                resultInFormat.setMediaType(MediaType.TEXT_RDF_N3);
                break;
            case "N-TRIPLE":
                resultInFormat.setMediaType(MediaType.TEXT_RDF_NTRIPLES);
                break;
            default:
                resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
        }
        }catch(Exception e){
            System.out.println(e.getMessage());
        resultInFormat.setMediaType(MediaType.TEXT_PLAIN);            
        }        
        
        return resultInFormat;
    }
    
    public StringRepresentation formatSparql(StringRepresentation resultInFormat, String format, MediaType mt){
        
        if (mt.equals(MediaType.TEXT_PLAIN)) {
            resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
            return resultInFormat;
        }
        
        format=format.toUpperCase();
        
         try{
        switch (format) {
            case "TXT":
                resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
                break;
            case "JSON":
                resultInFormat.setMediaType(MediaType.APPLICATION_JSON);
                break;
            case "CSV":
                resultInFormat.setMediaType(MediaType.TEXT_CSV);
                break;
            case "TSV":
                resultInFormat.setMediaType(MediaType.TEXT_TSV);
                break;
            case "XML":
                resultInFormat.setMediaType(MediaType.APPLICATION_XML);
                break;            
            default:
                resultInFormat.setMediaType(MediaType.TEXT_PLAIN);
        }
        }catch(Exception e){
            System.out.println(e.getMessage());
        resultInFormat.setMediaType(MediaType.TEXT_PLAIN);            
        }
        
         return resultInFormat;
        
    }
}
