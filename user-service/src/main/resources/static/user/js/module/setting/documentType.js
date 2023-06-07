var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllDocumentTypes();
});

function saveUpdateDocumentType() {
    var id = $.trim($('#document_type_id').val());
    var documentType = $.trim($('#document_type').val());
    var description = $.trim($('#document_type_description').val());
    var status = $.trim($('#document_type_status').val());

    var documentTypeObj = {
        "id": id,
        "documentType": documentType,
        "description": description,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/documentType/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(documentTypeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearDocumentTypeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllDocumentTypes() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/documentType/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setDocumentTypeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function documentTypeEdit(id) {
    $("#document_type_id").val(id);
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/documentType/getById/' + id,
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
                $('#document_type_id').val(content.id);
                $('#document_type').val(content.documentType);
                $('#document_type_description').val(content.description);
                $('#document_type_status').val(content.status);
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

function deleteDocumentType(id) {
    delete_id = id;
}

function delete_document_type() {
    $.ajax({
        url: BASE_URL + EMPLOYEE_SERVICE + '/documentType/deleteById/' + delete_id,
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
                clearDocumentTypeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setDocumentTypeTable(documentTypes) {
    if ($.isEmptyObject(documentTypes)) {
        $('#document_type_table').DataTable().clear();
        $('#document_type_table').DataTable({
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
        $("#document_type_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Document Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Document Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Document Type Details',
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
            "data": documentTypes,
            "columns": [{
                "data": "documentType"
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
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=documentTypeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#documentTypeBackdrop" title="Delete" onclick=deleteDocumentType(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearDocumentTypeFields() {
    $('#document_type_id').val('');
    $('#document_type').val('');
    $('#document_type_description').val('');
    $('#document_type_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllDocumentTypes();
}