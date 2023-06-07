package com.cims.user.controllers;

import com.cims.user.dtos.UserRoleDTO;
import com.cims.user.services.UserRoleService;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The UserRoleController class handles HTTP requests for User Role management endpoints.
 */
@RestController
@RequestMapping("/user/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * Saves or updates a User Role in the database.
     *
     * @param token       the authorization token for the request.
     * @param userRoleDTO the DTO representing the User Role to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody UserRoleDTO userRoleDTO) {
        return userRoleService.saveUpdateUserRole(StringUtils.extractBearerPrefix(token), userRoleDTO);
    }

    /**
     * Gets all User Roles from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of User Role DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return userRoleService.getAllUserRoles(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Gets all active User Roles from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active User Role DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return userRoleService.getAllActiveUserRoles(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Gets a User Role by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the User Role to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the User Role DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return userRoleService.getUserRoleById(id);
    }

    /**
     * Deletes a User Role by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the User Role to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return userRoleService.deleteUserRoleById(id);
    }
}
