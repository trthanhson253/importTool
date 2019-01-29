/*<![CDATA[*/
$( document ).ready(function() {
//	$('#FromDate').mask("00/00/0000", {placeholder: "dd/mm/yyyy"});
//	$('#ToDate').mask("00/00/0000", {placeholder: "dd/mm/yyyy"});
	$("#FromDate").datepicker({
		changeMonth: true,
        changeYear: true,
        dateFormat: 'dd/mm/yy'
	});
	$("#ToDate").datepicker({
		changeMonth: true,
        changeYear: true,
        dateFormat: 'dd/mm/yy'
	});
});
/*]]>*/