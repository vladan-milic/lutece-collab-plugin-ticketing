function loadModalContent(modalSelector){
	$(modalSelector).on("show.bs.modal", function(e) {
	    var link = $(e.relatedTarget);
		var attrActionId = $(this).attr("aria-action-id");
		var linkActionId = link.attr("data-action-id");
		if ( typeof attrActionId === typeof undefined || attrActionId === false )
		{
			attrActionId = "";
		}
		if( attrActionId == "" || linkActionId != attrActionId )
		{
			$(this).find(".modal-body").load( link.attr("href") );
			$(this).find(".modal-title").text( link.data('title') );
			if ( typeof linkActionId !== typeof undefined && linkActionId !== false )
			{
				$(this).attr( "aria-action-id", linkActionId );
			}
		}
	});
}
