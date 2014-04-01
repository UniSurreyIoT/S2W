/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var lookup_url_prefix = "/S2W/repository/lookup/iot-a";

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
    });
});

$(document).on("click", "#submit2", function(e) {

    e.preventDefault(); //will prevent "submit"
    $('#resultFormats').empty();
    $('#resulttextbox').empty();

    var descId = $('#hasID').val();
    var descType = $("input[name=desctype]:checked").val();
    var format = $("input[name=resultFormat]:checked").val();

    var lookup_url = lookup_url_prefix + "/" + descType + "/" + descId;

    $('#resultFormats').append('<img id="processing" src="/S2W/images/processing.gif" />');
    $('#processing').hide();

    jQuery.ajaxSetup({
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
        accept: "text/plain",
        url: lookup_url,
        dataType: "text",
        data: {resultFormat: format}
    });
    request.done(function(result) {
        //alert(response);


        $("#accordion").accordion('activate', 1);
        $("#section1").focus();
        $('#resulttextbox').val(result);


    });
    request.fail(function(jqXHR, textStatus) {
        alert("Request failed: " + textStatus);
    });

});