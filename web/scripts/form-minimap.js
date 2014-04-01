/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var elevator;
var markersArray = [];

function load() {

    var latlng = new google.maps.LatLng(51.243615,-0.588341);
    var myOptions = {
        zoom: 18,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.HYBRID
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
        
    var imageBounds = new google.maps.LatLngBounds(
        //SW point first then NE point
        new google.maps.LatLng(51.243385,-0.588725),
        new google.maps.LatLng(51.243813,-0.587955)
        );
    var ccsrMap = new google.maps.GroundOverlay(
        "/S2W/images/ccsr-map-2D-first-floor-2.png",
        imageBounds);
    ccsrMap.setMap(map);
                
    var image = '/S2W/images/res-on-map.png';
    if (document.mainform.desctype.value ==="entity"){
        image = '/S2W/images/ent-on-map.png';        
    }
    else if (document.mainform.desctype.value ==="service"){
        image = '/S2W/images/srv-on-map.png';
    }
    
    var marker = new google.maps.Marker({
        map: map,
        draggable:true,
        animation: google.maps.Animation.DROP,
        icon: image,
        position: latlng
    });
                
    
    // Create an ElevationService
    elevator = new google.maps.ElevationService();

    //                document.getElementById("lat").value = latlng.lat().toFixed(6);
    //                document.getElementById("lng").value = latlng.lng().toFixed(6);

    google.maps.event.addListener(marker, "dragend", function() {
        var point = marker.getPosition();
        map.panTo(point);
        document.getElementById("hasLatitude").value = point.lat().toFixed(6);
        document.getElementById("hasLongitude").value = point.lng().toFixed(6);
        getElevation(point);
    });
	
    markersArray.push(marker);
    google.maps.event.addListener(map, 'click', function(event) {
        //marker.setMap(null);
        clearOverlays();
        addMarker(map, event.latLng, image);
    });
}
            
            

function addMarker(map, location,image) {
                
    var marker = new google.maps.Marker({
        position: location,
        map: map,
        draggable:true,
        //animation: google.maps.Animation.DROP,
        icon: image

    });
                
    var point = marker.getPosition();
    map.panTo(point);
    document.getElementById("hasLatitude").value = point.lat().toFixed(6);
    document.getElementById("hasLongitude").value = point.lng().toFixed(6);
    getElevation(point);
                
    google.maps.event.addListener(marker, "dragend", function() {
        var point = marker.getPosition();
        map.panTo(point);
        document.getElementById("hasLatitude").value = point.lat().toFixed(6);
        document.getElementById("hasLongitude").value = point.lng().toFixed(6);
        getElevation(point);
    });
                
    markersArray.push(marker);
}
            
            
function getElevation(location) {
                
    var locations = [];

    // Retrieve the clicked location and push it on the array
    var clickedLocation = location;
    locations.push(clickedLocation);

    // Create a LocationElevationRequest object using the array's one value
    var positionalRequest = {
        'locations': locations
    }

    // Initiate the location request
    elevator.getElevationForLocations(positionalRequest, function(results, status) {
        if (status == google.maps.ElevationStatus.OK) {

            // Retrieve the first result
            if (results[0]) {          
                document.getElementById("hasAltitude").value = results[0].elevation.toFixed(6);;        
            } else {
                alert("No results found");
            }
        } else {
            alert("Elevation service failed due to: " + status);
        }
    });
            
}
            

function clearOverlays() {
    if (markersArray) {
        for (i in markersArray) {
            markersArray[i].setMap(null);
        }
    }
}

function SelectAll(id)
{
    document.getElementById(id).focus();
    document.getElementById(id).select();
}


