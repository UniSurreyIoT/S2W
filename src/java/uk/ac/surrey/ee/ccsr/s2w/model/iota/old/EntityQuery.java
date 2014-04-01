/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.old;

/**
 *
 * @author t-74
 */
public class EntityQuery {

    String hasName = "?uri em:hasName ";//RName.\n";
    String hasEntityID = "?uri em:hasentityID ";//RID.\n";
    String hasTag = "?uri em:hasTag ";//Tag.\n";
    String hasTimeZone = "?uri em:hasTimeZone ";//RTimeZone.\n";
    String isLinkedTo = "?uri em:isLinkedTo ";//RLinkedToValue.\n";//rLinkedToValue
    String hasLocalLocation = "?uri em:hasLocalLocation ";//localList.\n"; //"localList";
    String hasGlobalLocation = "?uri em:hasGlobalLocation ";//locationList.\n";//locationList";
    String hasLatitude = "?uri em:hasLatitude ";//RLatitude.\n"; //rLatitude
    String hasLongitude = "?uri em:hasLongitude ";//RLongitude.\n"; //rLongitude
    String hasAltitude = "?uri em:hasAltitude ";//RAltitude.\n"; //rAltitude
    String hasDescription = "?uri em:hasDescription ";//rDescription.\n"; //rDescription
    String hasURITag = "?uri em:hasURITag ";
    String prefix = "PREFIX em: <http://www.surrey.ac.uk/ccsr/IoT-A/EntityModel.owl#> ";
    String query = "SELECT *\n";
    String condition = " WHERE {\n";
    String parameters =     "?uri em:hasName ?name.\n"+
                            "?uri em:hasentityID ?id.\n"+
                            "?uri em:hasTag ?tag.\n"+
                            "?uri em:isLinkedTo ?linkedTo.\n"+
                            "?uri em:hasGlobalLocation ?globalLoc.\n"+
                            "?uri em:hasLocalLocation ?localLoc.\n"+
                            "?uri em:hasLatitude ?latitude.\n"+
                            "?uri em:hasLongitude ?longitude.\n"+
                            "?uri em:hasURITag ?uriTag.\n"+
                            "?uri em:hasTimeZone ?timeZone.\n"+
                            "?uri em:hasAltitude ?altitude.\n"+
                            "?uri em:hasDescription ?entDesc.\n";

    public EntityQuery() {
    }

    public String constructQuery(Entity entity) {

        String qStatement = prefix + query + condition + parameters;

        if ((entity.getName() != null) && entity.getName().length() > 0) {
            qStatement = qStatement + hasName + "\"" + entity.getName() + "\"" + ".\n";
        }
        if ((entity.getId() != null) && entity.getId().length() > 0) {
            qStatement = qStatement + hasEntityID + "\"" + entity.getId() + "\"" + ".\n";
        }
        if ((entity.getTag() != null) && entity.getTag().length() > 0) {
            qStatement = qStatement + hasTag + "\"" + entity.getTag() + "\"" + ".\n";
        }
        if ((entity.getTimeZone() != null) && entity.getTimeZone().length() > 0) {
            qStatement = qStatement + hasTimeZone + "\"" + entity.getTimeZone() + "\"" + ".\n";
        }
        if ((entity.getLinkedToValue() != null) && entity.getLinkedToValue().length() > 0) {
            qStatement = qStatement + isLinkedTo + "\"" + entity.getLinkedToValue() + "\"" + ".\n";
        }
        if ((entity.getLocalList() != null) && (entity.getLocalList().length() > 0)) {
            qStatement = qStatement + hasLocalLocation + "\"" + entity.getLocalList() + "\"" + ".\n";
        }
        if ((entity.getLocationList() != null) && (entity.getLocationList().length() > 0)) {
            qStatement = qStatement + hasGlobalLocation + "\"" + entity.getLocationList() + "\"" + ".\n";
        }
        if ((entity.getLatitude() != null) && (entity.getLatitude().length() > 0)) {
            qStatement = qStatement + hasLatitude + "\"" + entity.getLatitude() + "\"" + ".\n";
        }
        if ((entity.getLongitude() != null) && (entity.getLongitude().length() > 0)) {
            qStatement = qStatement + hasLongitude + "\"" + entity.getLongitude() + "\"" + ".\n";
        }
        if ((entity.getAltitude() != null) && (entity.getAltitude().length() > 0)) {
            qStatement = qStatement + hasAltitude + "\"" + entity.getAltitude() + "\"" + ".\n";
        }

        if ((entity.getDescription() != null) && (entity.getDescription().length() > 0)) {
            qStatement = qStatement + hasDescription + "\"" + entity.getDescription() + "\"" + ".\n";
        }
        if ((entity.getTagList() != null) && (entity.getTagList().length() > 0)) {
            qStatement = qStatement + hasURITag + "\"" + entity.getTagList() + "\"" + ".\n";
        }

        qStatement = qStatement + "}";
        return qStatement;
    }
}
