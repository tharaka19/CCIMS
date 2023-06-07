package com.cims.api.gateway.constants;

import java.util.List;

/**
 * The EndPoints class contains a static list of open API endpoints that do not require authorization.
 * These endpoints can be accessed by any user without the need for a valid authentication token.
 */
public class EndPoints {

    /**
     * A list of open API endpoints, which do not require authorization.
     */
    public static final List<String> OPEN_API_END_POINTS = List.of(
            "/auth/token",
            "/admin/login",
            "/admin/dashboard/",
            "/admin/settings-user-role/",
            "/admin/settings-user-account/",
            "/admin/settings-financial-year/",
            "/admin/settings-data-sync/",
            "/admin/company-company/",
            "/admin/company-branch/",
            "/admin/company-profile/",
            "/admin/js/",
            "/admin/css/",
            "/admin/libs/",
            "/admin/images/",
            "/admin/fonts/",
            "/admin/userAccount/getByUserName/",
            "/admin/userAccount/saveToken",
            "/user/login",
            "/user/dashboard/",
            "/user/settings-user-role/",
            "/user/settings-user-account/",
            "/user/settings-financial-year/",
            "/user/settings-employee-type/",
            "/user/settings-allowance-type/",
            "/user/settings-document-type/",
            "/user/settings-equipment-type/",
            "/user/settings-project-type/",
            "/user/employee-employee/",
            "/user/employee-profile/",
            "/user/employee-document/",
            "/user/employee-history/",
            "/user/employee-salary/",
            "/user/equipment-equipment/",
            "/user/equipment-supplier/",
            "/user/equipment-stock/",
            "/user/project-project/",
            "/user/project-client/",
            "/user/client-client/",
            "/user/js/",
            "/user/css/",
            "/user/libs/",
            "/user/images/",
            "/user/fonts/",
            "/user/userAccount/getByUserName/",
            "/user/userAccount/saveToken",
            "/user/dataSync/postSystemAdminAndUserAccount",
            "/user/file/upload",
            "/user/file/imgDownloader/",
            "/eureka",
            "/eureka/apps/"
    );
}
