var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllProjectTypes();
});

function saveUpdateProjectType() {
    var id = $.trim($('#project_type_id').val());
    var projectType = $.trim($('#project_type').val());
    var description = $.trim($('#project_type_description').val());
    var status = $.trim($('#project_type_status').val());

    var projectTypeObj = {
        "id": id,
        "projectType": projectType,
        "description": description,
        "status": status
    }

    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/projectType/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(projectTypeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearProjectTypeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllProjectTypes() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/projectType/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setProjectTypeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function projectTypeEdit(id) {
    $("#project_type_id").val(id);
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/projectType/getById/' + id,
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
                $('#project_type_id').val(content.id);
                $('#project_type').val(content.projectType);
                $('#project_type_description').val(content.description);
                $('#project_type_status').val(content.status);
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

function deleteProjectType(id) {
    delete_id = id;
}

function delete_project_type() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/projectType/deleteById/' + delete_id,
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
                clearProjectTypeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setProjectTypeTable(projectTypes) {
    if ($.isEmptyObject(projectTypes)) {
        $('#project_type_table').DataTable().clear();
        $('#project_type_table').DataTable({
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
        $("#project_type_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Project Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Project Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Project Type Details',
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
            "data": projectTypes,
            "columns": [{
                "data": "projectType"
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
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=projectTypeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#projectTypeBackdrop" title="Delete" onclick=deleteProjectType(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearProjectTypeFields() {
    $('#project_type_id').val('');
    $('#project_type').val('');
    $('#project_type_description').val('');
    $('#project_type_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllProjectTypes();
}