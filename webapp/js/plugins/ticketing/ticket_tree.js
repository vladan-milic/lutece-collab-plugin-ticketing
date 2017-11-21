// Turns the combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(branch, categories_tree, url) {

	var categories_depths = categories_tree.categories_depths;
	var arraySelectedCategoryId = new Array();
	for (var i = 1; i<=categories_depths.length; i++)
	{
		arraySelectedCategoryId[i] = undefined;
	}

	if (branch != undefined && branch.length > 0)
	{
		var depthNumber = branch[0].depth_number;
		var categories = categories_tree["categories_depth_1"];
		var i = 0;
		while (i < branch.length)
		{
			var selector = "#id_category_" + depthNumber;
			arraySelectedCategoryId[depthNumber] = branch[i].id;
			loadCombo(selector, categories, undefined, branch[i].id);
			loadGenericAttributesForm(url, false, selector, true);
//			loadHelpMessage("#help_message_" + depthNumber, categories);
			var index = getIndex(categories, arraySelectedCategoryId[depthNumber]);
			i++;
			if (i < branch.length)
			{
				depthNumber = branch[i].depth_number;
				categories = categories[index]["categories_depth_" + depthNumber];
			}
		}
		if (i < categories.length)
		{
			var nextDepthNumber = depthNumber + 1;
			loadCombo("#id_category_" + nextDepthNumber, categories, categories_depths[nextDepthNumber - 1]);
			hideSelectors(branch.length+2);
		}
		else
		{
			hideSelectors(branch.length+1);
		}
		
	}
	else
	{
		loadCombo("#id_category_1", categories_tree["categories_depth_1"], categories_depths[0]);
		hideSelectors(2);
	}
	
	// Change a selected category on click
	if (categories_depths != undefined && categories_depths.length > 0)
	{
		for (var i = 0; i<categories_depths.length; i++)
		{
			$("#id_category_" + categories_depths[i].depth_number).change(function() {
				changeSelector(this);
		    });
		}
	}

	// Hide the selectors from the depthNumber
	function hideSelectors(depthNumber) {
		for (var i = depthNumber; i<=categories_depths.length; i++)
		{
			var selector = "#id_category_" + i;
			if (selector != undefined)
			{
				$(selector).parents(".form-group:first").hide();
			}
		}
	}
	
	// Change the next selector
	function changeSelector(selector)
	{
		var depthNumber = parseInt($(selector).attr('depth'));
		
		arraySelectedCategoryId[depthNumber] = $(selector).val();
		for (var i = depthNumber+1; i<arraySelectedCategoryId.length; i++)
		{
			arraySelectedCategoryId[i] = undefined;
		}
		
		removeDefaultValue(selector, arraySelectedCategoryId[depthNumber]);
		var nextDepthNumber = depthNumber + 1;
		categories = getCategories(nextDepthNumber, categories_tree, arraySelectedCategoryId)
		
		if (categories.length > 0)
		{
			loadCombo("#id_category_" + nextDepthNumber, categories, categories_depths[depthNumber], undefined);
			hideSelectors(nextDepthNumber+1);
		}
		else
		{
			hideSelectors(nextDepthNumber);
		}
		loadGenericAttributesForm(url, false, "#id_category_" + depthNumber, false);
//		loadHelpMessage("#help_message_" + depthNumber, categories);
	}
}

//Load the help message corresponding to the selected category 
function loadHelpMessage(help_message, selectedCategory)
{
	if (selectedCategory!= undefined && selectedCategory.help != undefined) {
		$(help_message).html(selectedCategory.help);
	} else {
		$(help_message).html("");
	}
}

//Load generic attributes form from selected category
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

//Load the combo corresponding to the parameter 'selector' with the parameter 'categories' 
function loadCombo(selector, categories, depth, idCategoryToSelect) {
	
	$(selector).children().remove();
	if (idCategoryToSelect != undefined && idCategoryToSelect > 0 && categories.length > 0)
	{
		var index = getIndex(categories, idCategoryToSelect);
		for (var i = 0; i<categories.length; i++)
		{
			$(selector).append(new Option(categories[i].label, categories[i].id, categories[index].id == categories[i].id, categories[index].id == categories[i].id));
		}
		$(selector).parents(".form-group:first").show();
	}
	else
	{
		if (categories.length > 0)
		{
			var defaultMessage = "-- Valeur par d\u00e9faut --";
			if (depth != undefined && depth.label != undefined && depth.label != "")
			{
				defaultMessage = "-- " + depth.label + " \u00e0 s\u00e9lectionner --";
			}
			$(selector).append(new Option(defaultMessage, -1, true));
			for (var i = 0; i<categories.length; i++)
			{
				$(selector).append(new Option(categories[i].label, categories[i].id, false));
			}
			$(selector).parents(".form-group:first").show();
		}
	}
}

//Get the categories array corresponding to the depthNumber and to the selected categories 
function getCategories(depthNumber, categories_tree, arraySelectedCategoryId)
{
	var categories = categories_tree["categories_depth_1"];
	if (categories != undefined && arraySelectedCategoryId != undefined && depthNumber != undefined)
	{
		var i = 1;
		while (i < depthNumber)
		{
			var index = getIndex(categories, arraySelectedCategoryId[i]);
			if (index != undefined)
			{
			    i++;
				categories = categories[index]["categories_depth_" + i];
			}
			else
			{
				categories = undefined;
				break;
			}
		}
	}
	else 
	{
		categories = undefined;
	}
	return categories;
}

//Get the index of categories array corresponding to the categoryId 
function getIndex(categories, categoryId) {
	var result = undefined;
	var i = 0;
	if (categories != undefined)
	{
		while (i < categories.length) {
			if (categories[i].id == categoryId)
			{
				result = i;
				break;
			}
		    i++;
		}
	}
	return result;
}

// Remove the default value on a list
function removeDefaultValue(selector, currentValue)
{
	if(currentValue != '-1' && $(selector).find('option[value="-1"]').length > 0 )
	{
		selector.remove(0);
	}
}