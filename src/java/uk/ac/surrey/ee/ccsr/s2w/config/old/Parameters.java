/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.surrey.ee.ccsr.s2w.config.old;

/**
 *
 * @author Payam
 */
public class Parameters {

    // XSL transformation files (form to RDF)
    // changed from xsltFile
    public String xsltResourceFile= "/repo/ResourceOnto.xsl";
    public String xsltEntityFile= "/repo/EntityOnto.xsl";
    public String xsltServiceFile= "/repo/ServiceOnto.xsl";
    public String ttlFile = "/repo/sdb.ttl";

    // OWL name spaces
    // changed from ontoURI
    public String ontoResourceURI= "http://www.surrey.ac.uk/ccsr/IoT-A/ResourceModel.owl#";
    public String ontoEntityURI= "http://www.surrey.ac.uk/ccsr/IoT-A/EntityModel.owl#";
    public String ontoServiceURI= "http://www.surrey.ac.uk/ccsr/IoT-A/ServiceModel.owl#";

    // DB settings
    // changed from dbURL
    public String dbResourceUrl = "jdbc:mysql://localhost:3306/resourcedb";
    public String dbEntityUrl = "jdbc:mysql://localhost:3306/entitydb";
    public String dbServiceUrl = "jdbc:mysql://localhost:3306/servicedb";

    public String driverClass = "com.mysql.jdbc.Driver";
    public String dbuser = "root";
    public String dbpass = "root";
    public String connURL = "jdbc:mysql://localhost:3306/resourcedb";
    
    //RESTful i/f messages
    public String invalidRepoMsg = "invalid repository selection";
    public String successPubMsg = "description(s) published, thanks!";
    public String successUnpubMsg = "unpublished, thanks";
    public String unSuccessPubMsg = "failed to publish";
    public String unSuccessUnpubMsg = "failed to delete";
    public String invalidIdMsg = "No object found by this ID";
    public String successUpdMsg = "object updated, thanks!";
    public String unSuccessUpdMsg = "object update failed";
    
//    // local ontology
    //public String localOnto = "http://localhost/S2W/repo/DemoLocationModel.owl";
    public String localOnto = "/S2W/repo/DemoLocationModel.owl";
//
//    // virtual sensors
//    public String vsensors = "/repo/vsensors/vsensors.xml";
    
}
