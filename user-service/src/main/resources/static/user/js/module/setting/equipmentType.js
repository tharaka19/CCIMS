var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllEquipmentTypes();
});

function saveUpdateEquipmentType() {
    var id = $.trim($('#equipment_type_id').val());
    var equipmentType = $.trim($('#equipment_type').val());
    var description = $.trim($('#equipment_type_description').val());
    var status = $.trim($('#equipment_type_status').val());

    var equipmentTypeObj = {
        "id": id,
        "equipmentType": equipmentType,
        "description": description,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentType/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(equipmentTypeObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEquipmentTypeFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEquipmentTypes() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentType/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEquipmentTypeTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function equipmentTypeEdit(id) {
    $("#equipment_type_id").val(id);
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentType/getById/' + id,
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
                $('#equipment_type_id').val(content.id);
                $('#equipment_type').val(content.equipmentType);
                $('#equipment_type_description').val(content.description);
                $('#equipment_type_status').val(content.status);
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

function deleteEquipmentType(id) {
    delete_id = id;
}

function delete_equipment_type() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentType/deleteById/' + delete_id,
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
                clearEquipmentTypeFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEquipmentTypeTable(equipmentTypes) {
    if ($.isEmptyObject(equipmentTypes)) {
        $('#equipment_type_table').DataTable().clear();
        $('#equipment_type_table').DataTable({
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
        $("#equipment_type_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Equipment Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Equipment Type Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Equipment Type Details',
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
            "data": equipmentTypes,
            "columns": [{
                "data": "equipmentType"
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
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=equipmentTypeEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#equipmentTypeBackdrop" title="Delete" onclick=deleteEquipmentType(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEquipmentTypeFields() {
    $('#equipment_type_id').val('');
    $('#equipment_type').val('');
    $('#equipment_type_description').val('');
    $('#equipment_type_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEquipmentTypes();
}