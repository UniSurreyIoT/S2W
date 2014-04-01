/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function(){
    $(function() {
        $('div.ff_option_class').hide(); // Hide divs
        $('input.ff_button_option_class:checked').each(function() { // Show for checked option
            $('#ff_option_' + $(this).val()).fadeIn('normal');
        });
        $('input.ff_button_option_class').click(function() { // When clicked
            $('div.ff_option_class').hide(); // Hide others
            $('#ff_option_' + $(this).val()).fadeIn('normal'); // Show associated one
            $.getScript("/S2W/scripts/form-minimap.js", function(){                
            // here you can use anything you defined in the loaded script
            load();

            });
        });
    });
});

