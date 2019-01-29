var contextPath = document.getElementsByTagName('base')[0].getAttribute('href');

function remove(username) {
    if (confirm('Do you want to delete ' + username + '?')) {
        $.ajax({
            method: 'DELETE',
            url: contextPath + '/users?' + $.param({username: username}),
            success: function () {
                location.reload();
            }
        })
    }
}