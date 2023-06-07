var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveFinancialYears();
    getAllActiveEmployees();
    getAllActiveEmployeeTypes();
    getAllActiveAllowanceTypes();
    getAllEmployeeProfiles();
});

function getAllActiveFinancialYears() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/financialYear/getAllActive',
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
                    $('#financial_year').append('<option value="' + value.id + '">' + value.financialYear + '</option>');
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

function getAllActiveEmployees() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employee/getAllActive',
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
                    $('#employee').append('<option value="' + value.id + '">' + value.fullName + ' - ' + value.employeeNumber + '</option>');
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

function getAllActiveEmployeeTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/getAllActive',
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
                    $('#employee_type').append('<option value="' + value.id + '">' + value.employeeType + '</option>');
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

function getAllActiveAllowanceTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/allowanceType/getAllActive',
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
                    $('#allowance_type_div').append('<div class="col-md-2"><div class="form-group"><div class="form-check"><input type="checkbox" class="form-check-input" name="allowance_type" value=' + value.id + '><label class="form-check-label">' + value.allowanceType + '</label></div></div></div>');
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

function saveUpdateEmployeeProfile() {
    var id = $.trim($('#profile_id').val());
    var financialYearId = $.trim($('#financial_year').val());
    var employeeId = $.trim($('#employee').val());
    var employeeTypeId = $.trim($('#employee_type').val());
    var status = $.trim($('#profile_status').val());
    var allowanceTypes = [];
    $.each($("input[name='allowance_type']:checked"), function () {
        allowanceTypes.push({
            id: $(this).val()
        });
    });

    var employeeProfileObj = {
        "id": id,
        "financialYearId": financialYearId,
        "employeeId": employeeId,
        "employeeTypeId": employeeTypeId,
        "allowanceTypes": allowanceTypes,
        "status": status,
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeProfileObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeProfileFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployeeProfiles() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeProfileTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeProfileEdit(id) {
    $("#profile_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/getById/' + id,
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
                $('#profile_id').val(content.id);
                $('#financial_year').val(content.financialYearId);
                $('#employee').val(content.employeeId);
                $('#employee_type').val(content.employeeTypeId);
                $('#profile_status').val(content.status);
                $('#saveBtn').val("Update");
                var allowanceTypes = content.allowanceTypes;
                console.log(allowanceTypes);
                $.each(allowanceTypes, function (key, value) {
                    $('input[name=allowance_type][value=' + value.id + ']').prop("checked", "true");
                });
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteEmployeeProfile(id) {
    delete_id = id;
}

function delete_employee_profile() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/deleteById/' + delete_id,
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
                clearEmployeeProfileFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeProfileTable(employeeProfiles) {
    if ($.isEmptyObject(employeeProfiles)) {
        $('#profile_table').DataTable().clear();
        $('#profile_table').DataTable({
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
        $("#profile_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Employee Profile Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Employee Profile Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Employee Profile Details',
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
            "data": employeeProfiles,
            "columns": [{
                "data": "financialYear"
            },
                {
                    "data": "employee"
                },
                {
                    "data": "employeeFullName"
                },
                {
                    "data": "employeeType"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeProfileEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#profileBackdrop" title="Delete" onclick=deleteEmployeeProfile(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeProfileFields() {
    $('#profile_id').val('');
    $('#financial_year').val('NONE');
    $('#employee').val('NONE');
    $('#employee_type').val('NONE');
    $('#profile_status').val('NONE');
    $('#saveBtn').val("Create");
    $(':checkbox').each(function () {
        this.checked = false;
    });
    getAllEmployeeProfiles();
}

function toggleAllowanceTypes(source) {
    var allowanceTypeCheckboxes = document.querySelectorAll('input[name="allowance_type"]');
    for (var i = 0; i < allowanceTypeCheckboxes.length; i++) {
        if (allowanceTypeCheckboxes[i] != source)
            allowanceTypeCheckboxes[i].checked = source.checked;
    }
}