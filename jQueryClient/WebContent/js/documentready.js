$(document).ready(function(){
	$('#loadroot').click(function(){
		$('#listroot').load('listdata-root.html');
	});
	
	$('#loadnonroot').click(function(){
		$('#listnonroot').load('listdata-nonroot.html');
	});
});