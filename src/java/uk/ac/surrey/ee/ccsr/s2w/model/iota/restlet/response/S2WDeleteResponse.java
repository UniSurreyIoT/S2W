/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

/**
 *
 * @author te0003
 */
public class S2WDeleteResponse {
    
    String id ="";
    String type ="";
    boolean deleted=false;
    String responseStatus="";
    
    public S2WDeleteResponse(String id, String type, boolean deleted, String errorMessage){
    
        this.id=id;
        this.type=type;
        this.deleted=deleted;
        this.responseStatus=errorMessage;
    
    }
    
    
}
