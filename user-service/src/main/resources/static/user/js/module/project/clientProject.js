var token;
var delete_id;
var isPDFExtension = false;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveProjects();
    getAllActiveClients();
    getAllClientProjects();

    $("#doc_file").change(function (e) {
        fileUploadValidation(this);
    });
});

function getAllActiveProjects() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/project/getAllActive',
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
                    $('#project').append('<option value="' + value.id + '">' + value.projectNumber + '</option>');
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

function getAllActiveClients() {
    $.ajax({
        url: BASE_URL + CLIENT_SERVICE + '/client/getAllActive',
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
                    $('#client').append('<option value="' + value.id + '">' + value.fullName + '</option>');
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

function getAllActiveEquipmentTypes() {
    $(".equipment_type").remove();
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentType/getAllActive',
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
                    $('#equipment_type').append('<option class="equipment_type" value="' + value.id + '">' + value.equipmentType + '</option>');
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

function getAllActiveEquipmentsByEquipmentType(equipmentTypeId) {
    $(".equipment").remove();
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipment/getAllActiveByEquipmentType/' + equipmentTypeId,
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
                    $('#equipment').append('<option class="equipment" value="' + value.id + '">' + value.equipmentName + '</option>');
                });
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getEquipmentStockByEquipmentId(equipmentId) {
    $('#available_quantity').val('');
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/getByEquipmentId/' + equipmentId,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                $('#equipment_stock_id').val(data.content.id);
                $('#available_quantity').val(data.content.availableQuantity);
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
    resetPreViewClientProjectImage();
    resetClientProjectPDF();
    if (isPDFExtension) {
        preViewClientProjectPDF(URL.createObjectURL(input.files[0]));
    } else {
        preViewClientProjectImage(URL.createObjectURL(input.files[0]));
    }
}

function uploadClientProjectFile() {
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
                saveUpdateClientProject(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateClientProject(fileName) {
    var id = $.trim($('#client_project_id').val());
    var projectNumber = $.trim($('#client_project_number').val());
    var projectDetails = $.trim($('#client_project_details').val());
    var projectStartDate = $.trim($('#project_start_date').val());
    var projectId = $.trim($('#project').val());
    var clientId = $.trim($('#client').val());

    var clientProjectObj = {
        "id": id,
        "projectNumber": projectNumber,
        "projectDetails": projectDetails,
        "projectStartDate": projectStartDate,
        "fileName": fileName,
        "projectId": projectId,
        "clientId": clientId
    }

    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProject/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(clientProjectObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearClientProjectFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateClientProjectEquipmentStock() {
    var availableQuantity = $.trim($('#available_quantity').val());
    var operation = $.trim($('#equipment_stock_operation').val());
    var equipmentQuantity = $.trim($('#equipment_quantity').val());
    var stockNote = $.trim($('#equipment_stock_note').val());
    var clientProjectId = $.trim($('#client_project_id').val());
    var equipmentId = $.trim($('#equipment').val());
    var equipmentStockId = $.trim($('#equipment_stock_id').val());

    var clientProjectStockObj = {
        "availableQuantity": availableQuantity,
        "operation": operation,
        "equipmentQuantity": equipmentQuantity,
        "stockNote": stockNote,
        "clientProjectId": clientProjectId,
        "equipmentId": equipmentId,
        "equipmentStockId": equipmentStockId,
    }

    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProjectStock/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(clientProjectStockObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearClientProjectEquipmentStockFields();
                clientProjectEquipmentStockEdit(clientProjectId);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllClientProjects() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProject/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setClientProjectTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllClientProjectEquipmentStockByClientProject(clientProjectId) {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProjectStock/getAllByClientProject/' + clientProjectId,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setClientProjectEquipmentTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function clientProjectEdit(id) {
    $("#client_project_id").val(id);
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProject/getById/' + id,
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
                $('#client_project_id').val(content.id);
                $('#client_project_number').val(content.projectNumber);
                $('#client_project_details').val(content.projectDetails);
                $('#project_start_date').val(content.projectStartDate);
                $('#project').val(content.projectId);
                $('#client').val(content.clientId);
                $('#saveBtn').val("Update");

                if (content.pdf || ((content.fileName).toLowerCase().indexOf("pdf") >= 0)) {
                    getClientProjectPDF(content.id, content.fileName);
                } else {
                    getClientProjectImage(content.id, content.fileName);
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

function clientProjectEquipmentStockEdit(id) {
    $('#client_project_id').val(id);
    getAllActiveEquipmentTypes();
    getAllClientProjectEquipmentStockByClientProject(id);
}

function deleteClientProject(id) {
    delete_id = id;
}

function delete_client_project() {
    $.ajax({
        url: BASE_URL + PROJECT_SERVICE + '/clientProject/deleteById/' + delete_id,
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
                clearClientProjectFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setClientProjectTable(clientProjects) {
    if ($.isEmptyObject(clientProjects)) {
        $('#client_project_table').DataTable().clear();
        $('#client_project_table').DataTable({
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
        $("#client_project_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Client Project Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Client Project Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Client Project Details',
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
            "data": clientProjects,
            "columns": [{
                "data": "projectNumber"
            },
                {
                    "data": "projectStartDate"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=clientProjectEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-success edit" data-bs-toggle="modal" data-bs-target="#clientProjectUpdateBackdrop" title="Update Stock" onclick=clientProjectEquipmentStockEdit(value)><i class="bx bx-adjust"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#clientProjectBackdrop" title="Delete" onclick=deleteClientProject(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function setClientProjectEquipmentTable(clientProjectEquipmentStock) {
    if ($.isEmptyObject(clientProjectEquipmentStock)) {
        $('#client_project_equipment_stock_table').DataTable().clear();
        $('#client_project_equipment_stock_table').DataTable({
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
        $("#client_project_equipment_stock_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Client Project Equipment Stock Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Client Project Equipment Stock Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Client Project Equipment Stock Details',
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
            "data": clientProjectEquipmentStock,
            "columns": [{
                "data": "projectNumber"
            },
                {
                    "data": "operation"
                },
                {
                    "data": "equipmentQuantity"
                },
                {
                    "data": "stockNote"
                },
                {
                    "data": "date"
                }
            ]
        });
    }
}

function clearClientProjectFields() {
    $('#client_project_id').val('');
    $('#client_project_number').val('');
    $('#client_project_details').val('');
    $('#project_start_date').val('');
    $('#project').val('NONE');
    $('#client').val('NONE');
    $('#saveBtn').val("Create");
    resetPreViewClientProjectImage();
    resetClientProjectPDF();
}

function clearClientProjectEquipmentStockFields() {
    $('#equipment_type').val('NONE');
    $('#equipment').val('NONE');
    $('#available_quantity').val('');
    $('#equipment_stock_operation').val('NONE');
    $('#equipment_quantity').val('');
    $('#equipment_stock_note').val('');
}

function getClientProjectImage(id, projectImageName) {
    var url = BASE_URL + USER_SERVICE + '/file/imgDownloader/' + id + '/' + projectImageName + '/PROJECT';
    preViewClientProjectImage(url)
}

function getClientProjectPDF(id, projectPDFName) {
    $.ajax({
        url: BASE_URL + USER_SERVICE + '/file/pdfDownloader/' + id + '/' + projectPDFName + '/PROJECT',
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
                    preViewClientProjectPDF(url)
                });
            } else {
                resetClientProjectPDF();
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function preViewClientProjectImage(url) {
    $('#preview_client_project_file_div').html('');
    $('#preview_client_project_file_div').append('<div class="card-img">');
    $('#preview_client_project_file_div').append('<img src=' + url + ' id="preview_client_project_file_image" alt="Client Project Image" style="max-width:100%; max-height:100%;">');
    $('#preview_client_project_file_div').append('</div>');
}

function preViewClientProjectPDF(url) {
    $('#preview_client_project_file_div').html('');
    $('#preview_client_project_file_div').append('<div class="card-pdf">');
    $('#preview_client_project_file_div').append('<embed src=' + url + ' id="preview_client_project_file_pdf" type="application/pdf" alt="Client Project PDF" style="width:100%; height:500px;">');
    $('#preview_client_project_file_div').append('</div>');
}

function resetPreViewClientProjectImage() {
    $('#preview_client_project_file_div').html('');
    $('#preview_client_project_file_div').append('<div class="card-img">');
    $('#preview_client_project_file_div').append('<img src="/user/images/default_image.jpg" id="preview_client_project_file_image" alt="Client Project Image" style="max-width:100%; max-height:100%;">');
    $('#preview_client_project_file_div').append('</div>');
}

function resetClientProjectPDF() {
    const embed = document.getElementById('preview_client_project_file_pdf');
    if (embed != null) {
        embed.setAttribute('src', "");
        embed.setAttribute('width', "0px");
        embed.setAttribute('height', "0px");
    }
}