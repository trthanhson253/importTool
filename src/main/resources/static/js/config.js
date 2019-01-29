var contextPath = document.getElementsByTagName('base')[0].getAttribute('href');
function remove(key) {
    if (confirm('Do you want to delete ' + key + '?')) {
        $.ajax({
            method: 'DELETE',
            url: contextPath + '/configs?' + $.param({key: key}),
            success: function () {
                location.reload();
            }
        })
    }
}

$( document ).ready(function() {
	var currentForm;
	$('#submitSave').click(function () {
		currentForm = $(this).closest('form');
		if (!$('#file').val()) {
			$('#messageConfirm').text('Bạn chưa chọn file template, bạn có muốn lưu dữ liệu ?');
			$("#dialog-confirm").dialog('open');
			return false;
		}
		var templateIdVal = $('#templateId').val();
		var exist;
		$.ajax({
            type: "POST",
            url: contextPath + "/configs/checkExistFileTemplate",
            data: {templateId: templateIdVal},
            dataType: "json",
            async: false,
            success: function (data) {
                exist = data;
            }
        });
		if (exist) {
			$('#messageConfirm').text('File template đã tồn tại trên hệ thống. Bạn có muốn ghi đè lên file đó ?');
			$("#dialog-confirm").dialog('open');
		} else {
			currentForm.submit();
		}
		return false;
	});
	
	$("#dialog-confirm").dialog({
        resizable: false,
        width: 750,
        modal: true,
        autoOpen: false,
        position: {my: "center", at: "center", of: window},
        buttons: {
            'Có': function () {
                $(this).dialog('close');
                currentForm.submit();
            },
            'Không': function () {
                $(this).dialog('close');
            }
        }
    });
});