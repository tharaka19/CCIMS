var token;
var delete_id;
var isPDFExtension = false;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }

    getAllActiveEmployees();
    getAllActiveDocumentTypes();
    getAllEmployeeDocuments();

    $("#doc_file").change(function (e) {
        fileUploadValidation(this);
    });
});

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

function getAllActiveDocumentTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/documentType/getAllActive',
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
                    $('#document_type').append('<option value="' + value.id + '">' + value.documentType + '</option>');
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

function fileUploadValidation(input) {
    var extension = $('.upload_sd_img').val().replace(/^.*\./, '');
    if (extension == 'pdf') {
        isPDFExtension = true;
    }
    resetPreViewEmployeeDocumentImage();
    resetEmployeeDocumentPDF();
    if (isPDFExtension) {
        preViewEmployeeDocumentPDF(URL.createObjectURL(input.files[0]));
    } else {
        preViewEmployeeDocumentImage(URL.createObjectURL(input.files[0]));
    }
}

function uploadEmployeeDocument() {
    var formData = new FormData();
    formData.append("file", doc_file.files[0]);
    formData.append("fileType", 'DOCUMENT');

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
                saveUpdateEmployeeDocument(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateEmployeeDocument(fileName) {
    var id = $.trim($('#employee_document_id').val());
    var employeeId = $.trim($('#employee').val());
    var documentTypeId = $.trim($('#document_type').val());
    var status = $.trim($('#document_status').val());

    var employeeDocumentObj = {
        "id": id,
        "fileName": fileName,
        "employeeId": employeeId,
        "documentTypeId": documentTypeId,
        "status": status,
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeDocument/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(employeeDocumentObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEmployeeDocumentFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEmployeeDocuments() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeDocument/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEmployeeDocumentTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function employeeDocumentEdit(id) {
    $("#employee_document_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeDocument/getById/' + id,
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
                $('#employee_document_id').val(content.id);
                $('#employee').val(content.employeeId);
                $('#document_type').val(content.documentTypeId);
                $('#document_status').val(content.status);
                $('#saveBtn').val("Update");

                if (content.pdf || ((content.fileName).toLowerCase().indexOf("pdf") >= 0)) {
                    getEmployeeDocumentPDF(content.id, content.fileName);
                } else {
                    getEmployeeDocumentImage(content.id, content.fileName);
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

function deleteEmployeeDocument(id) {
    delete_id = id;
}

function delete_employee_document() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/employeeDocument/deleteById/' + delete_id,
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
                clearEmployeeDocumentFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEmployeeDocumentTable(employeeDocuments) {
    if ($.isEmptyObject(employeeDocuments)) {
        $('#document_table').DataTable().clear();
        $('#document_table').DataTable({
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
        $("#document_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Employee Document Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Employee Document Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Employee Document Details',
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
            "data": employeeDocuments,
            "columns": [{
                "data": "employee"
            },
                {
                    "data": "employeeFullName"
                },
                {
                    "data": "documentType"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=employeeDocumentEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#documentBackdrop" title="Delete" onclick=deleteEmployeeDocument(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEmployeeDocumentFields() {
    $('#employee_document_id').val('NONE');
    $('#employee').val('NONE');
    $('#document_type').val('NONE');
    $('#document_status').val('NONE');
    $('#saveBtn').val("Create");
    resetPreViewEmployeeDocumentImage();
    resetEmployeeDocumentPDF();
    getAllEmployeeDocuments();
}


function getEmployeeDocumentImage(id, employeeDocumentName) {
    var url = BASE_URL + USER_SERVICE + '/file/imgDownloader/' + id + '/' + employeeDocumentName + '/DOCUMENT';
    preViewEmployeeDocumentImage(url)
}

function getEmployeeDocumentPDF(id, employeeDocumentName) {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/file/pdfDownloader/' + id + '/' + employeeDocumentName + '/DOCUMENT',
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
                    preViewEmployeeDocumentPDF(url)
                });
            } else {
                resetEmployeeDocumentPDF();
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function preViewEmployeeDocumentImage(url) {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-img">');
    $('#preview_employee_file_div').append('<img src=' + url + ' id="preview_employee_file_image" alt="Employee Document Image" style="max-width:100%; max-height:100%;">');
    $('#preview_employee_file_div').append('</div>');
}

function preViewEmployeeDocumentPDF(url) {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-pdf">');
    $('#preview_employee_file_div').append('<embed src=' + url + ' id="preview_employee_file_pdf" type="application/pdf" alt="Employee Document PDF" style="width:100%; height:500px;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetPreViewEmployeeDocumentImage() {
    $('#preview_employee_file_div').html('');
    $('#preview_employee_file_div').append('<div class="card-img">');
    $('#preview_employee_file_div').append('<img src="/user/images/default_image.jpg" id="preview_employee_file_image" alt="Employee Document Image" style="max-width:100%; max-height:100%;">');
    $('#preview_employee_file_div').append('</div>');
}

function resetEmployeeDocumentPDF() {
    const embed = document.getElementById('preview_employee_file_pdf');
    if (embed != null) {
        embed.setAttribute('src', "");
        embed.setAttribute('width', "0px");
        embed.setAttribute('height', "0px");
    }
}