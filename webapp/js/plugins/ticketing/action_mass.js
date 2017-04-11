$(document).ready(function(){
	// Select or deselect all checkbox
	$("#select_all_tickets").change(function(){
		var status = $(this).is(":checked") ? true : false;
		$(".mass-action-ready").prop("checked",status);
		var checkedValues = getSelectedTickets( );
		if(status){
			activeMassAction( );
		} else {
			if(checkedValues == undefined || checkedValues.length == 0){
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
			disableMassAction( );
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

	// Send the ticket list selected to the modal
	$('#ticketing-modal-workflow-action-form').on("shown.bs.modal", function (e) {
		var checkedValues = getSelectedTickets();
		$('#ticketing-modal-workflow-action-form').find("input[name=selected_tickets]").val(checkedValues);
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
	var currentUrl = $(".link-modal").attr("href");
	var checkedValues = getSelectedTickets( );
	$(".link-modal").attr("href", currentUrl.replace(/id=[^&]+/, 'id='+checkedValues[0]));
	document.getElementById("linkMassAction").removeAttribute("disabled");
};

// Disabled the mass action link and reset the id value in the url
function disableMassAction( ) {
	var currentUrl = $(".link-modal").attr("href");
	$(".link-modal").attr("href", currentUrl.replace(/id=[^&]+/, 'id=-1'));
	document.getElementById("linkMassAction").setAttribute("disabled","disabled");
};