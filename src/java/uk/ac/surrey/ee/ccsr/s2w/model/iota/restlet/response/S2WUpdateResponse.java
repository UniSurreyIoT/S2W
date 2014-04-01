/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

/**
 *
 * @author te0003
 */
public class S2WUpdateResponse {
    
    String id ="";
    String type ="";
    boolean stored=false;
    String association ="";
    String responseStatus="";
    
    public S2WUpdateResponse(String id, String type, boolean stored, String association, String errorMessage){
    
        this.id=id;
        this.type=type;
        this.stored=stored;
        this.association=association;
        this.responseStatus=errorMessage;
    
    }
    
    
}
