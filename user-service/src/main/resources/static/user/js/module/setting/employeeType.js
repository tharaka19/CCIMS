var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllEmployeeTypes();
});

function saveUpdateEmployeeType() {
    var id = $.trim($('#employee_type_id').val());
    var employeeType = $.trim($('#employee_type').val());
    var description = $.trim($('#employee_type_description').val());
    var basicPay = $.trim($('#employee_type_basic_pay').val());
    var epf = $.trim($('#employee_type_epf').val());
    var epfCoContribution = $.trim($('#employee_type_epf_co_contribution').val());
    var totalEpf = $.trim($('#employee_type_total_epf').val());
    var etf = $.trim($('#employee_type_etf').val());
    var status = $.trim($('#employee_type_status').val());

    var employeeTypeObj = {
        "id": id,
        "employeeType": employeeType,
        "description": description,
        "basicPay": basicPay,
        "epf": epf,
        "epfCoContribution": epfCoContribution,
        "totalEpf": totalEpf,
        "etf": etf,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeTypeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeTypeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployeeTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeTypeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeTypeEdit(id) {
    $("#employee_type_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/getById/' + id,
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
                $('#employee_type_id').val(content.id);
                $('#employee_type').val(content.employeeType);
                $('#employee_type_description').val(content.description);
                $('#employee_type_basic_pay').val(content.basicPay);
                $('#employee_type_epf').val(content.epf);
                $('#employee_type_epf_co_contribution').val(content.epfCoContribution);
                $('#employee_type_total_epf').val(content.totalEpf);
                $('#employee_type_etf').val(content.etf);
                $('#employee_type_status').val(content.status);
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

function deleteEmployeeType(id) {
    delete_id = id;
}

function delete_employee_type() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/deleteById/' + delete_id,
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
                clearEmployeeTypeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeTypeTable(useRoles) {
    if ($.isEmptyObject(useRoles)) {
        $('#employee_type_table').DataTable().clear();
        $('#employee_type_table').DataTable({
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
        $("#employee_type_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Assignment  Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Assignment Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Assignment Details',
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
                "data": "employeeType"
            },
                {
                    "data": "description"
                },
                {
                    "data": "totalEpf"
                },
                {
                    "data": "etf"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeTypeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#employeeTypeBackdrop" title="Delete" onclick=deleteEmployeeType(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeTypeFields() {
    $('#employee_type_id').val('');
    $('#employee_type').val('');
    $('#employee_type_description').val('');
    $('#employee_type_basic_pay').val('');
    $('#employee_type_epf').val('');
    $('#employee_type_epf_co_contribution').val('');
    $('#employee_type_total_epf').val('');
    $('#employee_type_etf').val('');
    $('#employee_type_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEmployeeTypes();
}

function calculateTotalEPF(epfCoContribution) {
    var epf = parseFloat($.trim($('#employee_type_epf').val()));
    var total = epf + parseFloat(epfCoContribution);
    $('#employee_type_total_epf').val(total);
}