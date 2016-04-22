function redirectOnClick( element ){
	if ( $(element).attr("data-url") != undefined ){
	    var form = $('<form>', 
	    	{ 'action':$(element).attr("data-url"),
	    	'method':'post'}
       );
       form.appendTo('body');
       form.submit().remove();
    }
}