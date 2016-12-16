// Turns the 3 combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(type_selector, domain_selector, category_selector, precision_selector, selected_category_id, is_front, is_generic_attributes_managed) {
    var base = $('head base').attr('href');
    $.getJSON( base + "rest/ticketing/type/s?format=json", function( data ) {
        $(precision_selector).hide();
        var types_map = {};
        var help_category_messages_map = {};
        var help_precision_messages_map = {};
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
                    if(typeof(category.help) != undefined){
                    	help_category_messages_map[category.id] = category.help
                    }
                    domain.categories_map[category.id] = category;
                    category.precisions_map = {};
                    if(category.precisions != undefined){
                    	for (var l = 0; l<category.precisions.length; l++) {
                    		var precision = category.precisions[l];
                    		if(typeof(precision.help) != undefined){
                    			help_precision_messages_map[precision.id] = precision.help
                            }
                    		category.precisions_map[category.label] = precision;
                            domain.categories_map[precision.id] = category;
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
            var type_id = $(type_selector).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                load_domain_combo(type, initial_domain_id);
                var domain_id = $(domain_selector).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                    var category_id = $(category_selector).val();
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
                    	toggleHelpMessage(help_category_messages_map[category_id], "#help_message_category");
                    }
                } else {
                	resetGenericAttributesForm();
                }
            }
        };

        var change_domain_type = function() {
            $(precision_selector).find($('option')).attr('selected',false);
            change_domain_type_impl();
        }
        var change_domain_type_impl = function(initial_category_id) {
            clear_category_combo();
            clear_precision_combo();
            var type_id = $(type_selector).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                var domain_id = $(domain_selector).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                    load_category_combo(domain, initial_category_id);
                    var category_id = $(category_selector).val();
                    var category = types_map[type_id].domains_map[domain_id].categories_map[category_id];
                    if(typeof(category) != 'undefined') {
                    	
                    	toggleHelpMessage(help_category_messages_map[category_id], "#help_message_category");
                    	
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
            var type_id = $(type_selector).val();
            var type = types_map[type_id];
            if (typeof(type) != 'undefined') {
                var domain_id = $(domain_selector).val();
                var domain = types_map[type_id].domains_map[domain_id];
                if (typeof(domain) != 'undefined') {
                	var category_id = $(category_selector).val();
                	var category = types_map[type_id].domains_map[domain_id].categories_map[category_id];
                	if(typeof(category) != 'undefined') {

                    	toggleHelpMessage(help_category_messages_map[category_id], "#help_message_category");
                    	
                		initial_category_id = category_id;
                		load_precision_combo(category, initial_category_id);
                	}
                }
            }
        }

        var clear_domain_combo = function() {
            clearCombo(domain_selector);
        }
        var clear_category_combo = function() {
            clearCombo(category_selector);
        }
        var clear_precision_combo = function() {
            clearCombo(precision_selector);
        }
        
        var load_domain_combo = function(type, initial_domain_id) {
            var domains = type.domains;
            for (var i = 0; i<domains.length; i++) {
                var selected = initial_domain_id == domains[i].id;

                $(createComboOption(domains[i].id, domains[i].label, selected)).appendTo(domain_selector);
            }
        }
        var load_category_combo = function(domain, initial_category_id) {
            var categories = domain.categories;
            var has_category_changed = true ;
            var alreadySelected = false;
            for (var i = 0; i<categories.length; i++) {
            	var labelCategory = $("#label_category").val();
                
                var selected = false;
                var precisions = categories[i].precisions;
                if(precisions != undefined) {
                    for (var j = 0; j<precisions.length; j++) {
                        if (initial_category_id == precisions[j].id) {
                            selected = true;
                            categories[i].id = initial_category_id;
                            break;
                        }
                    }
                } else {
                    selected = initial_category_id == categories[i].id;
                }
                
                if(selected){
                	alreadySelected = true;
                }
                
                var selectedLabel = labelCategory == categories[i].label;
                if( selectedLabel && initial_category_id && !alreadySelected ) {
                	categories[i].id = initial_category_id;
                	selected = selectedLabel;                	
                }
                
                $(createComboOption(categories[i].id, categories[i].label, selected)).appendTo(category_selector);
                
                if (selected) {
                	has_category_changed = false;

                	toggleHelpMessage(help_category_messages_map[categories[i].id], "#help_message_category");
                }
            }            
            if( has_category_changed ) {
              loadGenericAttributesForm(is_generic_attributes_managed, true, is_front);
            }
        }
        var load_precision_combo = function(category, initial_precision_id){
        	var precisions = category.precisions;
        	var has_precision_changed = true;
        	if(precisions != undefined) {
        		for (var i = 0; i<precisions.length; i++) {
                	var selected = initial_precision_id == precisions[i].id;
                	
                	$(createComboOption(precisions[i].id, precisions[i].label, selected)).appendTo(precision_selector);
                	if (selected) {
                		has_precision_changed = false;
                		
                		toggleHelpMessage(help_precision_messages_map[precisions[i].id], "#help_message_precision");
						
						var type_id = $(type_selector).val();
                        var domain_id = $(domain_selector).val();
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
        		$(precision_selector).show();
        	} else {
				var form_precision_area = $("#requalify_category");
        		if(form_precision_area != undefined) {
        			form_precision_area.hide();
        		}
        		$(precision_selector).hide();
        		$("#help_message_precision").hide();
                var type_id = $(type_selector).val();
                var domain_id = $(domain_selector).val();
                var domain = types_map[type_id].domains_map[domain_id];
                clear_category_combo();
        		load_category_combo(domain, initial_precision_id);
        	}
        }

        $(type_selector).change(change_ticket_type);
        $(domain_selector).change(change_domain_type);
        $(category_selector).change(change_category_type);
        $(precision_selector).change(function() {
        	var selectedPrecision = document.getElementById("id_ticket_precision");
        	var categorySelect = document.getElementById("id_ticket_category");
        	
    		toggleHelpMessage(help_precision_messages_map[selectedPrecision.options[selectedPrecision.selectedIndex].value], "#help_message_precision");
    		
    		categorySelect.options[categorySelect.selectedIndex].value = selectedPrecision.options[selectedPrecision.selectedIndex].value;
            
            loadGenericAttributesForm(is_generic_attributes_managed, true, is_front);
        });
        var domain_id = $(domain_selector).val();
        change_ticket_type_impl(domain_id, selected_category_id);
          
        var categorySelect = document.getElementById("id_ticket_category");       
        if (selected_category_id > 0 ) {
    		categorySelect.value=selected_category_id;
    	}
        
    	loadGenericAttributesForm(is_generic_attributes_managed, false, is_front);
    	
    	categorySelect.onchange = function() {
          loadGenericAttributesForm(is_generic_attributes_managed, true, is_front);
    	}
    });
}
//load generic attributes form from selected category
function loadGenericAttributesForm(is_generic_attributes_managed, is_response_reseted, is_front) {
  if (is_generic_attributes_managed) {
    $.ajax({
      url: "jsp/site/Portal.jsp?page=ticket&view=ticketForm&id_ticket_category="+getSelectedCategoryValue()+"&reset_response="+is_response_reseted+"&display_front="+is_front,
      type: "GET",
      dataType : "html",
      success: function( response ) {
        $('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
      }
    });
  }
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

function toggleHelpMessage(help_message, placeholder) {
	if (typeof(help_message) != 'undefined'){
		$(placeholder).html(help_message);
		$(placeholder).show();
	} else {
		$(placeholder).html("");
		$(placeholder).hide();
	}
}

function clearCombo(combo_selector) {
	$(combo_selector + " > option").each(function(idx, elem) { $(elem).remove(); });
}

function createComboOption(value, label, is_selected) {
	var option = document.createElement("option");
	option.value = value;
	option.innerHTML = label;
	
	if (is_selected) {
		option.selected = "selected";
	}
	
	return option;
}