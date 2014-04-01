/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.old;

/**
 *
 * @author t-74
 */
public class ResourceQuery {

    String hasName = "?uri rm:hasName ";//RName.\n";
    String hasResourceID = "?uri rm:hasResourceID ";//RID.\n";
    String hasTag = "?uri rm:hasTag ";//Tag.\n";
    String hasTimeZone = "?uri rm:hasTimeZone ";//RTimeZone.\n";
    String isLinkedTo = "?uri rm:isLinkedTo ";//RLinkedToValue.\n";//rLinkedToValue
    String hasLocalLocation = "?uri rm:hasLocalLocation ";//localList.\n"; //"localList";
    String hasGlobalLocation = "?uri rm:hasGlobalLocation ";//locationList.\n";//locationList";
    String hasLatitude = "?uri rm:hasLatitude ";//RLatitude.\n"; //rLatitude
    String hasLongitude = "?uri rm:hasLongitude ";//RLongitude.\n"; //rLongitude
    String hasAltitude = "?uri rm:hasAltitude ";//RAltitude.\n"; //rAltitude
    String hasType = "?uri rm:hasType ";//RType.\n"; //RType
    String hasResourceDescriptionID = "?uri rm:hasResourceDescriptionID ";//rDescription.\n"; //rDescription
    String hasInterfaceType = "?uri rm:hasInterfaceType ";//rIntType.\n"; //RIntType
    String hasAccessInterface = "?uri rm:hasAccessInterface ";//rIntURI.\n"; //RIntURI
    String prefix = "PREFIX rm: <http://www.surrey.ac.uk/ccsr/IoT-A/ResourceModel.owl#>\n";
    String query = "SELECT *\n";
    String condition = " WHERE {\n";
    String parameters =     "?uri rm:hasName  ?name.\n"+
                            "?uri rm:hasResourceID  ?id.\n"+
                            "?uri rm:hasTag ?tag.\n"+
                            "?uri rm:hasType  ?type.\n"+
                            "?uri rm:hasGlobalLocation ?globalLoc.\n"+
                            "?uri rm:hasLocalLocation ?localLoc.\n"+
                            "?uri rm:hasInterfaceType ?ifType.\n"+
                            "?uri rm:hasAccessInterface ?accessIf.\n"+
                            "?uri rm:hasLatitude ?latitude.\n"+
                            "?uri rm:hasLongitude ?longitude.\n"+
                            "?uri rm:hasURITag ?uriTag.\n"+ ///////////////
                            "?uri rm:hasTimeZone ?timeZone.\n"+
                            "?uri rm:isLinkedTo ?linkedTo.\n"+
                            "?uri rm:hasAltitude ?altitude.\n"+
                            "?uri rm:hasResourceDescriptionID ?resDescID.\n";

    public ResourceQuery() {
    }

    public String constructQuery(Resource resource) {

        String qStatement = prefix + query + condition + parameters;

        if ((resource.getName() != null) && resource.getName().length() > 0) {
            qStatement = qStatement + hasName + "\"" + resource.getName() + "\"" + ".\n";
        }
        if ((resource.getId() != null) && resource.getId().length() > 0) {
            qStatement = qStatement + hasResourceID + "\"" + resource.getId() + "\"" + ".\n";
        }
        if ((resource.getTag() != null) && resource.getTag().length() > 0) {
            qStatement = qStatement + hasTag + "\"" + resource.getTag() + "\"" + ".\n";
        }
        if ((resource.getTimeZone() != null) && resource.getTimeZone().length() > 0) {
            qStatement = qStatement + hasTimeZone + "\"" + resource.getTimeZone() + "\"" + ".\n";
        }
        if ((resource.getLinkedToValue() != null) && resource.getLinkedToValue().length() > 0) {
            qStatement = qStatement + isLinkedTo + "\"" + resource.getLinkedToValue() + "\"" + ".\n";
        }
        if ((resource.getLocalList() != null) && (resource.getLocalList().length() > 0)) {
            qStatement = qStatement + hasLocalLocation + "\"" + resource.getLocalList() + "\"" + ".\n";
        }
        if ((resource.getLocationList() != null) && (resource.getLocationList().length() > 0)) {
            qStatement = qStatement + hasGlobalLocation + "\"" + resource.getLocationList() + "\"" + ".\n";
        }
        if ((resource.getLatitude() != null) && (resource.getLatitude().length() > 0)) {
            qStatement = qStatement + hasLatitude + "\"" + resource.getLatitude() + "\"" + ".\n";
        }
        if ((resource.getLongitude() != null) && (resource.getLongitude().length() > 0)) {
            qStatement = qStatement + hasLongitude + "\"" + resource.getLongitude() + "\"" + ".\n";
        }
        if ((resource.getAltitude() != null) && (resource.getAltitude().length() > 0)) {
            qStatement = qStatement + hasAltitude + "\"" + resource.getAltitude() + "\"" + ".\n";
        }
        if ((resource.getType() != null) && (resource.getType().length() > 0)) {
            qStatement = qStatement + hasType + "\"" + resource.getType() + "\"" + ".\n";
        }
        if ((resource.getDescription() != null) && (resource.getDescription().length() > 0)) {
            qStatement = qStatement + hasResourceDescriptionID + "\"" + resource.getDescription() + "\"" + ".\n";
        }
        if ((resource.getIntType() != null) && (resource.getIntType().length() > 0)) {
            qStatement = qStatement + hasInterfaceType + "\"" + resource.getIntType() + "\"" + ".\n";
        }
        if ((resource.getIntURI() != null) && (resource.getIntURI().length() > 0)) {
            qStatement = qStatement + hasAccessInterface + "\"" + resource.getIntURI() + "\"" + ".\n";
        }

        qStatement = qStatement + "}";
        return qStatement;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
