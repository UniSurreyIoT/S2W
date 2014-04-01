/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.old;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 *
 * @author t-74
 */
public class Resource {

    String name = "";
    String id = "";
    String tag = "";
    String tagword = "";
    String timeZone = "";
    String linkedToValue = "";
    String localList = "";
    String locationList = "";
    String latitude = "";
    String longitude = "";
    String altitude = "";
    String type = "";
    String description = "";
    String intType = "";
    String intURI = "";

    public Resource() {
    }

    public Resource(String rID) {
        id = rID;
    }

    public Resource(HttpServletRequest request) {

        name = request.getParameter("RName");
        id = request.getParameter("RID");
        tag = request.getParameter("Tag");
        tagword = request.getParameter("tagword");
        timeZone = request.getParameter("RTimeZone");
        linkedToValue = request.getParameter("RLinkedToValue");
        localList = request.getParameter("localList");
        locationList = request.getParameter("locationList");
        latitude = request.getParameter("RLatitude");
        longitude = request.getParameter("RLongitude");
        altitude = request.getParameter("RAltitude");
        type = request.getParameter("RType");
        description = request.getParameter("RDescription");
        intType = request.getParameter("RIntType");
        intURI = request.getParameter("RIntURI");

    }

    public ArrayList getElementNames() {

        ArrayList al = new ArrayList();
        al.add("RName");
        al.add("RID");
        al.add("Tag");
        al.add("TagList");
        al.add("RTimeZone");
        al.add("RLinkedToValue");
        al.add("localList");
        al.add("locationList");
        al.add("RLatitude");
        al.add("RLongitude");
        al.add("RAltitude");
        al.add("RType");
        al.add("RDescription");
        al.add("RIntType");
        al.add("RIntURI");

        return al;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getIntURI() {
        return intURI;
    }

    public void setIntURI(String intURI) {
        this.intURI = intURI;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLinkedToValue() {
        return linkedToValue;
    }

    public void setLinkedToValue(String linkedToValue) {
        this.linkedToValue = linkedToValue;
    }

    public String getLocalList() {
        return localList;
    }

    public void setLocalList(String localList) {
        this.localList = localList;
    }

    public String getLocationList() {
        return locationList;
    }

    public void setLocationList(String locationList) {
        this.locationList = locationList;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagword() {
        return tagword;
    }

    public void setTagword(String tagword) {
        this.tagword = tagword;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
