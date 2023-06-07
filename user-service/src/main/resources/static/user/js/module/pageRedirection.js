function internalPageRedirection(pageUrl) {
    token = sessionStorage.getItem('token');
    $('#AjaxLoader').show();
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userAccount/pageRedirection',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            $('#AjaxLoader').hide();
            var user = data.userName;
            window.location.href = BASE_URL + USER_SERVICE + "/" + pageUrl + "/" + user + "/" + token
        }
    });
}