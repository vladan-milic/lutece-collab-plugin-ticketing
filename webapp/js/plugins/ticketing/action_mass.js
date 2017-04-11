$(document).ready(function(){
	// The init url of modal opening
	var initUrlModal = $(".link-modal").attr("href");
	
	// Select or deselect all checkbox
	$("#select_all_tickets").change(function(){
		var status = $(this).is(":checked") ? true : false;
		$(".mass-action-ready").prop("checked",status);
		var checkedValues = getSelectedTickets( );
		if(status){
			activeMassAction( );
			if(checkedValues != undefined && checkedValues.length != 0){
				for(i=0; i<checkedValues.length; i++){
					// Change the url parameters value
					var currentUrl = $(".link-modal").attr("href");
					$(".link-modal").attr("href", currentUrl.concat("&id=" + checkedValues[i].value));
				}
			}
		} else {
			if(checkedValues == undefined || checkedValues.length == 0){
				$(".link-modal").attr("href", initUrlModal);
				disableMassAction( );
			}
		}
	});

	// Deselect the global checkbox if manual modification
	$(".mass-action-ready").change(function(){
		if($("#select_all_tickets").is(":checked")){
			$("#select_all_tickets").prop("checked",false);
		}
		// If all checkbox are checked we will check the global checkbox
		if($(".mass-action-ready:checked").length == $(".mass-action-ready").length){
			$("#select_all_tickets").prop("checked",true);
		}
	});

	// Modify the url for the action to execute
	$(".mass-action-ready").on("change", function(){
		var checkedValues = getSelectedTickets( );
		if(checkedValues != undefined && checkedValues.length != 0){
			activeMassAction( );
		} else {
			$(".link-modal").attr("href", initUrlModal);
			disableMassAction( );
		}
		var currentUrl = $(".link-modal").attr("href");
		if($(this).is(":checked")){
			$(".link-modal").attr("href", currentUrl.concat("&id=" + $(this).val()));			
		} else {
			$(".link-modal").attr("href", currentUrl.replace("&id=" + $(this).val(), '') );
		}
	});

	// Change the modal values on action selection
	$("#id_mass_action").on('click',function(){
		var massActionList = document.getElementById("id_mass_action");
		var massActionSelected = massActionList.options[massActionList.selectedIndex];
		$(".link-modal").attr("data-action-id", massActionSelected.value);
		$(".link-modal").attr("title", massActionSelected.getAttribute("data-name"));
		$(".link-modal").attr("data-title", massActionSelected.getAttribute("data-description"));
		// Change the url parameters value
		var currentUrl = $(".link-modal").attr("href");
		$(".link-modal").attr("href", currentUrl.replace(/id_action=[^&]+/, 'id_action='+massActionSelected.value));
	});
});

// Return the list of id of all selected tickets
function getSelectedTickets( ) {
	var checkedValues = $('.mass-action-ready:checkbox:checked').map(function() {
		return this.value;
	}).get();
	return checkedValues;
};

// Active the action mass link and init the id value in the url
function activeMassAction( ) {
	document.getElementById("linkMassAction").removeAttribute("disabled");
};

// Disabled the mass action link and reset the id value in the url
function disableMassAction( ) {
	document.getElementById("linkMassAction").setAttribute("disabled","disabled");
};