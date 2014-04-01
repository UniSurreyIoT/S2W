/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var submitURL = "/S2W/IotaDiscoverWebForm";

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

function LoadPage1() {
    $('#siteloader').hide();
    $('#siteloader').load("/S2W/discover/resource-v2.html", {}, function() {
        $(this).fadeIn('normal');
        initializeForm();
        $.getScript("/S2W/scripts/form-minimap.js", function() {
            load();     //loads map       
        });
    });
}

function LoadPage2() {
    $('#siteloader').hide();
    $('#siteloader').load("/S2W/discover/entity-v2.html", {}, function() {
        $(this).fadeIn('normal');
        initializeForm();
        $.getScript("/S2W/scripts/form-minimap.js", function() {
            load();     //loads map       
        });
    });
}

function LoadPage3() {
    $('#siteloader').hide();
    $('#siteloader').load("/S2W/discover/service-v2.html", {}, function() {
        $(this).fadeIn('normal');
        initializeForm();
        $.getScript("/S2W/scripts/form-minimap.js", function() {
            load();     //loads map       
        });
    });
}

function initializeForm() {

    readyForSubmit();
}


function readyForSubmit() {

    $(document).on("click", "#submit2", function(e) {
        e.preventDefault();
        $('#resultFormats').empty();
        $('#resulttextbox').empty();
        var form = $('#mainform');

        if (form[0].checkValidity()) {

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

            var req = $.ajax({
                type: "POST",
                url: submitURL,
                accept: "text/plain",
                processData: false,
                contentType: form.attr('enctype'),
                data: form.serialize()
            });
            req.done(function(response) {
//                alert(response);
                var searchResults = jQuery.parseJSON(response);
//                alert(searchResults);
                $('#resultFormats').append("<p>Select a format:</p>");
                
                var searchResultsSize = searchResults.length;
                
                
                for(var i = 0; i < searchResultsSize; i++) {
                    $('#resultFormats').append("Rank #"+(i+1)+": ");
                    
                $.each(searchResults[i], function(key, value) {
                    // alert(key + ' -> ' + value);

                    var button = $('<button>', {
                        text: key, //set text 1 to 10
                        id: key,
                        click: function() {

                            //alert(value);                                     
                            getDescription(value);
                        }
                    });
                    
                    $('#resultFormats').append(button);
                    
                });
                $('#resultFormats').append("<br />");
                }
                
                
                $('#resultFormats').append('<img id="processing" src="/S2W/images/processing.gif" />');
                $('#processing').hide();

            });
            req.fail(function(jqXHR, textStatus, errorThrown) {
                //alert("Request failed: " + jqXHR.responseText);
                $('<iframe />', {
                    name: 'myFrame',
                    id: 'myFrame',
                    frameborder: 1,
                    width: '60%',
                    srcdoc: jqXHR.responseText
                }).appendTo('#resultFormats');
                //$('#resultFormats').html("object with ID \""+$('#hasID').val()+"\" already exists!, please use another ID");
            });

            $("#accordion").accordion('activate', 1);
            $("#section1").focus();
        }
        else {
            //console.log("please enter all fields");
            alert("please enter at least: ID, Name, Longitude and Latitude");
        }
    });
}

function getDescription(value) {

    $('#resulttextbox').empty();
    $.ajax({
        type: "GET",
        accept: "text/plain",
        dataType: "text",
        url: value //name of servlet                
    }).done(function(result) {
        $('#resulttextbox').val(result);
    });

}

