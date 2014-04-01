/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.restlet.response;

/**
 *
 * @author te0003
 */
public class S2W_ETStatusResponse {
    
    
    boolean iota_resource_index_created=false;
    boolean iota_entity_index_created=false;
    boolean iota_service_index_created=false;
    
    public S2W_ETStatusResponse( boolean iota_resource, boolean iota_entity, boolean iota_service){
    
        this.iota_resource_index_created=iota_resource;
        this.iota_entity_index_created=iota_entity;
        this.iota_service_index_created=iota_service;        
    }
    
    
}
