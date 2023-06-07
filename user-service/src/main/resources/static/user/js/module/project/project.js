var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveProjectTypes();
    getAllProjects();

    $("#doc_file").change(function (e) {
        fileUploadValidation(this);
    });
});

function getAllActiveProjectTypes() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/projectType/getAllActive',
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
                    $('#project_type').append('<option value="' + value.id + '">' + value.projectType + '</option>');
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
    resetPreViewProjectDocumentImage();
    preViewProjectDocumentImage(URL.createObjectURL(input.files[0]));
}

function uploadProjectImage() {
    var formData = new FormData();
    formData.append("file", doc_file.files[0]);
    formData.append("fileType", 'PROJECT');

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
                saveUpdateProject(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateProject(fileName) {
    var id = $.trim($('#project_id').val());
    var projectNumber = $.trim($('#project_number').val());
    var projectName = $.trim($('#project_name').val());
    var projectDetails = $.trim($('#project_details').val());
    var projectPrice = $.trim($('#project_price').val());
    var projectTypeId = $.trim($('#project_type').val());
    var status = $.trim($('#project_status').val());

    var projectObj = {
        "id": id,
        "projectNumber": projectNumber,
        "projectName": projectName,
        "projectDetails": projectDetails,
        "projectPrice": projectPrice,
        "fileName": fileName,
        "projectTypeId": projectTypeId,
        "status": status
    }

    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/project/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(projectObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearProjectFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllProjects() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/project/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setProjectTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function projectEdit(id) {
    $("#project_id").val(id);
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/project/getById/' + id,
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
                $('#project_id').val(content.id);
                $('#project_number').val(content.projectNumber);
                $('#project_name').val(content.projectName);
                $('#project_details').val(content.projectDetails);
                $('#project_price').val(content.projectPrice);
                $('#project_type').val(content.projectTypeId);
                $('#project_status').val(content.status);
                $('#saveBtn').val("Update");
                getProjectDocumentImage(content.id, content.fileName);
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteProject(id) {
    delete_id = id;
}

function delete_project() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/project/deleteById/' + delete_id,
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
                clearProjectFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setProjectTable(projects) {
    if ($.isEmptyObject(projects)) {
        $('#project_table').DataTable().clear();
        $('#project_table').DataTable({
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
        $("#project_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Project Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Project Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Project Details',
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
            "data": projects,
            "columns": [{
                "data": "projectType"
            },
                {
                    "data": "projectNumber"
                },
                {
                    "data": "projectName"
                },
                {
                    "data": "projectPrice"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=projectEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#projectBackdrop" title="Delete" onclick=deleteProject(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearProjectFields() {
    $('#project_id').val('');
    $('#project_number').val('');
    $('#project_name').val('');
    $('#project_details').val('');
    $('#project_price').val('');
    $('#project_type').val('NONE');
    $('#project_status').val('NONE');
    $('#saveBtn').val("Create");
    resetPreViewProjectDocumentImage();
    getAllProjects();
}

function getProjectDocumentImage(id, projectImageName) {
    var url = BASE_URL + USER_SERVICE + '/file/imgDownloader/' + id + '/' + projectImageName + '/PROJECT';
    preViewProjectDocumentImage(url)
}

function preViewProjectDocumentImage(url) {
    $('#preview_project_file_div').html('');
    $('#preview_project_file_div').append('<div class="card-img">');
    $('#preview_project_file_div').append('<img src=' + url + ' id="preview_project_file_image" alt="Project Image" style="max-width:100%; max-height:100%;">');
    $('#preview_project_file_div').append('</div>');
}

function resetPreViewProjectDocumentImage() {
    $('#preview_project_file_div').html('');
    $('#preview_project_file_div').append('<div class="card-img">');
    $('#preview_project_file_div').append('<img src="/user/images/default_image.jpg" id="preview_project_file_image" alt="Project Image" style="max-width:100%; max-height:100%;">');
    $('#preview_project_file_div').append('</div>');
}