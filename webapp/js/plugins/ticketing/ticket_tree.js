// The three messages of selections
var msg_select_type = "-- Choisir une nature --";
var msg_select_domain = "-- Choisir un domaine --";
var msg_select_category = "-- Choisir une probl\u00e9matique --";
var msg_select_precision = "-- Choisir une pr\u00e9cision --";

// Turns the 3 combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(type_selector, domain_selector, category_selector, precision_selector, selected_category_id, url) {
    var base = $('head base').attr('href');
    $.getJSON( base + "rest/ticketing/type/s?format=json", function( data ) {
        var types_map = {};
		var selectedType = undefined;
        var selectedDomain = undefined;
        var selectedCategory = undefined;
        var selectedPrecision = undefined;
        var idTypeSelected = $("#ticket_type_id").val();
        var idDomainSelected = $("#ticket_domain_id").val();
        var idTicketCategorySelected = $("#ticket_category_id").val();
        var precisionSelected = $("#ticket_precision").val();

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

                    if(category.precisions != undefined) {
                    	for (var l = 0; l<category.precisions.length; l++) {
                    		var precision = category.precisions[l];
                    		category.precisions_map[precision.id] = precision;
                    		domain.categories_map[precision.id] = category;
                    		                    		
							if (selected_category_id == precision.id && precisionSelected != "-1") {
								selectedType = type;
								selectedDomain = domain;
								selectedCategory = category;
								selectedPrecision = precision;
							}
                    	}
                        // Add the default message for precision when no selection has been made
                        if ( selectedPrecision == undefined )
                        {
                        	addDefaultValueInList(category.precisions, msg_select_precision);
                        }
                	} else {
						if (selected_category_id == category.id) {
							selectedType = type;
							selectedDomain = domain;
							selectedCategory = category;
							selectedPrecision = undefined;
						}
                    }
                }
                // Add the default message for category when no selection has been made
                if ( selectedCategory == undefined && (!idTicketCategorySelected || idTicketCategorySelected < 1) )
                {
                	addDefaultValueInList(domain.categories, msg_select_category);
                }
            }
            // Add the default message for domain when no selection has been made
            if ( selectedDomain == undefined && (!idDomainSelected || idDomainSelected < 1) )
            {
            	addDefaultValueInList(type.domains, msg_select_domain);
            }
        }
        // Add the default message for type when no selection has been made
        if ( selectedType == undefined && (!idTypeSelected || idTypeSelected < 1) )
        {
        	addDefaultValueInList(data.types, msg_select_type);
        }
        
		if (selectedType == undefined) {
			if (idTypeSelected && idTypeSelected > 0)
			{
				// Return from errors during validation case
				// --- retrieve type selected
		        for (var i = 0; i<data.types.length; i++)
		        {
		            if (data.types[i].id == idTypeSelected)
		            {
		            	selectedType = data.types[i];
		            	break;
		            }
		        }
		        // --- retrieve domain selected
		        if(selectedType != undefined)
		        {
		        	if(idDomainSelected && idDomainSelected > 0)
		        	{
		        		// one domain has beeen selected
		        		for (var j = 0; j<selectedType.domains.length; j++)
			        	{
			            	if (selectedType.domains[j].id == idDomainSelected)
			            	{
			            		selectedDomain = selectedType.domains[j];
			            		if(idTicketCategorySelected && idTicketCategorySelected > 0)
			            		{
									for(var k = 0; k<selectedDomain.categories.length; k++)
									{
										if(selectedDomain.categories[k].id == idTicketCategorySelected)
										{
											selectedCategory = selectedDomain.categories[k];
											break;
										}
									}
			            		}
			            		else
			            		{
			            			selectedCategory = selectedDomain.categories[0];
			            		}
								if (selectedCategory != undefined && selectedCategory.precisions != undefined)
								{
									selectedPrecision = selectedCategory.precisions[0];
								}
			            		break;
			            	}
			        	}
		        	}
		        	else
		        	{
		        		// no domain has been selected
		        		selectedDomain = selectedType.domains[0];
						selectedCategory = selectedType.domains[1].categories[0];
						if (selectedCategory.precisions != undefined) {
							selectedPrecision = selectedCategory.precisions[0];
						}
		        	}
		        }
		        // reset the preselected value
		        $("#ticket_type_id").val("");
		        $("#ticket_domain_id").val("");
		        $("#ticket_category_id").val("");
		        $("#ticket_precision").val("");
			}
			else
			{
				// Initialisation case
				selectedType = data.types[0];
				selectedDomain = data.types[1].domains[0];
				selectedCategory = data.types[1].domains[1].categories[0];
				if (selectedCategory.precisions != undefined) {
					selectedPrecision = selectedCategory.precisions[0];
				}
			}
		}


        var load_messages = function() {
        	if (selectedCategory!= undefined && selectedCategory.help != undefined) {
        		$("#help_message_category").html(selectedCategory.help);
        	} else {
        		$("#help_message_category").html("");
        	}

			$("#help_message_precision").html("");
        	if (selectedPrecision != undefined) {
				if (selectedPrecision.help != undefined) {
					$("#help_message_precision").html(selectedPrecision.help);
				}
        	}
        }
        var setSelectedCategoryId = function() {
        	if (selectedPrecision != undefined) {
        		$(category_selector + ' option:selected').val(selectedPrecision.id);
        	}
        }

        // Load all lists (type/domain/category)
		load_combo(type_selector, data.types, selectedType);
		load_combo(domain_selector, selectedType.domains, selectedDomain);
		load_combo(category_selector, selectedDomain.categories, selectedCategory);
		if(selectedCategory != undefined)
		{
			load_combo(precision_selector, selectedCategory.precisions, selectedPrecision);
		}
		else
		{
			load_combo(precision_selector, undefined, -1, msg_select_precision);
		}
		load_messages();
		setSelectedCategoryId();
		loadGenericAttributesForm(url, false, category_selector, true);

		// Change the selected type
		$(type_selector).change(function() {
			removeDefaultValue(type_selector, "id_ticket_type");
			selectedType = types_map[$(type_selector).val()];
			load_combo(domain_selector, selectedType.domains);
			$(domain_selector).trigger("change", true);
        });

		// Change the selected domain
        $(domain_selector).on("change", function(event, fromParent) {
			manageDefaultValueInList(fromParent, domain_selector, "id_ticket_domain", msg_select_domain);
			selectedDomain = selectedType.domains_map[$(domain_selector).val()];
			load_combo(category_selector, (selectedDomain == undefined ? selectedDomain : selectedDomain.categories), -1, msg_select_category);
			$(category_selector).trigger("change", true);
        });

        // change the selected category
        $(category_selector).on("change", function(event, fromParent) {
        	manageDefaultValueInList(fromParent, category_selector, "id_ticket_category", msg_select_category);
			selectedCategory = (selectedDomain != undefined ? selectedDomain.categories_map[$(category_selector).val()] : selectedDomain);
			load_combo(precision_selector, (selectedCategory == undefined ? selectedCategory : selectedCategory.precisions));
			$(precision_selector).trigger("change", true);
			
        });

        // Change the selected precision
        $(precision_selector).on("change", function(event, fromParent) {
        	manageDefaultValueInList(fromParent, precision_selector, "id_ticket_precision", msg_select_precision);
			if(selectedCategory != undefined && selectedCategory.precisions_map != undefined)
			{
				selectedPrecision = selectedCategory.precisions_map[$(precision_selector).val()];
			}
			else
			{
				selectedPrecision = -1;
			}
			load_messages();
			setSelectedCategoryId();
			loadGenericAttributesForm(url, false, category_selector, false);
        });
    });
}


//load generic attributes form from selected category
function loadGenericAttributesForm(url, is_response_reseted, category_selector, is_first_call) {
	if (typeof url !== "undefined") {
		$.ajax({
			url: url+"&id_ticket_category="+$(category_selector).val()+"&reset_response="+is_response_reseted,
			type: "GET",
			dataType : "html",
			success: function( response ) {
				$('#generic_attributes').replaceWith('<div id="generic_attributes">' + response + '</div>');
				
				if(is_first_call && $('.alert.alert-danger').length > 0 && $('#messages_errors_div').length == 0)
				{
					var positionToScroll = 0;
					if($('.alert.alert-danger').prev('.form-group').length)
					{
						positionToScroll = $('.alert.alert-danger').prev('.form-group').offset().top;
					}
					else if($('.alert.alert-danger').parent('.form-group').length)
					{
						positionToScroll = $('.alert.alert-danger').parent('.form-group').offset().top;
					}
					else 
					{
						positionToScroll = $('.alert.alert-danger').closest('.form-group').offset().top;
					}					
					$('html,body').animate({
						  scrollTop: positionToScroll
						}, 1000);
				}
			}
		});
	}
}

function load_combo(id_combo, options, selected_option, text_to_display) {
	$(id_combo).children().remove();
	if (options != undefined)
	{
		if (selected_option == undefined)
		{
			selected_option = options[0];
		}
		for (var i = 0; i<options.length; i++)
		{
			$(id_combo).append(new Option(options[i].label, options[i].id, selected_option.id == options[i].id, selected_option.id == options[i].id));
			$(id_combo).parents(".form-group:first").show();
		}
	} 
	else if (selected_option != undefined && (selected_option.id == '-1' || selected_option == -1))
	{
			$(id_combo).append(new Option((selected_option.label != undefined ? selected_option.label : text_to_display), -1));
			$(id_combo).parents(".form-group:first").show();
	} 
	else
	{
		$(id_combo).parents(".form-group:first").hide();
	}
}

// Add default value on list with specific message
function addDefaultValueInList(list, message)
{
	var emptyElement = {};
	emptyElement['id'] = -1;
	emptyElement['label'] = message;
	list.unshift(emptyElement);
}

// Add the default value or remove it in a list if necessary
function manageDefaultValueInList( fromParent, selector, idField, message )
{
	removeDefaultValue(selector, idField);
	addDefaultValue(fromParent, selector, idField, message);
}

// Add the default value in a list
function addDefaultValue( fromParent, selector, idField, message )
{
	if(fromParent && $(selector).find('option[value="-1"]').length < 1 )
	{
		$("#" + idField).prepend(new Option(message, -1));
		$("#" + idField + " option:first-child").attr("selected", "selected");
	}
}

// Remove the default value on a list
function removeDefaultValue( selector, idField )
{
	if($(selector).val() != '-1' && $(selector).find('option[value="-1"]').length > 0 )
	{
		document.getElementById(idField).remove(0);
	}
}