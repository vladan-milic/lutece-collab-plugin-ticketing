// Turns the 3 combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(type_selector, domain_selector, category_selector, precision_selector, selected_category_id, url) {
    var base = $('head base').attr('href');
    $.getJSON( base + "rest/ticketing/type/s?format=json", function( data ) {
        var types_map = {};
		var selectedType = undefined;
        var selectedDomain = undefined;
        var selectedCategory = undefined;
        var selectedPrecision = undefined;

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
                    		                    		
							if (selected_category_id == precision.id) {
								selectedType = type;
								selectedDomain = domain;
								selectedCategory = category;
								selectedPrecision = precision;
							}
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
            }
        }
		if (selectedType == undefined) {
			selectedType = data.types[0];
			selectedDomain = selectedType.domains[0];
			selectedCategory = selectedDomain.categories[0];
			if (selectedCategory.precisions != undefined) {
				selectedPrecision = selectedCategory.precisions[0];
			}
		}


        var load_messages = function() {
        	if (selectedCategory.help != undefined) {
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


		load_combo(type_selector, data.types, selectedType);
		load_combo(domain_selector, selectedType.domains, selectedDomain);
		load_combo(category_selector, selectedDomain.categories, selectedCategory);
		load_combo(precision_selector, selectedCategory.precisions, selectedPrecision);
		load_messages();
		setSelectedCategoryId();
		loadGenericAttributesForm(url, false, category_selector, true);

		$(type_selector).change(function() {
			selectedType = types_map[$(type_selector).val()];
			load_combo(domain_selector, selectedType.domains);
			$(domain_selector).change();
        });

        $(domain_selector).change(function() {
			selectedDomain = selectedType.domains_map[$(domain_selector).val()];
			load_combo(category_selector, selectedDomain.categories);
			$(category_selector).change();
        });

        $(category_selector).change(function() {
        	selectedCategory = selectedDomain.categories_map[$(category_selector).val()];
			load_combo(precision_selector, selectedCategory.precisions);
			$(precision_selector).change();
			
        });

        $(precision_selector).change(function() {
        	selectedPrecision = selectedCategory.precisions_map[$(precision_selector).val()];
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
				
				if(is_first_call && $('.alert.alert-danger').length > 0)
				{
					$('html,body').animate({
						  scrollTop: $('.alert.alert-danger').prev('form-group').offset().top
						}, 1000);
				}
			}
		});
	}
}

function load_combo(id_combo, options, selected_option) {
	$(id_combo).children().remove();
	if (options != undefined) {
		if (selected_option == undefined) {
			selected_option = options[0];
		}
		for (var i = 0; i<options.length; i++) {
			$(id_combo).append(new Option(options[i].label, options[i].id, selected_option.id == options[i].id, selected_option.id == options[i].id));
			$(id_combo).parents(".form-group:first").show();
		}
	} else {
		$(id_combo).parents(".form-group:first").hide();
	}
}