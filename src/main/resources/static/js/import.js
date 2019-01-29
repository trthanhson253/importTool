/*<![CDATA[*/
$( document ).ready(function() {
	$('#templateId').val($('#inputTemplate').val());
    $('#templateId').change(function() {
    	$.ajax({
            dataType: 'json',
            url: contextPath + '/import/history',
            data: {templateId: $('#templateId').val()},
            success: function (data) {
                $('#bodyTableHistory').html("");
                $.each(data, function (index, log) {
                    $('#bodyTableHistory').append(
                        '<tr>' + 
                    		'<td>' + index + '</td>'+
                    		'<td>' + log.strCreateTime + '</td>' +
                    		'<td>' + log.userName + '</td>' +
                    		'<td>' + log.action + '</td>' +
                    		'<td>' + '<a onclick="dowLoadFileImport(' + "'" + log.id + "'" + ')" ' + 
                    			'text="'+ log.fileName + '"' + '>' + log.fileName + '</a></td>' +
                    		'<td>' + log.result + '</td>' +
		                '</tr>'
                    );
                });
            }
        });
    });
    
    $("#dialog-Notice").dialog({
        width: 750,
        modal: true,
        autoOpen: false,
        position: {my: "center", at: "center", of: window},
        buttons: {
            'Ok': function () {
                $(this).dialog('close');
            }
        }
    });
});

function dowLoadFileImport(logImportId) {
	$.ajax({
        dataType: 'json',
        url: contextPath + '/import/checkExistFileImport',
        data: {logId: logImportId
        },
        success: function (data) {
        	console.log(data);
        	if (data == false) {
        		$("#dialog-Notice").dialog('open');
        	} else {
        		var a = document.createElement('a');
        		a.href = contextPath + '/dowload/' + logImportId;
        	    a.click();
        	}
        }
    });
}

function dowLoadFileTemplate() {
	var templateIdVal = $('#templateId').val();
	$.ajax({
        dataType: 'json',
        url: contextPath + '/import/checkExistFileTemplate',
        data: {templateId: templateIdVal},
        success: function (data) {
        	console.log(data);
        	if (data == false) {
        		$("#dialog-Notice").dialog('open');
        	} else {
        		var a = document.createElement('a');
        		a.href = contextPath + '/import/download/template/' + templateIdVal;
        	    a.click();
        	}
        }
    });
}

function openTabview(evt, tabViewName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tabViewName).style.display = "block";
    evt.currentTarget.className += " active";
}

function uploadFile() {
	// Get form
    var form = $('#FormImport')[0];
    var data = new FormData(form);
    // APPEND FILE TO POST DATA
    $.ajax({
    	dataType: 'json',
        url: contextPath + '/import/loadData',
        type: 'POST',
        contentType: false,
        data: data,
        enctype: 'multipart/form-data',
        //JQUERY CONVERT THE FILES ARRAYS INTO STRINGS.SO processData:false
        processData: false,
        success: function(dataResult) {
        	$('#bodyDataFileImport').html("");
            $.each(dataResult, function (index, obj) {
            	var tr = '<tr>';
            	$.each(obj, function (index, cell) {
            		tr = tr + '<td>' + cell + '</td>';
            	});
                tr = tr + '</tr>';
                $('#bodyDataFileImport').append(tr);
            });
        }
    });
}

function onClickBtSubmit() {
	// validate excel
    var form = $('#FormImport')[0];
    var data = new FormData(form);
	$.ajax({
    	dataType: 'json',
        url: contextPath + '/import/validateExcel',
        type: 'POST',
        contentType: false,
        data: data,
        enctype: 'multipart/form-data',
        //JQUERY CONVERT THE FILES ARRAYS INTO STRINGS.SO processData:false
        processData: false,
        success: function(dataResult) {
        	console.log('aaaaaaaaaa');
        	console.log(dataResult.message);
        	if (dataResult.message == 'TRUE') {
        		document.getElementById("FormImport").submit();
        	} else {
        		alert(dataResult.message);
        	}
        }
    });
	
	
}
/*]]>*/