/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.servlet;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author te0003
 */
public class WebFormResultLinks {
    
    public String result_RDF_XML = "?resultFormat=RDF/XML";
    public String result_RDF_XML_ABBREV = "?resultFormat=RDF/XML-ABBREV";
    public String result_TURTLE = "?resultFormat=TURTLE";
    public String result_N_TRIPLE = "?resultFormat=N-TRIPLE";
    public String result_N3 = "?resultFormat=N3";
    public String result_RDF_JSON = "?resultFormat=RDF/JSON";
    
    public Map<String, String> formatLinks = new HashMap<String, String>();

    public Map<String, String> getFormatLinks() {
        return formatLinks;
    }

    public String lookupLink = "";
    public String objectId ="";
    
    public WebFormResultLinks(String descriptionLink){
        
        formatLinks.put("RDF/XML", descriptionLink +  result_RDF_XML);
        formatLinks.put("RDF/XML-ABBREV", descriptionLink + result_RDF_XML_ABBREV);
        formatLinks.put("TURTLE", descriptionLink + result_TURTLE);
        formatLinks.put("N-TRIPLE", descriptionLink +  result_N_TRIPLE);
        formatLinks.put("N3", descriptionLink + result_N3);
        formatLinks.put("RDF/JSON", descriptionLink + result_RDF_JSON);
        
    }

    
    
    
    
}
