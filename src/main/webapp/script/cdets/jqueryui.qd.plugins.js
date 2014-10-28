;(function ($) {
    $.fn.attachAutoComplement = function(arr,callback,holder){
        if(!this.is('input')){
            return;
        }
        this.attr('placeHolder',holder);
//        console.log(arr);
        this.autocomplete({
            minLength: 0,
            source: arr,
            focus: function( event, ui ) {
                $(this).val(ui["item"][0]);
                return false;
            },
            select: function( event, ui ) {
                callback(ui["item"][0]);
                return false;
            }
        }).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
            return $( "<li>" )
                .append( "<a style='font-size:13px'>" + item[0] + "<span style='width:15px;font-width:border;color:blue;'>(" + item[1] + ")</span></a>" )
                .appendTo( ul );
        };
    }

}(jQuery))