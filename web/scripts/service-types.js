/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
function getAreaDimensions(selection) {
    
    $('#serviceAreaDimensions').empty();

    var dimensions_rect = ["SW_latitude", "SW_longitude", "NE_longitude", "NE_latitude"];
    var dimensions_circ = ["centre_latitude", "centre_longitude", "radius"];

    if (selection === "http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#RectangularArea") {

        for (i = 0; i < 4; i++) {

            var row = $('<tr>');
            var cell_title = $('<td>');
            var cell_value = $('<td>');
            var input = $('<input>');

            input.attr("id", dimensions_rect[i]);
            input.attr("name", dimensions_rect[i]);
            input.attr('type', 'text');

            row.append(cell_title.html(dimensions_rect[i]));
            row.append(cell_value.html(input));

            $('#serviceAreaDimensions').append(row);
        }

    }
    else if (selection === "http://www.surrey.ac.uk/ccsr/ontologies/ServiceModel.owl#CircularArea") {

        for (i = 0; i < 3; i++) {

            var row = $('<tr>');
            var cell_title = $('<td>');
            var cell_value = $('<td>');
            var input = $('<input>');

            input.attr("id", dimensions_circ[i]);
            input.attr("name", dimensions_circ[i]);
            input.attr('type', 'text');

            row.append(cell_title.html(dimensions_circ[i]));
            row.append(cell_value.html(input));

            $('#serviceAreaDimensions').append(row);
        }

    }

}

