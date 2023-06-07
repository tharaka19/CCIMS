var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllAllowanceTypes();
});

function saveUpdateAllowanceType() {
    var id = $.trim($('#allowance_type_id').val());
    var allowanceType = $.trim($('#allowance_type').val());
    var description = $.trim($('#allowance_type_description').val());
    var allowancePay = $.trim($('#allowance_type_pay').val());
    var status = $.trim($('#allowance_type_status').val());

    var allowanceTypeObj = {
        "id": id,
        "allowanceType": allowanceType,
        "description": description,
        "allowancePay": allowancePay,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/allowanceType/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(allowanceTypeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearAllowanceTypeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllAllowanceTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/allowanceType/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setAllowanceTypeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function allowanceTypeEdit(id) {
    $("#allowance_type_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/allowanceType/getById/' + id,
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
                $('#allowance_type_id').val(content.id);
                $('#allowance_type').val(content.allowanceType);
                $('#allowance_type_description').val(content.description);
                $('#allowance_type_pay').val(content.allowancePay);
                $('#allowance_type_status').val(content.status);
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

function deleteAllowanceType(id) {
    delete_id = id;
}

function delete_allowance_type() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/allowanceType/deleteById/' + delete_id,
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
                clearAllowanceTypeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setAllowanceTypeTable(allowanceTypes) {
    if ($.isEmptyObject(allowanceTypes)) {
        $('#allowance_type_table').DataTable().clear();
        $('#allowance_type_table').DataTable({
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
        $("#allowance_type_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Allowance Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Allowance Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Allowance Type Details',
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
            "data": allowanceTypes,
            "columns": [{
                "data": "allowanceType"
            },
                {
                    "data": "description"
                },
                {
                    "data": "allowancePay"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=allowanceTypeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#allowanceTypeBackdrop" title="Delete" onclick=deleteAllowanceType(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearAllowanceTypeFields() {
    $('#allowance_type_id').val('');
    $('#allowance_type').val('');
    $('#allowance_type_description').val('');
    $('#allowance_type_pay').val('');
    $('#allowance_type_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllAllowanceTypes();
}