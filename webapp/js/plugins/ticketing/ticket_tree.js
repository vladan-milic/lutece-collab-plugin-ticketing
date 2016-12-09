// Turns the 3 combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(id_type, id_domain, id_category, selected_category_id, is_front, is_generic_attributes_managed) {
    var base = $('head base').attr('href');
    $.getJSON( base + "rest/ticketing/type/s?format=json", function( data ) {
    	$("#id_ticket_precision").hide();
        var types_map = {};
        for (var i = 0; i<data.types.length; i++) {
            var type = data.types[i];
            types_map[type.id] = type;
            type.domains_map = {};
            for (var j = 0; j<type.domains.length; j++) {
                var domain = type.domains[j];
                type.domains_map[domain.id] = domain;
                domain.categories_map = {};
                for (var k = 0; k<domain.categories.length; k++) {
                    var category = domain.categories[k];
                    domain.categories_map[category.id] = category;
                    category.precisions_map = {};
                    if(category.precisions != undefined){
                    	for (var l = 0; l<category.precisions.length; l++) {
                    		var precision = category.precisions[l];
                    		category.precisions_map[category.label] = precision;
                    	}
                	}
                }
            }
        }

        var change_ticket_type = function() {
            change_ticket_type_impl();
        }
        var change_ticket_type_impl = function(initial_domain_id, initial_category_id) {
            clear_domain_combo();
            clear_category_combo();
            clear_precision_combo();
            var type_id = $(id_type).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                load_domain_combo(type, initial_domain_id);
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                    var category_id = $(id_category).val();
                    var category = types_map[type_id].domains_map[domain_id].categories_map[category_id];
                    if(category == undefined){
                    	var categoryMap = types_map[type_id].domains_map[domain_id].categories_map;
                    	for(var key in categoryMap){
                        	var currentLabel = categoryMap[key].label;
                		    var libelleCategory = $("#label_category").val();
                		    if(currentLabel == libelleCategory){
                		    	category = categoryMap[key];
                		    	break;
                		    }
                    	}
                    }
                    if(typeof(category) != 'undefined') {
                    	load_precision_combo(category, selected_category_id);
                    }
                } else {
                	resetGenericAttributesForm();
                }
            }
        };

        var change_domain_type = function() {
        	$('#id_ticket_category').find($('option')).attr('selected',false);
            change_domain_type_impl();
        }
        var change_domain_type_impl = function(initial_category_id) {
            clear_category_combo();
            clear_precision_combo();
            var type_id = $(id_type).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                    var category_id = $(id_category).val();
                    var category = types_map[type_id].domains_map[domain_id].categories_map[category_id];
                    if(typeof(category) != 'undefined') {
                		initial_category_id = category_id;
                    	load_precision_combo(category, initial_category_id);
                    }
                } else {
                	resetGenericAttributesForm();
                }
            }
        };
        
        var change_category_type = function() {
        	change_category_type_impl();
        }
        var change_category_type_impl = function(initial_category_id) {
        	clear_precision_combo();
            var type_id = $(id_type).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                	var category_id = $(id_category).val();
                	var category = types_map[type_id].domains_map[domain_id].categories_map[category_id];
                	if(typeof(category) != 'undefined') {                  	
                		initial_category_id = category_id;
                		load_precision_combo(category, initial_category_id);
                	}
                }
            }
        }

        var clear_domain_combo = function() {
            $(id_domain + " > option").each(function(idx, elem) { $(elem).remove(); });
        }
        var clear_category_combo = function() {
            $(id_category + " > option").each(function(idx, elem) { $(elem).remove(); });
        }
        var clear_precision_combo = function() {
            $("#id_ticket_precision" + " > option").each(function(idx, elem) { $(elem).remove(); });
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
            var alreadySelected = false;
            for (var i = 0; i<categories.length; i++) {
            	var labelCategory = $("#label_category").val();
                var selected = initial_category_id == categories[i].id ? " selected=\"selected\"" : "";
                if(selected){
                	alreadySelected = true;
                }
                var selectedLabel = labelCategory == categories[i].label ? " selected=\"selected\"" : "";
                if( selectedLabel && initial_category_id && !alreadySelected ) {
                	categories[i].id = initial_category_id;
                	selected = selectedLabel;                	
                }
                
                var newOption = "<option value=\"" +categories[i].id + "\"" + selected + ">" + categories[i].label + "</option>";
                $(newOption).appendTo(id_category);
                if (selected) {
                	has_category_changed = false;
                }
            }            
            if( has_category_changed ) {
            	if (is_generic_attributes_managed) 
            	{
            		loadGenericAttributesForm(true, is_front);
            	}
            }
        }
        var load_precision_combo = function(category, initial_precision_id){
        	var precisions = category.precisions;
        	var has_precision_changed = true;
        	if(precisions != undefined) {
        		for (var i = 0; i<precisions.length; i++) {
                	var selected = initial_precision_id == precisions[i].id ? " selected=\"selected\"" : "";
                	var newOption = "<option value=\"" +precisions[i].id + "\"" + selected + ">" + precisions[i].label + "</option>";
                	$(newOption).appendTo("#id_ticket_precision");
                	if (selected) {
                		has_precision_changed = false;
                		
                        var type_id = $(id_type).val();
                        var domain_id = $(id_domain).val();
                		var libelleCategory = $("#label_category").val();
						var map_category = types_map[type_id].domains_map[domain_id].categories_map;
						var key_to_erase = category.id;
						var map_to_extract = "";
						for(var key in map_category){
							var currentLabel = map_category[key].label;
                		    if(currentLabel == libelleCategory){
								key_to_erase = key;
								map_to_extract = map_category[key];
                		    }
						}
						if(key_to_erase != precisions[i].id){
							types_map[type_id].domains_map[domain_id].categories_map[precisions[i].id] = map_to_extract;
							delete types_map[type_id].domains_map[domain_id].categories_map[key_to_erase];
						}
						
                	}
        		}
        		var form_precision_area = $("#requalify_category");
        		if(form_precision_area != undefined) {
        			form_precision_area.show();
        		}
        		$("#id_ticket_precision").show();
        	} else {
        		var form_precision_area = $("#requalify_category");
        		if(form_precision_area != undefined) {
        			form_precision_area.hide();
        		}
        		$("#id_ticket_precision").hide();
                var type_id = $(id_type).val();
                var domain_id = $(id_domain).val();
                var domain = types_map[type_id].domains_map[domain_id];
                clear_category_combo();
        		load_category_combo(domain, initial_precision_id);
        	}
        }

        $(id_type).change(change_ticket_type);
        $(id_domain).change(change_domain_type);
        $(id_category).change(change_category_type);
        $("#id_ticket_precision").change(function() {
        	var selectedPrecision = document.getElementById("id_ticket_precision");
        	var categorySelect = document.getElementById("id_ticket_category");
    		
    		categorySelect.options[categorySelect.selectedIndex].value = selectedPrecision.options[selectedPrecision.selectedIndex].value;
        	loadGenericAttributesForm(true, is_front);
        });
        var domain_id = $(id_domain).val();
        change_ticket_type_impl(domain_id, selected_category_id);
          
        var categorySelect = document.getElementById("id_ticket_category");       
        if (selected_category_id > 0 ) {
    		categorySelect.value=selected_category_id;
    	} 
    	if (is_generic_attributes_managed) 
    	{
    		loadGenericAttributesForm(false, is_front);
    	}
    	categorySelect.onchange=function(){
        	if (is_generic_attributes_managed) 
        	{
        		loadGenericAttributesForm(true, is_front);
        	}
    	}
    });
}
//load generic attributes form from selected category
function loadGenericAttributesForm(is_response_reseted, is_front) {
	$.ajax({
        url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue()+"&reset_response="+is_response_reseted+"&display_front="+is_front,
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
