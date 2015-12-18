    $(document).ready(function() {
            lutece_ticket_tree("#id_ticket_type", "#id_ticket_domain", "#id_ticket_category");
			var categorySelect = document.getElementById("id_ticket_category");
			var catValue = categorySelect.options[categorySelect.selectedIndex].value;
			if (catValue >= 0 ) {
			       $.ajax({
						url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+catValue ,
						type: "GET",
						dataType : "html",
						success: function( response ) {
							$('#generic_attributes').replaceWith('<div  class="form-group" id="generic_attributes">' + response + '</div>');
						}
						});
			}
    });
	var categorySelect = document.getElementById("id_ticket_category");
	categorySelect.onchange=function(){
			var catValue = categorySelect.options[categorySelect.selectedIndex].value;
            $.ajax({
            url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+catValue ,
            type: "GET",
            dataType : "html",
            success: function( response ) {
				$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
            }
            });
	};
