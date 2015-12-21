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
            $.ajax({
            url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue(),
            type: "GET",
            dataType : "html",
            success: function( response ) {
				$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
            }
            });
	};
	
	var domainSelect = document.getElementById("id_ticket_domain");
	domainSelect.onchange=function(){
            $.ajax({
            url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue()  ,
            type: "GET",
            dataType : "html",
            success: function( response ) {
				$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
            }
            });
	};

	var typeSelect = document.getElementById("id_ticket_type");
	typeSelect.onchange=function(){
            $.ajax({
            url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue()  ,
            type: "GET",
            dataType : "html",
            success: function( response ) {
				$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
            }
            });
	};

	function getSelectedCategoryValue()  {
		var categorySelect = document.getElementById("id_ticket_category");
		var catValue = -1;	
		if(categorySelect.options[categorySelect.selectedIndex] != undefined) 
		{
			catValue = categorySelect.options[categorySelect.selectedIndex].value;
		}
		return catValue ;
	}
	