/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var templateURL = "/S2W/IotaTemplates";
var queryUrlPrefix = "/S2W/repository/query/sparql/iot-a";

$(function() {//document ready
    var icons = {
        header: "ui-icon-circle-arrow-e",
        activeHeader: "ui-icon-circle-arrow-s"
    };

    $("#accordion").accordion({
        icons: icons,
        collapsible: true,
        heightStyle: "content"
    });

    $(document).ready(function() {
        $("#accordion").accordion('activate', 0);
        getTemplate();
    });
});

$(document).on("click", "input[name=desctype]", function(e) {
    
    //e.preventDefault(); //will prevent "submit"    
    getTemplate();
    
});

function getTemplate(){
    
    $('#getTemplate').empty();
    var descType = $("input[name=desctype]:checked").val();

        $('#getTemplate').append('<img id="processing1" src="/S2W/images/processing.gif" />');
        $('#processing1').hide();

        jQuery.ajaxSetup({
            beforeSend: function() {
                $('#processing1').show();
            },
            complete: function() {
                $('#processing1').hide();
            },
            success: function() {
            }
        });

        var request = $.ajax({
            async: true,
            type: "GET",
            url: templateURL,
            data: {descType: descType, methodType: "query"}
        });
        request.done(function(result) {
            //alert(response);
            $('#querytextbox').val(result);


        });
        request.fail(function(jqXHR, textStatus) {
            alert("Request failed: " + textStatus);
        });
}



$(document).on("click", "#submit2", function(e) {

    e.preventDefault(); //will prevent "submit"
    $('#resultFormats').empty();
    $('#resulttextbox').empty();

    var queryText = $('#querytextbox').val();
    var descType = $("input[name=desctype]:checked").val();
    var format = $("input[name=resultFormat]:checked").val();

    var query_url = queryUrlPrefix + "/" + descType;

    $('#resultFormats').append('<img id="processing" src="/S2W/images/processing.gif" />');
    $('#processing').hide();

    jQuery.ajaxSetup({
        //headers: {accepts: "text/plain; charset=utf-8", "content-type": "text/plain; charset=utf-8" },
        accepts: "text/plain",
        contentType: "text/plain",
        beforeSend: function() {
            $('#processing').show();
        },
        complete: function() {
            $('#processing').hide();
        },
        success: function() {
        }
    });

    var request = $.ajax({        
        async: true,
        type: "GET",        
        url: query_url,
        contentType: "text/plain",
        dataType: "text",
        data: {sparql: queryText, resultFormat: format}
    });

    request.done(function(result) {
        //alert(result);

        $("#accordion").accordion('activate', 1);
        $("#section1").focus();
        $('#resulttextbox').val(result);


    });
    request.fail(function(jqXHR, textStatus) {
        alert("Request failed: " + textStatus);
    });






});



