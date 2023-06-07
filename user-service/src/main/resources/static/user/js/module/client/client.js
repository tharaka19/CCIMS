var token;
var delete_id;
$(document).ready(function () {
    token = sessionStorage.getItem('token');
    if (token == null || token.length == 0) {
        logOut();
    }
    getAllClients();
});

function saveUpdateClient() {
    var id = $.trim($('#client_id').val());
    var firstName = $.trim($('#client_first_name').val());
    var lastName = $.trim($('#client_last_name').val());
    var fullName = $.trim($('#client_full_name').val());
    var nic = $.trim($('#client_nic').val());
    var mobileNumber = $.trim($('#client_mobile_number').val());
    var landNumber = $.trim($('#client_land_number').val());
    var address = $.trim($('#client_address').val());
    var status = $.trim($('#client_status').val());

    var clientObj = {
        "id": id,
        "firstName": firstName,
        "lastName": lastName,
        "fullName": fullName,
        "nic": nic,
        "mobileNumber": mobileNumber,
        "landNumber": landNumber,
        "address": address,
        "status": status,
    }

    $.ajax({
        url: BASE_URL + CLIENT_SERVICE + '/client/saveUpdate',
        type: "POST",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        dataType: "json",
        data: JSON.stringify(clientObj),
        success: function (data) {
            if (data.code == "00") {
                toastr.success(data.message);
                clearClientFields();
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function getAllClients() {
    $.ajax({
        url: BASE_URL + CLIENT_SERVICE + '/client/getAll',
        type: "GET",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        data: {},
        success: function (data) {
            if (data.code == "00") {
                setClientTable(data.content);
            } else if (data.code == "10") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function clientEdit(id) {
    $.ajax({
        url: BASE_URL + CLIENT_SERVICE + '/client/getById/' + id,
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
                $('#client_id').val(content.id);
                $('#client_first_name').val(content.firstName);
                $('#client_last_name').val(content.lastName);
                $('#client_full_name').val(content.fullName);
                $('#client_nic').val(content.nic);
                $('#client_mobile_number').val(content.mobileNumber);
                $('#client_land_number').val(content.landNumber);
                $('#client_address').val(content.address);
                $('#client_status').val(content.status);
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

function deleteClient(id) {
    delete_id = id;
}

function delete_client() {
    $.ajax({
        url: BASE_URL + CLIENT_SERVICE + '/client/deleteById/' + delete_id,
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
                clearClientFields();
            } else if (data.code == "01") {
                toastr.error(data.message);
            }
        },
        error: function (xhr) {
            toastr.error(xhr.message);
        }
    });
}

function setClientTable(clients) {
    if ($.isEmptyObject(clients)) {
        $('#client_table').DataTable().clear();
        $('#client_table').DataTable({
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
        $("#client_table").DataTable({
            dom: 'Bfrtip',
            buttons: [{
                extend: 'pageLength'
            },
                {
                    extend: 'pdf',
                    title: 'Client Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'excel',
                    title: 'Client Details',
                    pageSize: 'A4',
                    exportOptions: {
                        columns: ':not(:last-child)'
                    }
                },
                {
                    extend: 'print',
                    title: 'Client Details',
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
            "data": clients,
            "columns": [{
                "data": "fullName"
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
                        var columnHtml = '<button value=' + data + ' class="btn btn-xs btn-primary edit" title="Edit" onclick=clientEdit(value)><i class="bx bx-edit-alt"></i></button> ' +
                            '<button value=' + data + ' class="btn btn-xs btn-danger delete" data-bs-toggle="modal" data-bs-target="#clientBackdrop" title="Delete" onclick=deleteClient(value)><i class="fa fa-times"></i></button>';
                        return (columnHtml);
                    }
                }
            ]
        });
    }
}

function clearClientFields() {
    $('#client_first_name').val('');
    $('#client_last_name').val('');
    $('#client_full_name').val('');
    $('#client_nic').val('');
    $('#client_mobile_number').val('');
    $('#client_land_number').val('');
    $('#client_address').val('');
    $('#client_status').val('NONE');
    $('#saveBtn').val("Create");
    getAllClients();
}