var BASE_URL = ''
var USER_SERVICE = '/user'
var EMPLOYEE_SERVICE = '/employee'
var EQUIPMENT_SERVICE = '/equipment'
var PROJECT_SERVICE = '/project'
var CLIENT_SERVICE = '/client'

function setBaseUrl() {
    BASE_URL = window.location.origin;
    console.log("BASE_URL :" + BASE_URL);
}

function setupSessionExpiryHandler() {
    var expiresAt = Date.parse(window.sessionStorage.getItem('expiresAt'));
    setTimeout(handleSessionExpiry, (expiresAt - Date.now()));
}

function handleSessionExpiry() {
    logOut();
}

function logOut() {
    window.sessionStorage.clear();
    navigateToLogin();
}

function navigateToLogin() {
    location.href = BASE_URL + USER_SERVICE  + "/login";
}

setBaseUrl();
