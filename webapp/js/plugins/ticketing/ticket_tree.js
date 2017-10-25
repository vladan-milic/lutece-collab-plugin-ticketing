// The three messages of selections
var default_msg_select_type = "-- Choisir une nature --";
var default_msg_select_domain = "-- Choisir un domaine --";
var default_msg_select_category = "-- Choisir une probl\u00e9matique --";
var default_msg_select_precision = "-- Choisir une sous-probl\u00e9matique --";

// Turns the combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(type_selector, domain_selector, category_selector, precision_selector, categories_tree, url) {
    var idTypeSelected = $("#ticket_type_id").val();
    var idDomainSelected = $("#ticket_domain_id").val();
    var idCategorySelected = $("#ticket_category_id").val();
    var idPrecisionSelected = $("#ticket_precision_id").val();
	var selectedType = undefined;
    var selectedDomain = undefined;
    var selectedCategory = undefined;
    var selectedPrecision = undefined;
    
	if (idTypeSelected > 0)
	{
		var index = get_index(categories_tree.categories_depth_1, idTypeSelected);
		selectedType = categories_tree.categories_depth_1[index];
	}
	if (idDomainSelected > 0)
	{
		var index = get_index(selectedType.categories_depth_2, idDomainSelected);
		selectedDomain = selectedType.categories_depth_2[index];
	}
	if (idCategorySelected > 0)
	{
		var index = get_index(selectedDomain.categories_depth_3, idCategorySelected);
		selectedCategory = selectedDomain.categories_depth_3[index];
	}
	if (idPrecisionSelected > 0)
	{
		var index = get_index(selectedCategory.categories_depth_4, idPrecisionSelected);
		selectedPrecision = selectedCategory.categories_depth_4[index];
	}

    var load_messages = function() {
    	if (selectedCategory!= undefined && selectedCategory.help != undefined) {
    		$("#help_message_category").html(selectedCategory.help);
    	} else {
    		$("#help_message_category").html("");
    	}

    	if (selectedPrecision!= undefined && selectedPrecision.help != undefined) {
    		$("#help_message_precision").html(selectedPrecision.help);
    	} else {
    		$("#help_message_precision").html("");
    	}
    }
	
    // Load all lists (type/domain/category/precision)
	load_combo(type_selector, categories_tree.categories_depth_1, idTypeSelected, default_msg_select_type, true, false);
	load_combo(domain_selector, (idTypeSelected > 0 ? selectedType.categories_depth_2 : undefined), idDomainSelected, default_msg_select_domain, false, false);
	load_combo(category_selector, (idDomainSelected > 0 ? selectedDomain.categories_depth_3 : undefined), idCategorySelected, default_msg_select_category, false, false);
	if (idCategorySelected > 0 && selectedCategory.categories_depth_4.length > 0)
	{
		load_combo(precision_selector, (idCategorySelected > 0 ? selectedCategory.categories_depth_4 : undefined), idPrecisionSelected, default_msg_select_precision, false, true);
	}
	else
	{
		hide_precision(precision_selector);
	}
	
    // Load generic attributes
	if (idPrecisionSelected > 0)
	{
		loadGenericAttributesForm(url, false, precision_selector, true);
	}
	else if (idCategorySelected > 0)
	{
		loadGenericAttributesForm(url, false, category_selector, true);
	}

	// Change the selected type on click
	$(type_selector).change(function() {
		selectedType = changeTypeList(categories_tree.categories_depth_1);
    });

	// Change the selected domain on click
    $(domain_selector).change(function() {
    	selectedDomain = changeDomainList(selectedType.categories_depth_2);
    });

    // Change the selected category on click
    $(category_selector).change(function() {
    	selectedCategory = changeCategoryList(selectedDomain.categories_depth_3);
    });

    // Change the selected precision on click
    $(precision_selector).change(function() {
    	selectedPrecision = changePrecisionList(selectedCategory.categories_depth_4)
    });
	
	// Change the selected type
	function changeTypeList(categories_depth_1)
	{
		var typeValue = $(type_selector).val();
		removeDefaultValue(type_selector, "id_ticket_type", typeValue);
		var index = get_index(categories_depth_1, typeValue);
		var newSelectedType = categories_depth_1[index];
		load_combo(domain_selector, newSelectedType.categories_depth_2, 0, default_msg_select_domain, true, false);
		load_combo(category_selector, undefined, 0, default_msg_select_category, false, false);
		hide_precision(precision_selector);
		return newSelectedType;
	}
	
	// Change the selected domain
	function changeDomainList(categories_depth_2)
	{
		var domainValue = $(domain_selector).val();
		removeDefaultValue(domain_selector, "id_ticket_domain", domainValue);
		var index = get_index(categories_depth_2, domainValue);
		var newSelectedDomain = categories_depth_2[index];
		load_combo(category_selector, newSelectedDomain.categories_depth_3, 0, default_msg_select_category, true, false);
		hide_precision(precision_selector);
		return newSelectedDomain;
	}
	
	// Change the selected category
	function changeCategoryList(categories_depth_3)
	{
		var categoryValue = $(category_selector).val();
		removeDefaultValue(category_selector, "id_ticket_category", categoryValue);
		var index = get_index(categories_depth_3, categoryValue);
		var newSelectedCategory = categories_depth_3[index];
		load_combo(precision_selector, newSelectedCategory.categories_depth_4, 0, default_msg_select_precision, true, true);
		loadGenericAttributesForm(url, false, category_selector, false);
		load_messages();
		return newSelectedCategory;
	}
	
	// Change the selected precision
	function changePrecisionList(categories_depth_4)
	{
		var precisionValue = $(precision_selector).val();
		removeDefaultValue(precision_selector, "id_ticket_precision", precisionValue);
		var index = get_index(categories_depth_4, precisionValue);
		var newSelectedPrecision = categories_depth_4[index];
		loadGenericAttributesForm(url, false, precision_selector, false);
		load_messages();
		return newSelectedPrecision;
	}
}


//load generic attributes form from selected category
function loadGenericAttributesForm(url, is_response_reseted, selector, is_first_call) {
	if (typeof url !== "undefined") {
		$.ajax({
			url: url+"&id_ticket_category="+$(selector).val()+"&reset_response="+is_response_reseted,
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
	else
	{
		$('#generic_attributes').replaceWith('<div id="generic_attributes">' + "" + '</div>');
	}
}

function hide_precision(precision_selector) {
	if (precision_selector != undefined)
	{
		$(precision_selector).parents(".form-group:first").hide();
	}
}

function load_combo(id_combo, categories, selected_category_id, default_msg_select, is_next_list_to_select, is_precision) {
	
	$(id_combo).children().remove();
	if (categories != undefined)
	{
		if (selected_category_id > 0)
		{
			var index = get_index(categories, selected_category_id);
			for (var i = 0; i<categories.length; i++)
			{
				$(id_combo).append(new Option(categories[i].label, categories[i].id, categories[index].id == categories[i].id, categories[index].id == categories[i].id));
				$(id_combo).parents(".form-group:first").show();
			}
		}
		else
		{
			if (categories.length == 0)
			{
				$(id_combo).parents(".form-group:first").hide();
			}
			else
			{
				$(id_combo).append(new Option(default_msg_select, -1, true));
				if (is_next_list_to_select)
				{
					for (var i = 0; i<categories.length; i++)
					{
						$(id_combo).append(new Option(categories[i].label, categories[i].id, false));
						$(id_combo).parents(".form-group:first").show();
					}
				}
			}
		}
	}
	else
	{
		if (is_precision)
		{
			$(id_combo).parents(".form-group:first").hide();
		}
		$(id_combo).append(new Option(default_msg_select, -1, true));
		$(id_combo).parents(".form-group:first").show();
	}
}

function get_index(categories, category_id) {
	var i = 0;
	if (categories != undefined)
	{
		while (i < categories.length) {
			if (categories[i].id == category_id)
			{
				break;
			}
		    i++;
		}
	}
	return i;
}

// Remove the default value on a list
function removeDefaultValue( selector, idField, currentValue )
{
	if(currentValue != '-1' && $(selector).find('option[value="-1"]').length > 0 )
	{
		document.getElementById(idField).remove(0);
	}
}