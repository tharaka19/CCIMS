var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllFinancialYears();
});

function saveUpdateFinancialYear() {
    var id = $.trim($('#financial_year_id').val());
    var financialYear = $.trim($('#financial_year_name').val());
    var term = $.trim($('#term_name').val());
    var startDate = $.trim($('#start_date').val());
    var endDate = $.trim($('#end_date').val());
    var status = $.trim($('#financial_year_status').val());

    var financialYearObj = {
        "id": id,
        "financialYear": financialYear + '-' + term,
        "startDate": startDate,
        "endDate": endDate,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/financialYear/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(financialYearObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearFinancialYearFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllFinancialYears() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/financialYear/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setFinancialYearTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function financialYearEdit(id) {
    $("#user_role_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/financialYear/getById/' + id,
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
                $('#financial_year_id').val(content.id);
                $('#financial_year_name').val((content.financialYear).split("-")[0]);
                $('#term_name').val((content.financialYear).split("-")[1]);
                $('#start_date').val(content.startDate);
                $('#end_date').val(content.endDate);
                $('#financial_year_status').val(content.status);
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

function deleteFinancialYear(id) {
    delete_id = id;
}

function delete_financial_year() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/financialYear/deleteById/' + delete_id,
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
                clearFinancialYearFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setFinancialYearTable(financialYears) {
    if ($.isEmptyObject(financialYears)) {
        $('#financial_Year_table').DataTable().clear();
        $('#financial_Year_table').DataTable({
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
        $("#financial_Year_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Financial Year Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Financial Year Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Financial Year Details',
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
            "data": financialYears,
            "columns": [{
                "data": "financialYear"
            },
                {
                    "data": "startDate"
                },
                {
                    "data": "endDate"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=financialYearEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#financialYearBackdrop" title="Delete" onclick=deleteFinancialYear(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearFinancialYearFields() {
    $('#financial_year_id').val('');
    $('#financial_year_name').val('');
    $('#term_name').val('');
    $('#start_date').val('');
    $('#end_date').val('');
    $('#financial_year_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllFinancialYears();
}