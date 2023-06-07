var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveCompanyProfiles();
});

function getAllActiveCompanyProfiles() {
    $.ajax({
        url: BASE_URL + ADMIN_SERVICE + '/companyProfile/getAllActive',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                $.each(data.content, function (key, value) {
                    $('#profile').append('<option class="profile" value="' + value.id + '">' + value.branchName + ' - ' + value.profileName + '</option>');
                });
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getCompanyProfile(profileId) {
    $.ajax({
        url: BASE_URL + ADMIN_SERVICE + '/companyProfile/getById/' + profileId,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                content = data.content;
                $('#user_name').val(content.userName);
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function dataSync() {
    var profileId = $.trim($('#profile').val());
    $.ajax({
        url: BASE_URL + ADMIN_SERVICE + '/dataSync/postSystemAdminAndUserAccount/' + profileId,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                content = data.content;
                $('#user_name').val(content.userName);
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function clearFields() {
    $('#user_name').val('');
    $('#profile').val("NONE");
}