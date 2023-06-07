var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveEquipmentTypes()
    getAllEquipments();
});

function getAllActiveEquipmentTypes() {
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
                    $('#equipment_type').append('<option value="' + value.id + '">' + value.equipmentType + '</option>');
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

function saveUpdateEquipment() {
    var id = $.trim($('#equipment_id').val());
    var equipmentNumber = $.trim($('#equipment_number').val());
    var equipmentName = $.trim($('#equipment_name').val());
    var equipmentTypeId = $.trim($('#equipment_type').val());
    var status = $.trim($('#equipment_status').val());

    var equipmentObj = {
        "id": id,
        "equipmentNumber": equipmentNumber,
        "equipmentName": equipmentName,
        "equipmentTypeId": equipmentTypeId,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipment/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(equipmentObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEquipmentFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEquipments() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipment/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEquipmentTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function equipmentEdit(id) {
    $("#equipment_id").val(id);
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipment/getById/' + id,
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
                $('#equipment_id').val(content.id);
                $('#equipment_number').val(content.equipmentNumber);
                $('#equipment_name').val(content.equipmentName);
                $('#equipment_type').val(content.equipmentTypeId);
                $('#equipment_status').val(content.status);
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

function deleteEquipment(id) {
    delete_id = id;
}

function delete_equipment_type() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipment/deleteById/' + delete_id,
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
                clearEquipmentFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEquipmentTable(equipments) {
    if ($.isEmptyObject(equipments)) {
        $('#equipment_table').DataTable().clear();
        $('#equipment_table').DataTable({
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
        $("#equipment_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Equipment Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Equipment Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Equipment Details',
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
            "data": equipments,
            "columns": [{
                "data": "equipmentType"
            },
                {
                    "data": "equipmentNumber"
                },
                {
                    "data": "equipmentName"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=equipmentEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#equipmentBackdrop" title="Delete" onclick=deleteEquipment(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEquipmentFields() {
    $('#equipment_id').val('');
    $('#equipment_number').val('');
    $('#equipment_name').val('');
    $('#equipment_type').val('NONE');
    $('#equipment_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEquipments();
}