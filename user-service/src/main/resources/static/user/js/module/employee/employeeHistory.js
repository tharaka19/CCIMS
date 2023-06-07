var token;
var delete_id;
var isPDFExtension = false;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }

    getAllActiveFinancialYears();
    getAllEmployeeHistories();

    $("#doc_file").change(function (e) {
        fileUploadValidation(this);
    });
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
                    $('#employee').append('<option class="employee_code" value="' + value.employeeId + '">' + value.nameWithInitials + '</option>');
                });
                $('#employee').val(employeeId);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function fileUploadValidation(input) {
    var extension = $('.upload_sd_img').val().replace(/^.*\./, '');
    if (extension == 'pdf') {
        isPDFExtension = true;
    }
    resetPreViewEmployeeHistoryImage();
    resetEmployeeHistoryPDF();
    if (isPDFExtension) {
        preViewEmployeeHistoryPDF(URL.createObjectURL(input.files[0]));
    } else {
        preViewEmployeeHistoryImage(URL.createObjectURL(input.files[0]));
    }
}

function uploadEmployeeHistory() {
    var formData = new FormData();
    formData.append("file", doc_file.files[0]);
    formData.append("fileType", 'HISTORY');

    $.ajax({
        url: BASE_URL + USER_SERVICE + '/file/upload',
        type: 'POST',
        headers: {
            'Authorization': 'Bearer ' + token
        },
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {
            if (data.code == "00") {
                saveUpdateEmployeeHistory(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateEmployeeHistory(fileName) {
    var id = $.trim($('#employee_history_id').val());
    var caption = $.trim($('#history_caption').val());
    var reminder = $.trim($('#history_reminder').val());
    var financialYearId = $.trim($('#financial_year').val());
    var employeeId = $.trim($('#employee').val());
    var status = $.trim($('#history_status').val());

    var employeeHistoryObj = {
        "id": id,
        "fileName": fileName,
        "caption": caption,
        "reminder": reminder,
        "financialYearId": financialYearId,
        "employeeId": employeeId,
        "status": status,
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeHistory/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeHistoryObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeHistoryFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployeeHistories() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeHistory/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeHistoryTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeHistoryEdit(id) {
    $("#employee_document_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeHistory/getById/' + id,
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
                $('#employee_history_id').val(content.id);
                $('#financial_year').val(content.financialYearId);
                $('#history_caption').val(content.caption);
                $('#history_reminder').val(content.reminder);
                $('#history_status').val(content.status);
                $('#saveBtn').val("Update");
                setActiveEmployeeProfileByFinancialYear(content.financialYearId, content.employeeId);
                if (content.pdf || ((content.fileName).toLowerCase().indexOf("pdf") >= 0)) {
                    getEmployeeHistoryPDF(content.id, content.fileName);
                } else {
                    getEmployeeHistoryImage(content.id, content.fileName);
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

function deleteEmployeeHistory(id) {
    delete_id = id;
}

function delete_employee_history() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeHistory/deleteById/' + delete_id,
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
                clearEmployeeHistoryFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeHistoryTable(employeeHistories) {
    if ($.isEmptyObject(employeeHistories)) {
        $('#history_table').DataTable().clear();
        $('#history_table').DataTable({
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
        $("#history_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Employee History Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Employee History Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Employee History Details',
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
            "data": employeeHistories,
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
                    "data": "caption"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeHistoryEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#historyBackdrop" title="Delete" onclick=deleteEmployeeHistory(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeHistoryFields() {
    $('#employee_history_id').val('');
    $('#financial_year').val('NONE');
    $('#employee').val('NONE');
    $('#history_caption').val('');
    $('#history_reminder').val('');
    $('#history_status').val('NONE');
    $('#saveBtn').val("Update");
    resetPreViewEmployeeHistoryImage();
    resetEmployeeHistoryPDF();
    getAllEmployeeHistories();
}


function getEmployeeHistoryImage(id, employeeHistoryName) {
    var url = BASE_URL + USER_SERVICE + '/file/imgDownloader/' + id + '/' + employeeHistoryName + '/HISTORY';
    preViewEmployeeHistoryImage(url)
}

function getEmployeeHistoryPDF(id, employeeHistoryName) {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/file/pdfDownloader/' + id + '/' + employeeHistoryName + '/HISTORY',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token,
        },
        data: {},
        success: function (data) {
            console.log(data);
            if (data != "" && data.length != 0) {
                $.each(data, function (key, value) {
                    console.log(key, value.byteArray);
                    const byteCharacters = atob(value.byteArray);
                    const byteNumbers = new Array(byteCharacters.length);
                    for (let i = 0; i < byteCharacters.length; i++) {
                        byteNumbers[i] = byteCharacters.charCodeAt(i);
                    }
                    const byteArray = new Uint8Array(byteNumbers);
                    const file = new Blob([byteArray], {type: 'application/pdf'});
                    const url = URL.createObjectURL(file);
                    preViewEmployeeHistoryPDF(url)
                });
            } else {
                resetEmployeeHistoryPDF();
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function preViewEmployeeHistoryImage(url) {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-img">');
    $('#preview_employee_file_div').append('<img src=' + url + ' id="preview_employee_file_image" alt="Employee Document Image" style="max-width:100%; max-height:100%;">');
    $('#preview_employee_file_div').append('</div>');
}

function preViewEmployeeHistoryPDF(url) {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-pdf">');
    $('#preview_employee_file_div').append('<embed src=' + url + ' id="preview_employee_file_pdf" type="application/pdf" alt="Employee Document PDF" style="width:100%; height:500px;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetPreViewEmployeeHistoryImage() {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-img">');
    $('#preview_employee_file_div').append('<img src="/user/images/default_image.jpg" id="preview_employee_file_image" alt="Employee Document Image" style="max-width:100%; max-height:100%;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetEmployeeHistoryPDF() {
    const embed = document.getElementById('preview_employee_file_pdf');
    if (embed != null) {
        embed.setAttribute('src', "");
        embed.setAttribute('width', "0px");
        embed.setAttribute('height', "0px");
    }
}