// Turns the 3 combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(id_type, id_domain, id_category) {
    var base = $('head base').attr('href');
    $.getJSON( base + "rest/ticketing/categories", function( data ) {
        var types_map = {};
        for (var i = 0; i<data.types.length; i++) {
            var type = data.types[i];
            types_map[type.id] = type;
            type.domains_map = {};
            for (var j = 0; j<type.domains.length; j++) {
                var domain = type.domains[j];
                type.domains_map[domain.id] = domain;
                domain.categories_map = {};
                for (var k = 0; k<domain.categories; k++) {
                    var category = domain.categories[k];
                    domain.categories_map[category.id] = category;
                }
            }
        }

        var change_ticket_type = function() {
            change_ticket_type_impl();
        }
        var change_ticket_type_impl = function(initial_domain_id, initial_category_id) {
            clear_domain_combo();
            clear_category_combo();
            var type_id = $(id_type).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                load_domain_combo(type, initial_domain_id);
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                } else {
                	resetGenericAttributesForm();
                }
            }
        };

        var change_domain_type = function() {
            change_domain_type_impl();
        }
        var change_domain_type_impl = function(initial_category_id) {
            clear_category_combo();
            var type_id = $(id_type).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                } else {
                	resetGenericAttributesForm();
                }
            }
        };

        var clear_domain_combo = function() {
            $(id_domain + " > option").each(function(idx, elem) { $(elem).remove(); });
        }
        var clear_category_combo = function() {
            $(id_category + " > option").each(function(idx, elem) { $(elem).remove(); });
        }
        var load_domain_combo = function(type, initial_domain_id) {
            var domains = type.domains;
            for (var i = 0; i<domains.length; i++) {
                var selected = initial_domain_id == domains[i].id ? " selected=\"selected\"" : "";
                var newOption = "<option value=\"" +domains[i].id + "\"" + selected + ">" + domains[i].label + "</option>";
                $(newOption).appendTo(id_domain);
            }
        }
        var load_category_combo = function(domain, initial_category_id) {
            var categories = domain.categories;
            var has_category_changed = true ;
            for (var i = 0; i<categories.length; i++) {
                var selected = initial_category_id == categories[i].id ? " selected=\"selected\"" : "";
                var newOption = "<option value=\"" +categories[i].id + "\"" + selected + ">" + categories[i].label + "</option>";
                $(newOption).appendTo(id_category);
                if (selected) {
                	has_category_changed = false;
                }
            }            
            if( has_category_changed ) {
            	loadGenericAttributesForm(true);
            }
        }

        $(id_type).change(change_ticket_type);
        $(id_domain).change(change_domain_type);
        var domain_id = $(id_domain).val();
        change_ticket_type_impl(domain_id, category_id);
          
        var categorySelect = document.getElementById("id_ticket_category");       
        if (category_id > 0 ) {
    		categorySelect.value=category_id;
    	} 
        loadGenericAttributesForm(false);
    	categorySelect.onchange=function(){
    		loadGenericAttributesForm(true);
    	}
  
        
    });
}
//load generic attributes form from selected category
function loadGenericAttributesForm(bResetResponse) {
	$.ajax({
        url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue()+"&reset_response="+bResetResponse,
        type: "GET",
        dataType : "html",
        success: function( response ) {
			$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
        }
    });
}
//reset generic attributes form
function resetGenericAttributesForm() {
	$('#generic_attributes').replaceWith('<div id="generic_attributes"></div>');
}
//returns selected category value
function getSelectedCategoryValue()  {
	var categorySelect = document.getElementById("id_ticket_category");
	var catValue = -1;	
	if(categorySelect.options[categorySelect.selectedIndex] != undefined) 
	{
		catValue = categorySelect.options[categorySelect.selectedIndex].value;
	}
	return catValue ;
}
