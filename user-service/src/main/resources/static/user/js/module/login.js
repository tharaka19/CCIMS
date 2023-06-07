$(document).ready(function () {
    if (window.location.href.toLowerCase().endsWith("loggedout")) {
        var divE = document.getElementById("loggedOutMssg");
        divE.hidden = false;
    }
    document.addEventListener('keypress', function (event) {
        if (event.keyCode == 13) {
            authRequest();
        }
    });
});

function authRequest() {
    var userName = $.trim($('#userName').val());
    var password = $.trim($('#password').val());

    var divE = document.getElementById("loggedOutMssg");
    divE.hidden = true;

    divE = document.getElementById("invalidCredentialsMssg");
    divE.hidden = true;

    divE = document.getElementById("enterCredentialsMssg");
    divE.hidden = true;

    if (userName.length == 0 || password.length == 0) {
        var divE = document.getElementById("enterCredentialsMssg");
        divE.hidden = false;
        return;
    }
    var authObj = {
        "serviceName": "USER",
        "userName": userName,
        "password": password,
    }

    $.ajax({
        url: BASE_URL + '/auth/token',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        dataType: "json",
        data: JSON.stringify(authObj),
        complete: function (data) {
            if (data.status != 200) {
                var divE = document.getElementById("invalidCredentialsMssg");
                divE.hidden = false;
                return;
            }
            var authorizationHeader = data.responseText;
            sessionStorage.setItem('token', authorizationHeader);
            pageRedirection(authorizationHeader);
        }
    });
}

function pageRedirection(token) {
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
            var user = data.userName;
            var token = data.token;
            sessionStorage.setItem('user', user);
            $('#AjaxLoader').hide();
            window.location.href = BASE_URL + USER_SERVICE + "/dashboard/" + user + "/" + token
        }
    });
}
