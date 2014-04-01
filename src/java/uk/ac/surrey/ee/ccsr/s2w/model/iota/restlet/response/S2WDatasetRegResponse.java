/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

/**
 *
 * @author te0003
 */
public class S2WDatasetRegResponse {
    
    String type ="";
    boolean stored=false;
    boolean indexing=false;
    String responseStatus="";
    
    public S2WDatasetRegResponse(String type, boolean stored, boolean indexing, String errorMessage){
    
        this.type=type;
        this.stored=stored;
        this.indexing=indexing;
        this.responseStatus=errorMessage;
    
    }
    
    
}
