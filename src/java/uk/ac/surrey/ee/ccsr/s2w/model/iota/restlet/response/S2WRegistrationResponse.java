/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

/**
 *
 * @author te0003
 */
public class S2WRegistrationResponse {
    
    String id ="";
    String type ="";
    boolean stored=false;
    boolean indexed=false;
    String association ="";
    String responseStatus="";
    
    public S2WRegistrationResponse(String id, String type, boolean stored, boolean indexed, String association, String errorMessage){
    
        this.id=id;
        this.type=type;
        this.stored=stored;
        this.indexed=indexed;
        this.association=association;
        this.responseStatus=errorMessage;
    
    }
    
    
}
