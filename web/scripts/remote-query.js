/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//var ip = "http://localhost:8080/S2W/";
//var ip = "http://131.227.88.98:8080/S2W/";
//var ip = "http://iotserver3.ee.surrey.ac.uk:8080/S2W/";
var ip = "http://"+ window.location.hostname +":"+ window.document.location.port+ "/S2W/";

var markersArray = [];

function ajaxFunctionType() {

    var xmlHttp;


    try { // Firefox, Opera 8.0+, Safari
        xmlHttp=new XMLHttpRequest();
        var imgStr= "<img style='width: 32px; height: 32px;'alt='processing' src='/S2W/images/processing.gif'>";
        document.getElementById("TypeImage").innerHTML= imgStr;
    }
    catch (e) { // Internet Explorer
        try {
            xmlHttp=new ActiveXObject("MSXML2.XMLHTTP.3.0"); //("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                alert("Your browser does not support AJAX!");
                return false;
            }
        }
    }
    xmlHttp.onreadystatechange=function() {
        //document.getElementById("dbpedialocation").innerHTML="processing...";
        if(xmlHttp.readyState==4) {
            document.getElementById("TypeImage").innerHTML= "";

            var slct = document.mainform.typeList;
            for (var k=slct.length; k>0; k--) {
                slct.removeChild(slct[k-1]);
            }
            //alert (xmlHttp.responseText);
            var eList = xmlHttp.responseXML.getElementsByTagName("entity");
            var lList = xmlHttp.responseXML.getElementsByTagName("label");
            if (eList.length > 0) {
                for (var i=0; i < eList.length; i++){
                    var enode = eList[i];
                    var lnode = lList[i]

                    var etag = enode.firstChild.nodeValue;
                    var ltag = lnode.firstChild.nodeValue;
                    //alert (etag, ltag);
                    //document.getElementById("hasGlobalLocation").add(new Option(place,place));
                    try{
                        var select = document.mainform.typeList;
                        select.options[i] = new Option(ltag + "   |  <" + etag + ">", etag);
                    }
                    catch(e){ //in IE
                    }
                //alert("2");
                //document.getElementById("dbpedialocation2").innerHTML= place;
                }
            }
        }
    }

    var url = ip+ "DBPediaTagSuggestion";
    //url = url + "?query=" + document.mainform.typeword.value + " " + "sensor";
    url = url + "?query=" + document.mainform.typeword.value + "sensor";
    url = url + "&limit=" + document.mainform.limit.value;
    //url = url + "&topic=" + topic;
    xmlHttp.open("GET",url,true);
    //alert (url);
    xmlHttp.send(null);
}



//***************************************
// Follwing is the Local Location function
//****************************************
function ajaxFunctionLocalLocation() {

    var xmlHttp;

    try { // Firefox, Opera 8.0+, Safari
        xmlHttp=new XMLHttpRequest();
        var imgStr= "<img style='width: 32px; height: 32px;'alt='processing' src='/S2W/images/processing.gif'>";
        document.getElementById("LocalLocationImage").innerHTML= imgStr;
    }
    catch (e) { // Internet Explorer
        try {
            xmlHttp=new ActiveXObject("MSXML2.XMLHTTP.3.0"); //("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                alert("Your browser does not support AJAX!");
                return false;
            }
        }
    }
    xmlHttp.onreadystatechange=function() {
        //document.getElementById("dbpedialocation").innerHTML="processing...";
        if(xmlHttp.readyState==4) {
            document.getElementById("LocalLocationImage").innerHTML= "";

            var slct = document.mainform.hasLocalLocation;
            for (var k=slct.length; k>0; k--) {
                slct.removeChild(slct[k-1]);
            }
            //alert (xmlHttp.responseText);
            var eList = xmlHttp.responseXML.getElementsByTagName("entity");
            var lList = xmlHttp.responseXML.getElementsByTagName("label");
            
            var select = document.mainform.hasLocalLocation;
                        //alert (ltag + "-----" + etag);
                        select.options[0] = new Option("");

            if (eList.length > 0) {
                for (var i=0; i < eList.length; i++){
                    var enode = eList[i];
                    var lnode = lList[i]

                    var etag = enode.firstChild.nodeValue;
                    var ltag = lnode.firstChild.nodeValue;
                    
                    try{
                       // var select = document.mainform.hasLocalLocation;
                        //alert (ltag + "-----" + etag);
                        select.options[i+1] = new Option(ltag + "   |  <" + etag + ">", etag);
                    }
                    catch(e){ //in IE
                        alert("error");
                    }
                //alert("2");
                //document.getElementById("dbpedialocation2").innerHTML= place;
                }
            }
        }
    }

    var url = ip+"LocalQuery";
    url = url + "?query=" + document.mainform.locallocationword.value;
    url = url + "&limit=" + document.mainform.limit.value;
    xmlHttp.open("GET",url,true);
    //alert (url);
    xmlHttp.send(null);
}


//*******************************************
//the follwing is a dbpedia location function
//*******************************************
function ajaxFunctionDBPediaLocation() {

    var xmlHttp;

    try { // Firefox, Opera 8.0+, Safari
        xmlHttp=new XMLHttpRequest();
        var imgStr= "<img style='width: 32px; height: 32px;'alt='processing' src='/S2W/images/processing.gif'>";
        document.getElementById("DBPediaLocationImage").innerHTML= imgStr;
    }
    catch (e) { // Internet Explorer
        try {
            xmlHttp=new ActiveXObject("MSXML2.XMLHTTP.3.0"); //("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                alert("Your browser does not support AJAX!");
                return false;
            }
        }
    }
    xmlHttp.onreadystatechange=function() {
        //document.getElementById("dbpedialocation").innerHTML="processing...";
        if(xmlHttp.readyState==4) {
            document.getElementById("DBPediaLocationImage").innerHTML= "";

            var slct = document.mainform.hasGlobalLocation;
            for (var k=slct.length; k>0; k--) {
                slct.removeChild(slct[k-1]);
            }
            //alert (xmlHttp.responseText);
            var eList = xmlHttp.responseXML.getElementsByTagName("entity");
            var lList = xmlHttp.responseXML.getElementsByTagName("label");
            if (eList.length > 0) {
                for (var i=0; i < eList.length; i++){
                    var enode = eList[i];
                    var lnode = lList[i]

                    var etag = enode.firstChild.nodeValue;
                    var ltag = lnode.firstChild.nodeValue;
                    //alert (etag, ltag);
                    //document.getElementById("hasGlobalLocation").add(new Option(place,place));
                    try{
                        var select = document.mainform.hasGlobalLocation;
                        select.options[i] = new Option(ltag + "   |  <" + etag + ">", etag);
                    }
                    catch(e){ //in IE
                    }
                //alert("2");
                //document.getElementById("dbpedialocation2").innerHTML= place;
                }
            }
        }
    }
    var len = document.mainform.locationTopicList.length;
    for (var j = 0; j < len; j++) {
        if (document.mainform.locationTopicList[j].selected) {
            var topic = document.mainform.locationTopicList[j].value;
        }
    }

    var url = ip+"DBPediaTagSuggestion";
    url = url + "?query=" + document.mainform.dbpedialocationword.value;
    url = url + "&limit=" + document.mainform.limit.value;
    url = url + "&topic=" + topic;
    xmlHttp.open("GET",url,true);
    //alert (url);
    xmlHttp.send(null);
}


//**********************************************
//  the follwing  function is for retrieving General Tags from DBPedia
//************************************************
function ajaxFunctionDBPediaTag() {

    var xmlHttp;

    try { // Firefox, Opera 8.0+, Safari
        xmlHttp=new XMLHttpRequest();
        var imgStr= "<img style='width: 32px; height: 32px;'alt='processing' src='/S2W/images/processing.gif'>";
        document.getElementById("DBPediaTagImage").innerHTML= imgStr;
    }
    catch (e) { // Internet Explorer
        try {
            xmlHttp=new ActiveXObject("MSXML2.XMLHTTP.3.0"); //("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
                alert("Your browser does not support AJAX!");
                return false;
            }
        }
    }
    xmlHttp.onreadystatechange=function() {
        //document.getElementById("dbpedialocation").innerHTML="processing...";
        if(xmlHttp.readyState==4) {
            document.getElementById("DBPediaTagImage").innerHTML= "";

            var slct = document.mainform.TagList;
            //var slct = document.getElementById ("TagList");
            for (var k=slct.length; k>0; k--) {
                slct.removeChild(slct[k-1]);
            }
            //alert (xmlHttp.responseText);
            var eList = xmlHttp.responseXML.getElementsByTagName("entity");
            var lList = xmlHttp.responseXML.getElementsByTagName("label");
            if (eList.length > 0) {
                for (var i=0; i < eList.length; i++){
                    var enode = eList[i];
                    var lnode = lList[i];

                    var etag = enode.firstChild.nodeValue;
                    var ltag = lnode.firstChild.nodeValue;
                    //alert (etag, ltag);
                    //document.getElementById("hasGlobalLocation").add(new Option(place,place));
                    try{
                        var select = document.mainform.TagList;
                        //var select = document.getElementById ("TagList");
                        select.options[i] = new Option(ltag + "   |  <" + etag + ">", etag);
                    }
                    catch(e){ //in IE
                    }
                //alert("2");
                //document.getElementById("dbpedialocation2").innerHTML= place;
                }
            }
        }
    }
    var len = document.mainform.tagTopicList.length;
    for (var j = 0; j < len; j++) {
        if (document.mainform.tagTopicList[j].selected) {
            var topic = document.mainform.tagTopicList[j].value;
        }
    }

    var url = ip + "DBPediaTagSuggestion";
    url = url + "?query=" + document.mainform.tagword.value;
    url = url + "&limit=" + document.mainform.limit.value;
    url = url + "&topic=" + topic;
    xmlHttp.open("GET",url,true);
    //alert (url);
    xmlHttp.send(null);
}
