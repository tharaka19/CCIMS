package com.cims.user.controllers;

import com.cims.user.dtos.PageRedirectionDTO;
import com.cims.user.dtos.UserAccountResponseDTO;
import com.cims.user.dtos.UserAccountSendDTO;
import com.cims.user.dtos.UserTokenDTO;
import com.cims.user.entities.UserAccount;
import com.cims.user.services.UserAccountService;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling user account related operations.
 */
@RestController
@RequestMapping("/user/userAccount")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * Saves or updates a User Account in the database.
     *
     * @param token              the authorization token for the request.
     * @param userAccountSendDTO the DTO representing the User Account to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody UserAccountSendDTO userAccountSendDTO) {
        return userAccountService.saveUpdateUserAccount(StringUtils.extractBearerPrefix(token), userAccountSendDTO);
    }

    /**
     * Endpoint for updating a user token DTO.
     *
     * @param userTokenDTO the user token DTO to update
     */
    @PostMapping("/saveToken")
    public void saveToken(@RequestBody UserTokenDTO userTokenDTO) {
        userAccountService.saveUserAccountToken(userTokenDTO);
    }

    /**
     * Gets all User Accounts from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of User Account DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return userAccountService.getAllUserAccounts(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Gets all active User Accounts from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active User Account DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return userAccountService.getAllActiveUserAccounts(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Gets all active System Admin User Accounts by from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active User Account DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActiveSystemAdmins")
    public ResponseEntity<ResponseDTO> getAllActiveSystemAdmins(@RequestHeader(name = "Authorization") String token) {
        return userAccountService.getAllActiveSystemAdminUserAccounts(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Gets a User Account by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the User Account to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the User Account DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return userAccountService.getUserAccountById(id);
    }

    /**
     * Retrieves the UserAccountResponseDTO associated with the given token for the client.
     *
     * @param token The token used to retrieve the UserAccountResponseDTO.
     * @return The UserAccountResponseDTO associated with the given token.
     */
    @GetMapping("/getByToken/{token}")
    public UserAccountResponseDTO getByTokenForClient(@PathVariable final String token) {
        return userAccountService.getUserAccountByTokenForClient(StringUtils.extractBearerPrefix(token));
    }

    /**
     * Endpoint for finding a user account by its username.
     *
     * @param userName the username to search for
     * @return the found user account, or null if not found
     */
    @GetMapping("/getByUserName/{userName}")
    public UserAccount getByUserName(@PathVariable String userName) {
        return userAccountService.getUserAccountFromDatabaseByUserName(userName);
    }

    /**
     * Deletes a User Account by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the User Account to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return userAccountService.deleteUserAccountById(id);
    }

    /**
     * Endpoint for getting a page redirection object based on a provided authorization token.
     *
     * @param authorizationHeader the authorization header containing the token
     * @return a page redirection DTO
     */
    @GetMapping("/pageRedirection")
    public PageRedirectionDTO pageRedirection(@RequestHeader(name = "Authorization") String token) {
        PageRedirectionDTO pageRedirectionDTO = userAccountService.getPageRedirection(StringUtils.extractBearerPrefix(token));
        return pageRedirectionDTO;
    }
}
