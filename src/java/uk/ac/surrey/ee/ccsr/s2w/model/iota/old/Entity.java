/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.surrey.ee.ccsr.s2w.model.iota.old;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author t-74
 */
public class Entity {

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
    String description = "";
    String tagList = "";

    public Entity() {
    }

    public Entity(String eID) {
        id = eID;
    }

    public Entity(HttpServletRequest request) {

        name = request.getParameter("EName");
        id = request.getParameter("EID");
        tag = request.getParameter("Tag");
        tagword = request.getParameter("tagword");
        timeZone = request.getParameter("ETimeZone");
        linkedToValue = request.getParameter("ELinkedToValue");
        localList = request.getParameter("localList");
        locationList = request.getParameter("locationList");
        latitude = request.getParameter("ELatitude");
        longitude = request.getParameter("ELongitude");
        altitude = request.getParameter("EAltitude");
        description = request.getParameter("EDescription");
        tagList = request.getParameter("taglist");
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

    public String getTagList() {
        return tagList;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
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

    public ArrayList getElementNames() {

        ArrayList al = new ArrayList();
        al.add("EName");
        al.add("EID");
        al.add("Tag");
        al.add("TagList");
        al.add("ETimeZone");
        al.add("ELinkedToValue");
        al.add("localList");
        al.add("locationList");
        al.add("ELatitude");
        al.add("ELongitude");
        al.add("EAltitude");
        al.add("EDescription");
        
        return al;


    }
}
