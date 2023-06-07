var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllEquipmentSuppliers();
});

function saveUpdateEquipmentSupplier() {
    var id = $.trim($('#equipment_supplier_id').val());
    var supplierNumber = $.trim($('#equipment_supplier_number').val());
    var supplierName = $.trim($('#equipment_supplier_name').val());
    var mobileNumber = $.trim($('#equipment_supplier_mobile_number').val());
    var landNumber = $.trim($('#equipment_supplier_land_number').val());
    var address = $.trim($('#equipment_supplier_address').val());
    var status = $.trim($('#equipment_supplier_status').val());

    var equipmentSupplierObj = {
        "id": id,
        "supplierNumber": supplierNumber,
        "supplierName": supplierName,
        "mobileNumber": mobileNumber,
        "landNumber": landNumber,
        "address": address,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentSupplier/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(equipmentSupplierObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEquipmentSupplierFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEquipmentSuppliers() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentSupplier/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEquipmentSupplierTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function equipmentSupplierEdit(id) {
    $("#equipment_supplier_id").val(id);
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentSupplier/getById/' + id,
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
                $('#equipment_supplier_id').val(content.id);
                $('#equipment_supplier_number').val(content.supplierNumber);
                $('#equipment_supplier_name').val(content.supplierName);
                $('#equipment_supplier_mobile_number').val(content.mobileNumber);
                $('#equipment_supplier_land_number').val(content.landNumber);
                $('#equipment_supplier_address').val(content.address);
                $('#equipment_supplier_status').val(content.status);
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

function deleteEquipmentSupplier(id) {
    delete_id = id;
}

function delete_equipment_supplier() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentSupplier/deleteById/' + delete_id,
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
                clearEquipmentSupplierFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEquipmentSupplierTable(equipmentSuppliers) {
    if ($.isEmptyObject(equipmentSuppliers)) {
        $('#equipment_Supplier_table').DataTable().clear();
        $('#equipment_Supplier_table').DataTable({
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
        $("#equipment_Supplier_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Equipment Supplier Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Equipment Supplier Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Equipment Supplier Details',
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
            "data": equipmentSuppliers,
            "columns": [{
                "data": "supplierNumber"
            },
                {
                    "data": "supplierName"
                },
                {
                    "data": "mobileNumber"
                },
                {
                    "data": "landNumber"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=equipmentSupplierEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#equipmentSupplierBackdrop" title="Delete" onclick=deleteEquipmentSupplier(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearEquipmentSupplierFields() {
    $('#equipment_supplier_id').val('');
    $('#equipment_supplier_number').val('');
    $('#equipment_supplier_name').val('');
    $('#equipment_supplier_mobile_number').val('');
    $('#equipment_supplier_land_number').val('');
    $('#equipment_supplier_address').val('');
    $('#equipment_supplier_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEquipmentSuppliers();
}