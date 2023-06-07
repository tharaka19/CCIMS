package com.cims.user.controllers;

import com.cims.user.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The `UIControllerInternal` class serves as a Spring MVC Controller that handles requests related to the user
 * of the application.
 */
@Controller
@RequestMapping("/user")
public class UIControllerInternal {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * Renders the login page.
     *
     * @return The login page view name.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Renders the dashboard page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The dashboard page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/dashboard/{user}/{token}")
    public String dashboardPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Dashboard");
            model.addAttribute("user", user);
            return "dashboard";
        }
        return "accessDenied";
    }

    /**
     * Renders the user role page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The user role page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-user-role/{user}/{token}")
    public String userRolePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "User Role");
            model.addAttribute("user", user);
            return "settings/userRole";
        }
        return "accessDenied";
    }

    /**
     * Renders the user account page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The user account page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-user-account/{user}/{token}")
    public String userAccountPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "User Account");
            model.addAttribute("user", user);
            return "settings/userAccount";
        }
        return "accessDenied";
    }

    /**
     * Renders the financial year page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The financial year page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-financial-year/{user}/{token}")
    public String financialYearPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Financial Year");
            model.addAttribute("user", user);
            return "settings/financialYear";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee type page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee type page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-employee-type/{user}/{token}")
    public String employeeTypePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee Type");
            model.addAttribute("user", user);
            return "settings/employeeType";
        }
        return "accessDenied";
    }

    /**
     * Renders the allowance type page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The allowance type page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-allowance-type/{user}/{token}")
    public String allowanceTypePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Allowance Type");
            model.addAttribute("user", user);
            return "settings/allowanceType";
        }
        return "accessDenied";
    }

    /**
     * Renders the document type page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The document type page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-document-type/{user}/{token}")
    public String documentTypePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Document Type");
            model.addAttribute("user", user);
            return "settings/documentType";
        }
        return "accessDenied";
    }

    /**
     * Renders the equipment type page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The equipment type page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-equipment-type/{user}/{token}")
    public String equipmentTypePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Equipment Type");
            model.addAttribute("user", user);
            return "settings/equipmentType";
        }
        return "accessDenied";
    }

    /**
     * Renders the project type page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The project type page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-project-type/{user}/{token}")
    public String projectTypePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Project Type");
            model.addAttribute("user", user);
            return "settings/projectType";
        }
        return "accessDenied";
    }

    /**
     * Renders the data sync page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The data sync page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/settings-data-sync/{user}/{token}")
    public String dataSyncPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Data Sync");
            model.addAttribute("user", user);
            return "settings/dataSync";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/employee-employee/{user}/{token}")
    public String employeePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee");
            model.addAttribute("user", user);
            return "employee/employee";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee profile page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee profile page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/employee-profile/{user}/{token}")
    public String employeeProfilePage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee Profile");
            model.addAttribute("user", user);
            return "employee/employeeProfile";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee document page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee document page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/employee-document/{user}/{token}")
    public String employeeDocumentPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee Document");
            model.addAttribute("user", user);
            return "employee/employeeDocument";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee history page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee history page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/employee-history/{user}/{token}")
    public String employeeHistoryPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee History");
            model.addAttribute("user", user);
            return "employee/employeeHistory";
        }
        return "accessDenied";
    }

    /**
     * Renders the employee salary page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The employee salary page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/employee-salary/{user}/{token}")
    public String employeeSalaryPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Employee Salary");
            model.addAttribute("user", user);
            return "employee/employeeSalary";
        }
        return "accessDenied";
    }

    /**
     * Renders the equipment page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The equipment page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/equipment-equipment/{user}/{token}")
    public String equipmentPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Equipment");
            model.addAttribute("user", user);
            return "equipment/equipment";
        }
        return "accessDenied";
    }

    /**
     * Renders the equipment supplier page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The equipment supplier page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/equipment-supplier/{user}/{token}")
    public String equipmentSupplierPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Equipment Supplier");
            model.addAttribute("user", user);
            return "equipment/equipmentSupplier";
        }
        return "accessDenied";
    }


    /**
     * Renders the equipment stock page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The equipment stock page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/equipment-stock/{user}/{token}")
    public String equipmentStockPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Equipment Stock");
            model.addAttribute("user", user);
            return "equipment/equipmentStock";
        }
        return "accessDenied";
    }

    /**
     * Renders the project page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The project page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/project-project/{user}/{token}")
    public String projectPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Project");
            model.addAttribute("user", user);
            return "project/project";
        }
        return "accessDenied";
    }

    /**
     * Renders the client project page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The client project page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/project-client/{user}/{token}")
    public String clientProjectPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Client Project");
            model.addAttribute("user", user);
            return "project/clientProject";
        }
        return "accessDenied";
    }

    /**
     * Renders the client page.
     *
     * @param model The Model object used to pass data to the view.
     * @param user  The username extracted from the URL path variable.
     * @param token The access token extracted from the URL path variable.
     * @return The client page view name if the token is valid, otherwise the accessDenied view name.
     */
    @GetMapping("/client-client/{user}/{token}")
    public String clientPage(Model model, @PathVariable final String user, @PathVariable final String token) {
        if (userAccountService.isValidToken(token, user)) {
            model.addAttribute("pageName", "Client");
            model.addAttribute("user", user);
            return "client/client";
        }
        return "accessDenied";
    }
}
