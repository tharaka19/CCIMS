var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllUserRoles();
});

function saveUpdateUserRole() {
    var id = $.trim($('#user_role_id').val());
    var roleName = $.trim($('#user_role_name').val());
    var description = $.trim($('#user_role_description').val());
    var status = $.trim($('#user_role_status').val());

    var userRoleObj = {
        "id": id,
        "roleName": roleName,
        "description": description,
        "status": status
    }

    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userRole/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(userRoleObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearUserRoleFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllUserRoles() {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userRole/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setUserRoleTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function userRoleEdit(id) {
    $("#user_role_id").val(id);
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userRole/getById/' + id,
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
                $('#user_role_id').val(content.id);
                $('#user_role_name').val(content.roleName);
                $('#user_role_description').val(content.description);
                $('#user_role_status').val(content.status);
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

function deleteUserRole(id) {
    delete_id = id;
}

function delete_role() {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/userRole/deleteById/' + delete_id,
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
                clearUserRoleFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setUserRoleTable(useRoles) {
    if ($.isEmptyObject(useRoles)) {
        $('#user_role_table').DataTable().clear();
        $('#user_role_table').DataTable({
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
        $("#user_role_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'User Role Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'User Role Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'User Role Details',
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
            "data": useRoles,
            "columns": [{
                "data": "roleName"
            },
                {
                    "data": "description"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        if (row.roleName == 'SUPPER_ADMIN' || row.roleName == 'SYSTEM_ADMIN') {
                            var columnHtml = '<button class="btn btn-xs btn-warning disabled" ><i class="fa fa-check-circle"></i>Reserved</button> ';
                            return (columnHtml);
                        } else {
                            var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=userRoleEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                                '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#userRoleBackdrop" title="Delete" onclick=deleteUserRole(value)><i class="fa fa-times"></i></button>';
                            return (columnHtml);
                        }
                    }
                }
            ]
        });
    }
}

function clearUserRoleFields() {
    $('#user_role_id').val('');
    $('#user_role_name').val('');
    $('#user_role_description').val('');
    $('#user_role_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllUserRoles();
}