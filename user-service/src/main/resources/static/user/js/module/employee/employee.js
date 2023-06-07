var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllEmployees();
});

function saveUpdateEmployee() {
    var id = $.trim($('#employee_id').val());
    var employeeNumber = $.trim($('#employee_number').val());
    var nameWithInitials = $.trim($('#employee_name_with_initials').val());
    var firstName = $.trim($('#employee_first_name').val());
    var lastName = $.trim($('#employee_last_name').val());
    var fullName = $.trim($('#employee_full_name').val());
    var dob = $.trim($('#employee_date_of_birth').val());
    var nic = $.trim($('#employee_nic').val());
    var joinDate = $.trim($('#employee_join_date').val());
    var mobileNumber = $.trim($('#employee_mobile_number').val());
    var homeNumber = $.trim($('#employee_home_number').val());
    var homeAddress = $.trim($('#employee_home_address').val());
    var status = $.trim($('#employee_status').val());
    var gender = 'NONE';
    if (document.getElementById('male').checked) {
        gender = document.getElementById('male').value;
    } else if (document.getElementById('female').checked) {
        gender = document.getElementById('female').value;
    }

    var employeeObj = {
        "id": id,
        "employeeNumber": employeeNumber,
        "nameWithInitials": nameWithInitials,
        "firstName": firstName,
        "lastName": lastName,
        "fullName": fullName,
        "dob": dob,
        "nic": nic,
        "joinDate": joinDate,
        "mobileNumber": mobileNumber,
        "homeNumber": homeNumber,
        "homeAddress": homeAddress,
        "gender": gender,
        "status": status,
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employee/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployees() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employee/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeEdit(id) {
    $("#employee_type_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employee/getById/' + id,
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
                $('#employee_id').val(content.id);
                $('#employee_number').val(content.employeeNumber);
                $('#employee_name_with_initials').val(content.nameWithInitials);
                $('#employee_first_name').val(content.firstName);
                $('#employee_last_name').val(content.lastName);
                $('#employee_full_name').val(content.fullName);
                $('#employee_date_of_birth').val(content.dob);
                $('#employee_nic').val(content.nic);
                $('#employee_join_date').val(content.joinDate);
                $('#employee_mobile_number').val(content.mobileNumber);
                $('#employee_home_number').val(content.homeNumber);
                $('#employee_home_address').val(content.homeAddress);
                $('#employee_status').val(content.status);
                $('#saveBtn').val("Update");
                if (content.gender == "MALE") {
                    $("#male").prop("checked", true);
                } else {
                    $("#female").prop("checked", true);
                }
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteEmployee(id) {
    delete_id = id;
}

function delete_employee() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employee/deleteById/' + delete_id,
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
                clearEmployeeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeTable(employees) {
    if ($.isEmptyObject(employees)) {
        $('#employee_table').DataTable().clear();
        $('#employee_table').DataTable({
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
        $("#employee_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Employee Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Employee Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Employee Details',
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
            "data": employees,
            "columns": [{
                "data": "employeeNumber"
            },
                {
                    "data": "fullName"
                },
                {
                    "data": "mobileNumber"
                },
                {
                    "data": "homeNumber"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#employeeBackdrop" title="Delete" onclick=deleteEmployee(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeFields() {
    $('#employee_type_id').val('');
    $('#employee_number').val('');
    $('#employee_name_with_initials').val('');
    $('#employee_first_name').val('');
    $('#employee_last_name').val('');
    $('#employee_full_name').val('');
    $('#employee_date_of_birth').val('');
    $('#employee_nic').val('');
    $('#employee_join_date').val('');
    $('#employee_mobile_number').val('');
    $('#employee_home_number').val('');
    $('#employee_home_address').val('');
    $('#male').prop('checked', false);
    $('#female').prop('checked', false);
    $('#employee_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEmployees();
}