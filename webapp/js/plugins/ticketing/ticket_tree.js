// Turns the combos identified by the jquery selectors into dynamic combos
function lutece_ticket_tree(branch, categories_tree, url, allowNullSelection) {
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
			loadCombo(selector, categories, allowNullSelection?categories_depths[depthNumber-1]:undefined, branch[i].id, allowNullSelection);
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
		if (i < categories_depths.length)
		{
			var nextDepthNumber = depthNumber + 1;
			categories = getCategories(nextDepthNumber, categories_tree, arraySelectedCategoryId)
			
			if (categories && categories.length > 0)
			{
				loadCombo("#id_category_" + nextDepthNumber, categories, categories_depths[depthNumber], undefined, allowNullSelection);
				hideSelectors(nextDepthNumber+1);
			}
			else
			{
				hideSelectors(nextDepthNumber);
			}
		}
		else
		{
			hideSelectors(branch.length+1);
		}
		
	}
	else
	{
		loadCombo("#id_category_1", categories_tree["categories_depth_1"], categories_depths[0], undefined, allowNullSelection);
		hideSelectors(2);
	}
	
	// Change a selected category on click
	var i = 1;
	while((category = $("#id_category_" + i)).length)
	{
		category.change(function() {
			changeSelector(this);
	    });
		i++;
	}

	// Hide the selectors from the depthNumber
	function hideSelectors(depthNumber) {
		var i = depthNumber;
		while((category = $("#id_category_" + i)).length)
		{
			category.val(-1);
			category.parents(".form-group:first").hide();
			i++;
		};
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
		
		if(!allowNullSelection) {
			removeDefaultValue(selector, arraySelectedCategoryId[depthNumber]);
		}

		// remove all inactives not selected
		var selfCategories = getCategories(depthNumber, categories_tree, arraySelectedCategoryId);
		if(selfCategories) {
			selfCategories
				.filter(function(category) { return category.inactive; })
				.map(function(category) { return $(selector).find('option[value='+category.id+']') })
				.filter(function($option) { return !$option.selected; })
				.forEach(function($option) { $option.remove(); });
		}
		
		var nextDepthNumber = depthNumber + 1;
		categories = getCategories(nextDepthNumber, categories_tree, arraySelectedCategoryId)
		
		if (categories && categories.length > 0)
		{
			loadCombo("#id_category_" + nextDepthNumber, categories, categories_depths[depthNumber], undefined, allowNullSelection);
			hideSelectors(nextDepthNumber+1);
		}
		else
		{
			hideSelectors(nextDepthNumber);
		}
		loadGenericAttributesForm(url, false, "#id_category_" + depthNumber, false);
		
		categories = getCategories(depthNumber, categories_tree, arraySelectedCategoryId);
		var category = categories[getIndex(categories, arraySelectedCategoryId[depthNumber])];
		loadHelpMessage("#help_message_" + depthNumber, category);
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
				$('#generic_attributes').html(response);
				
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
		$('#generic_attributes').html('');
	}
}

//Load the combo corresponding to the parameter 'selector' with the parameter 'categories' 
function loadCombo(selector, categories, depth, idCategoryToSelect, allowNullSelection) {
	$(selector).children().remove();
	if (categories.length > 0 )
	{
		var existingSelectedCategory = idCategoryToSelect !== undefined;

		var selectedCategoryIndexId = null;
		if(existingSelectedCategory) {
			var index = getIndex(categories, idCategoryToSelect);
			selectedCategoryIndexId = index !== undefined && categories[index] && categories[index].id;
		}
		
		if(depth  != undefined) {
			loadHelpMessage("#help_message_" + depth.depth_number, undefined);
		}

		if (!existingSelectedCategory || allowNullSelection)
		{
			var defaultMessage = "-- Valeur par d\u00e9faut --";
			if (depth != undefined && depth.label != undefined && depth.label != "")
			{
				defaultMessage = "-- S\u00e9lectionnez une " + depth.label.toLowerCase() + " --";
			}

			$(selector).append(new Option(defaultMessage, -1, !selectedCategoryIndexId));
		}
		
		var optionsCount = 0;
		for (var i = 0; i<categories.length; i++)
		{
			var isSelected = selectedCategoryIndexId == categories[i].id;
			var option = new Option(categories[i].label, categories[i].id, isSelected, isSelected);
			if(!categories[i].inactive || isSelected) {
				optionsCount++;
				$(selector).append(option);
			}
		}
		if(optionsCount > 0) {
			$(selector).parents(".form-group:first").show();
		} else {
			$(selector).parents(".form-group:first").hide();
			$(selector).children().remove();
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