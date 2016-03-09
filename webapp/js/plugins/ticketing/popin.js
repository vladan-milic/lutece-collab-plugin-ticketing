function loadModalContent(modalSelector){
	$(modalSelector).on("show.bs.modal", function(e) {
	    var link = $(e.relatedTarget);
	    $(this).find(".modal-body").load(link.attr("href"));
	    $(this).find(".modal-title").text( link.data('title') );
	});
}
