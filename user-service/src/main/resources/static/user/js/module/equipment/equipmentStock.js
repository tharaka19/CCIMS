var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllActiveEquipmentTypes();
    getAllActiveEquipmentSuppliers();
    getAllEquipmentStocks();
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

function getAllActiveEquipmentSuppliers() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentSupplier/getAllActive',
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
                    $('#equipment_supplier').append('<option value="' + value.id + '">' + value.supplierName + '</option>');
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

function setActiveEquipmentsByEquipmentType(equipmentTypeId, equipmentId) {
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
                $('#equipment').val(equipmentId);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateEquipmentStock() {
    var id = $.trim($('#equipment_stock_id').val());
    var stockNumber = $.trim($('#equipment_stock_number').val());
    var purchasePrice = $.trim($('#equipment_purchase_price').val());
    var equipmentTypeId = $.trim($('#equipment_type').val());
    var equipmentId = $.trim($('#equipment').val());
    var equipmentSupplierId = $.trim($('#equipment_supplier').val());
    var status = $.trim($('#equipment_stock_status').val());

    var equipmentObj = {
        "id": id,
        "stockNumber": stockNumber,
        "purchasePrice": purchasePrice,
        "equipmentTypeId": equipmentTypeId,
        "equipmentId": equipmentId,
        "equipmentSupplierId": equipmentSupplierId,
        "status": status
    }

    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/saveUpdate',
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
                clearEquipmentStockFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function saveUpdateEquipmentStockHistory() {
    var availableQuantity = $.trim($('#available_quantity').val());
    var operation = $.trim($('#equipment_stock_operation').val());
    var equipmentQuantity = $.trim($('#equipment_history_quantity').val());
    var historyNote = $.trim($('#equipment_stock_history_note').val());
    var equipmentStockId = $.trim($('#equipment_stock_id').val());

    var equipmentStockHistoryObj = {
        "availableQuantity": availableQuantity,
        "operation": operation,
        "equipmentQuantity": equipmentQuantity,
        "historyNote": historyNote,
        "equipmentStockId": equipmentStockId,
    }

    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStockHistory/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(equipmentStockHistoryObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearEquipmentStockHistoryFields();
                equipmentStockHistoryEdit(equipmentStockId);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllEquipmentStocks() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/getAll',
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

function getAllEquipmentStockHistoryByEquipmentStock(equipmentStockId) {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStockHistory/getAllByEquipmentStock/' + equipmentStockId,
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setEquipmentHistoryTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function equipmentStockEdit(id) {
    $("#equipment_stock_id").val(id);
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/getById/' + id,
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
                $('#equipment_stock_id').val(content.id);
                $('#equipment_stock_number').val(content.stockNumber);
                $('#equipment_purchase_price').val(content.purchasePrice);
                $('#equipment_type').val(content.equipmentTypeId);
                $('#equipment_supplier').val(content.equipmentSupplierId);
                $('#equipment_stock_status').val(content.status);
                $('#saveBtn').val("Update");
                setActiveEquipmentsByEquipmentType(content.equipmentTypeId, content.equipmentId);
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function equipmentStockHistoryEdit(id) {
    $('#equipment_stock_id').val(id);
    getAllEquipmentStockHistoryByEquipmentStock(id);
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/getById/' + id,
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
                $('#equipment_stock_id').val(content.id);
                $('#equipment_number').val(content.equipmentNumber);
                $('#equipment_name').val(content.equipmentName);
                $('#available_quantity').val(content.availableQuantity);
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function deleteEquipmentStock(id) {
    delete_id = id;
}

function delete_equipment_stock() {
    $.ajax({
        url: BASE_URL + EQUIPMENT_SERVICE + '/equipmentStock/deleteById/' + delete_id,
        type: "DELETE",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                $('#delete_close_btn').trigger('click');
                clearEquipmentStockFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setEquipmentTable(equipmentStocks) {
    if ($.isEmptyObject(equipmentStocks)) {
        $('#equipment_stock_table').DataTable().clear();
        $('#equipment_stock_table').DataTable({
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
        $("#equipment_stock_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Equipment Stock Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Equipment Stock Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Equipment Stock Details',
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
            "data": equipmentStocks,
            "columns": [{
                "data": "stockNumber"
            },
                {
                    "data": "equipmentNumber"
                },
                {
                    "data": "supplierName"
                },
                {
                    "data": "status"
                },
                {
                    "data": "id",
                    mRender: function (data, type, row) {
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=equipmentStockEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-success edit" data-bs-toggle="modal" data-bs-target="#equipmentStockUpdateBackdrop" title="Update Stock" onclick=equipmentStockHistoryEdit(value)><i class="bx bx-adjust"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#equipmentStockBackdrop" title="Delete" onclick=deleteEquipmentStock(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function setEquipmentHistoryTable(equipmentStockHistory) {
    if ($.isEmptyObject(equipmentStockHistory)) {
        $('#equipment_stock_history_table').DataTable().clear();
        $('#equipment_stock_history_table').DataTable({
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
        $("#equipment_stock_history_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Equipment Stock History Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Equipment Stock History Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Equipment Stock History Details',
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
            "data": equipmentStockHistory,
            "columns": [{
                "data": "stockNumber"
            },
                {
                    "data": "operation"
                },
                {
                    "data": "equipmentQuantity"
                },
                {
                    "data": "historyNote"
                },
                {
                    "data": "date"
                }
            ]
        });
    }
}

function clearEquipmentStockFields() {
    $('#equipment_stock_id').val('');
    $('#equipment_stock_number').val('');
    $('#equipment_purchase_price').val('');
    $('#equipment_type').val('NONE');
    $('#equipment').val('NONE');
    $('#equipment_supplier').val('NONE');
    $('#equipment_stock_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllEquipmentStocks();
}

function clearEquipmentStockHistoryFields() {
    $('#equipment_number').val('');
    $('#equipment_name').val('');
    $('#available_quantity').val('');
    $('#equipment_stock_operation').val('NONE');
    $('#equipment_history_quantity').val('');
    $('#equipment_stock_history_note').val('');
}