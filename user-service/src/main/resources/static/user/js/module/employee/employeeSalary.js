var token;
var delete_id;
var isPDFExtension = false;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }

    getAllActiveFinancialYears();
    getAllEmployeeSalaries();
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

function getAllActiveEmployeeProfilesByFinancialYear(financialYearId) {
    $(".employee_code").remove();
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/getAllActiveByFinancialYear/' + financialYearId,
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
                    $('#employee').append('<option class="employee_code" value="' + value.employeeId + '">' + value.employeeFullName + ' - ' + value.employee + '</option>');
                });
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getTotalAllowanceByEmployeeProfile(employeeId) {
    var financialYearId = $.trim($('#financial_year').val());
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/getByEmployeeAndFinancialYear/' + financialYearId + '/' + employeeId,
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
                getEmployeeTypeById(content.employeeTypeId);

                var totalAllowance = 0;
                $.each(content.allowanceTypes, function (key, value) {
                    totalAllowance += value.allowancePay;
                });
                $('#employee_total_allowance').val(totalAllowance);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getEmployeeTypeById(employeeTypeId) {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeType/getById/' + employeeTypeId,
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
                $('#employee_type_basic_pay').val(content.basicPay);
                $('#employee_type_epf').val(content.epf);
                $('#employee_type_epf_co_contribution').val(content.epfCoContribution);
                $('#employee_type_total_epf').val(content.totalEpf);
                $('#employee_type_etf').val(content.etf);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setActiveEmployeeProfileByFinancialYear(financialYearId, employeeId) {
    $(".employee_code").remove();
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeProfile/getAllActiveByFinancialYear/' + financialYearId,
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
                    $('#employee').append('<option class="employee_code" value="' + value.employeeId + '">' + value.employeeFullName + ' - ' + value.employee + '</option>');
                });
                $('#employee').val(employeeId);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateEmployeeSalary() {
    var id = $.trim($('#employee_salary_id').val());
    var totalAllowance = $.trim($('#employee_total_allowance').val());
    var totalOT = $.trim($('#employee_total_ot').val());
    var financialYearId = $.trim($('#financial_year').val());
    var employeeId = $.trim($('#employee').val());
    var salaryMonth = $.trim($('#salary_month').val());

    var employeeSalaryObj = {
        "id": id,
        "totalAllowance": totalAllowance,
        "totalOT": totalOT,
        "salaryMonth": salaryMonth,
        "financialYearId": financialYearId,
        "employeeId": employeeId,
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeSalary/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeSalaryObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeSalaryFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployeeSalaries() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeSalary/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeSalaryTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeSalaryEdit(id) {
    $("#employee_salary_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeSalary/getById/' + id,
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
                $('#employee_salary_id').val(content.id);
                $('#financial_year').val(content.financialYearId);
                $('#salary_month').val(content.salaryMonth);
                $('#employee_total_ot').val(content.totalOT);
                $('#saveBtn').val("Update");
                setActiveEmployeeProfileByFinancialYear(content.financialYearId, content.employeeId);
                getTotalAllowanceByEmployeeProfile(content.employeeId);

            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteEmployeeSalary(id) {
    delete_id = id;
}

function delete_employee_salary() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeSalary/deleteById/' + delete_id,
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
                clearEmployeeSalaryFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeSalaryTable(employeeSalaries) {
    if ($.isEmptyObject(employeeSalaries)) {
        $('#salary_table').DataTable().clear();
        $('#salary_table').DataTable({
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
        $("#salary_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Employee Salary Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Employee Salary Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Employee Salary Details',
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
            "data": employeeSalaries,
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
                    "data": "salaryMonth"
                },
                {
                    "data": "totalAllowance"
                },
                {
                    "data": "totalOT"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeSalaryEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-success edit" data-bs-toggle="modal" data-bs-target="#paySlipBackdrop" title="Update Stock" onclick=getPaySlip(value)><i class="bx bx-adjust"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#salaryBackdrop" title="Delete" onclick=deleteEmployeeSalary(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeSalaryFields() {
    $('#employee_salary_id').val('');
    $('#employee_type_basic_pay').val('');
    $('#employee_type_epf').val('');
    $('#employee_type_epf_co_contribution').val('');
    $('#employee_type_total_epf').val('');
    $('#employee_type_etf').val('');
    $('#employee_total_allowance').val('');
    $('#employee_total_ot').val('');
    $('#salary_month').val('NONE');
    $('#financial_year').val('NONE');
    $('#employee').val('NONE');
    $('#saveBtn').val("Create");
    resetPreViewEmployeePaySlipImage();
    resetEmployeePaySlipPDF();
    getAllEmployeeSalaries();
}

function getPaySlip(id) {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeSalary/exportPaySlip/' + id,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data != "" && data.length != 0) {
                $.each(data, function (key, value) {
                    const byteCharacters = atob(value.byteArray);
                    const byteNumbers = new Array(byteCharacters.length);
                    for (let i = 0; i < byteCharacters.length; i++) {
                        byteNumbers[i] = byteCharacters.charCodeAt(i);
                    }
                    const byteArray = new Uint8Array(byteNumbers);
                    const file = new Blob([byteArray], {type: 'application/pdf'});
                    const url = URL.createObjectURL(file);
                    preViewEmployeePaySlipPDF(url);
                });
            } else {
                resetEmployeePaySlipPDF();
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function preViewEmployeePaySlipPDF(url) {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-pdf">');
    $('#preview_employee_file_div').append('<embed src=' + url + ' id="preview_employee_file_pdf" type="application/pdf" alt="Employee Pay Slip PDF" style="width:100%; height:500px;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetPreViewEmployeePaySlipImage() {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-img">');
    $('#preview_employee_file_div').append('<img src="/user/images/default_image.jpg" id="preview_employee_file_image" alt="Employee Pay Slip Image" style="max-width:100%; max-height:100%;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetEmployeePaySlipPDF() {
    const embed = document.getElementById('preview_employee_file_pdf');
    if (embed != null) {
        embed.setAttribute('src', "");
        embed.setAttribute('width', "0px");
        embed.setAttribute('height', "0px");
    }
}