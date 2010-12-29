$(document).ready(function(){
	/* Wrap everything in a single element */
	$('#loadroot').click(function(){
		$('#listroot').load('listdata-root.html');
	});
	
	$('#loadnonroot').click(function(){
		$('#listnonroot').load('listdata-nonroot.html');
	});
	/* Wrap everything in a single element */
	
	/* Use event delegation */
	$('select.option-selector').change(function(e){
		var clicked = $(e.target);
		var input = clicked.prev();
		// Give selectors a context
		// get selected text of combobox
		var selectedText = $('option:selected', clicked).text().trim();
		input.val(clicked.val()+selectedText);
	});
});