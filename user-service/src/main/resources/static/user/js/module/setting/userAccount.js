var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveUserRoles();
    getAllUserAccounts();
});

function getAllActiveUserRoles() {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userRole/getAllActive',
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
                    $('#user_role').append('<option class="user_role" value="' + value.id + '">' + value.roleName + '</option>');
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

function saveUpdateUser() {
    var id = $.trim($('#user_id').val());
    var userName = $.trim($('#user_name').val());
    var password = $.trim($('#user_password').val());
    var confirmPassword = $('#user_confirm_password').val();
    var userRoleId = $.trim($('#user_role').val());
    var status = $.trim($('#user_status').val());

    var userObj = {
        "id": id,
        "userName": userName,
        "password": password,
        "confirmPassword": confirmPassword,
        "userRoleId": userRoleId,
        "status": status
    }

    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userAccount/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(userObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearUserAccountFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllUserAccounts() {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userAccount/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setUserAccountTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function userAccountEdit(id) {
    $("#user_id").val(id);
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userAccount/getById/' + id,
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
                $('#user_id').val(content.id);
                $('#user_name').val(content.userName);
                $('#user_role').val(content.userRoleId);
                $('#user_status').val(content.status);
                $('#saveBtn').val("Update");
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteUserAccount(id) {
    delete_id = id;
}

function delete_user_account() {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userAccount/deleteById/' + delete_id,
        type: "DELETE",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                $('#close_btn').trigger('click');
                clearUserAccountFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setUserAccountTable(useAccounts) {
    if ($.isEmptyObject(useAccounts)) {
        $('#user_account_table').DataTable().clear();
        $('#user_account_table').DataTable({
            "bPaginate": false,
            "bLengthChange": false,
            "bFilter": false,
            "bInfo": false,
            "destroy": true,
            "language": {
                "emptyTable": "No Data Found !!!",
                search: "",
                searchPlaceholder: "Search..."
            }
        });
    } else {
        $("#user_account_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'User Account Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'User Account Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'User Account Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                }
            ],
            "destroy": true,
            "language": {
                search: "",
                searchPlaceholder: "Search..."
            },
            "data": useAccounts,
            "columns": [{
                "data": "userName"
            },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=userAccountEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#userAccountBackdrop" title="Delete" onclick=deleteUserAccount(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearUserAccountFields() {
    $('#user_id').val('');
    $('#user_name').val('');
    $('#user_password').val('');
    $('#user_confirm_password').val('');
    $('#user_role').val('NONE');
    $('#user_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllUserAccounts();
}