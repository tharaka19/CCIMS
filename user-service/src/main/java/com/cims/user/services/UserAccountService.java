package com.cims.user.services;

import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.constants.enums.Status;
import com.cims.user.constants.enums.UserRoleType;
import com.cims.user.constants.validationMessages.CommonValidationMessages;
import com.cims.user.constants.validationMessages.UserAccountValidationMessages;
import com.cims.user.dtos.PageRedirectionDTO;
import com.cims.user.dtos.UserAccountResponseDTO;
import com.cims.user.dtos.UserAccountSendDTO;
import com.cims.user.dtos.UserTokenDTO;
import com.cims.user.entities.UserAccount;
import com.cims.user.entities.UserRole;
import com.cims.user.repositories.UserAccountRepository;
import com.cims.user.utils.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to user accounts.
 */
@Service
@Transactional
@Slf4j
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a user account in the database based on the provided user account DTO and token.
     *
     * @param token              The user token used to authorize the operation.
     * @param userAccountSendDTO the DTO representing the user account to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateUserAccount(String token, UserAccountSendDTO userAccountSendDTO) {
        try {
            List<String> validations = validateUserAccount(userAccountSendDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            userAccountSendDTO.setPassword(PasswordUtils.getBCryptPassword(userAccountSendDTO.getPassword()));
            userAccountSendDTO = setBranchDetails(userAccountSendDTO, token);
            saveUserAccountToDatabase(mapperUtils.mapDTOToEntity(userAccountSendDTO, UserAccount.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, null, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> saveUpdateUserAccount()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves or updates a user account by the system administrator.
     * If the user account already exists in the database, it updates the user account with the new values.
     * Otherwise, it creates a new user account with the given values.
     *
     * @param userAccountSendDTO the DTO containing the details of the user account to be saved or updated
     * @param branchCode         the branch code associated with the user account
     * @return a ResponseEntity containing a ResponseDTO with the result of the operation and the saved or updated user account details
     */
    public ResponseEntity<ResponseDTO> saveUpdateUserAccountByAdmin(UserAccountSendDTO userAccountSendDTO, UserRole userRole, String branchCode) {
        try {
            UserAccount userAccount = getUserAccountFromDatabaseByAdminUserIdAndBranchCode(userAccountSendDTO.getId(), branchCode);
            if (ValidatorUtils.entityNullValidator.test(userAccount)) {
                userAccount = new UserAccount();
                userAccount.setUserRole(userRole);
                userAccount.setBranchCode(branchCode);
                userAccount.setAdminUserId(Long.valueOf(userAccountSendDTO.getId()));
            }
            userAccount.setUserName(userAccountSendDTO.getUserName());
            userAccount.setPassword(userAccountSendDTO.getPassword());
            userAccount.setStatus(userAccountSendDTO.getStatus());
            saveUserAccountToDatabase(userAccount);
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, null, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> saveUpdateUserAccountByAdmin()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves the authentication token of the user identified by the given user name
     * to the database. If a user account with the specified user name does not exist,
     * the method does nothing.
     *
     * @param userTokenDTO the user token data transfer object containing the user name
     */
    public void saveUserAccountToken(UserTokenDTO userTokenDTO) {
        try {
            UserAccount userAccount = getUserAccountFromDatabaseByUserName(userTokenDTO.getUserName());
            userAccount.setToken(userTokenDTO.getToken());
            saveUserAccountToDatabase(userAccount);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> saveUpdateUserAccount()" + e);
        }
    }

    /**
     * Retrieves a list of all user accounts from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of UserAccountDTOs representing all user accounts from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllUserAccounts(String token) {
        try {
            List<UserAccount> userAccounts = getUserAccountsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(userAccounts, UserAccountResponseDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getAllUserAccounts()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active user accounts from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of UserAccountDTOs representing all active user accounts from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveUserAccounts(String token) {
        try {
            List<UserAccount> userAccounts = getActiveUserAccountsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(userAccounts, UserAccountResponseDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getAllActiveUserAccounts()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active system admin user accounts from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @return a ResponseEntity containing a List of UserAccountDTOs representing all system admin active user accounts from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveSystemAdminUserAccounts(String token) {
        try {
            List<UserAccount> userAccounts = getActiveSystemAdminUserAccountsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(userAccounts, UserAccountResponseDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getAllActiveSystemAdminUserAccounts()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a user account from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the user account to be retrieved
     * @return a ResponseEntity containing a UserAccountDTO representing the user account with the provided ID, or an error response if the user account does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getUserAccountById(String id) {
        try {
            if (isUserAccountExistById(id)) {
                UserAccount userAccount = getUserAccountFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(userAccount, UserAccountResponseDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a user account from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param token the ID of the user account to be retrieved
     * @return a ResponseEntity containing a UserAccountDTO representing the user account with the provided ID, or an error response if the user account does not exist or an error occurs
     */
    public UserAccountResponseDTO getUserAccountByTokenForClient(String token) {
        try {
            UserAccount userAccount = getUserAccountFromDatabaseByToken(token);
            return mapperUtils.mapEntityToDTOForClient(userAccount, UserAccountResponseDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountByTokenForClient()" + e);
        }
        return null;
    }

    /**
     * Deletes a user account from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the user account to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteUserAccountById(String id) {
        try {
            if (isUserAccountExistById(id)) {
                deleteUserAccountFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> deleteUserRoleById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves the user name and authentication token associated with the given token
     * from the database and returns them as a {@code PageRedirectionDTO} object.
     *
     * @param token the authentication token of the user
     * @return a {@code PageRedirectionDTO} object containing the user name and token
     */
    public PageRedirectionDTO getPageRedirection(String token) {
        UserAccount userAccount = getUserAccountFromDatabaseByToken(token);
        PageRedirectionDTO pageRedirectionDTO = new PageRedirectionDTO(userAccount.getUserName(), userAccount.getToken());
        return pageRedirectionDTO;
    }

    /**
     * Saves a user account entity to the database.
     *
     * @param userAccount the user account entity to be saved
     */
    public void saveUserAccountToDatabase(UserAccount userAccount) {
        try {
            userAccountRepository.save(userAccount);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> saveUserAccountToDatabase()" + e);
        }
    }

    /**
     * Retrieves all user accounts from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of UserAccount entities contain all user accounts from the database that belong to the same branch
     */
    public List<UserAccount> getUserAccountsFromDatabase(String token) {
        try {
            return userAccountRepository.findAllByBranchCode(getUserAccountFromDatabaseByToken(token).getBranchCode());
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active user accounts from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of UserAccount entities contain all active user accounts from the database that belong to the same branch
     */
    public List<UserAccount> getActiveUserAccountsFromDatabase(String token) {
        try {
            Predicate<UserAccount> filterOnStatus = ua -> ua.getStatus().equals(Status.ACTIVE);
            return getUserAccountsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getActiveUserAccountsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a list of all active system admin user accounts from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of UserAccount entities representing all active system admin user accounts from the database that belong to the same branch
     */
    public List<UserAccount> getActiveSystemAdminUserAccountsFromDatabase(String token) {
        try {
            Predicate<UserAccount> filterOnStatus = ua -> ua.getStatus().equals(Status.ACTIVE);
            Predicate<UserAccount> filterOnUserRole = ua -> ua.getUserRole().getRoleName().equals(UserRoleType.SYSTEM_ADMIN.toString());
            return getUserAccountsFromDatabase(token).stream().filter(filterOnStatus.and(filterOnUserRole)).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in UserAccountService -> getActiveSystemAdminUserAccountsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a user account entity from the database based on its ID.
     *
     * @param id the ID of the user account entity to retrieve
     * @return a UserAccount entity representing the requested user account, or null if the ID is not valid or an error occurs
     */
    public UserAccount getUserAccountFromDatabaseById(String id) {
        try {
            return userAccountRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a user account entity from the database based on its User Name.
     *
     * @param userName the Name of the user account entity to retrieve
     * @return a UserAccount entity representing the requested user account, or null if the User Name is not valid or an error occurs
     */
    public UserAccount getUserAccountFromDatabaseByUserName(String userName) {
        try {
            return userAccountRepository.findByUserName(userName);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountFromDatabaseByUserName()" + e);
            return null;
        }
    }

    /**
     * Retrieves a user account from the database by its admin user ID and branch code.
     *
     * @param adminUserId the admin user ID of the user account to retrieve
     * @param branchCode  the branch code of the user account to retrieve
     * @return a UserAccount object representing the retrieved user account, or null if no user account is found
     */
    public UserAccount getUserAccountFromDatabaseByAdminUserIdAndBranchCode(String adminUserId, String branchCode) {
        try {
            return userAccountRepository.findByAdminUserIdAndBranchCode(adminUserId, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountFromDatabaseByAdminUserIdAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Retrieves a user account entity from the database based on its Token.
     *
     * @param token the Name of the user account entity to retrieve
     * @return a UserAccount entity representing the requested user account, or null if the Token is not valid or an error occurs
     */
    public UserAccount getUserAccountFromDatabaseByToken(String token) {
        try {
            return userAccountRepository.findByToken(token);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> getUserAccountFromDatabaseByToken()" + e);
            return null;
        }
    }

    /**
     * Deletes a user account from the database by its ID.
     *
     * @param id the ID of the user account to be deleted
     */
    public void deleteUserAccountFromDatabase(String id) {
        try {
            userAccountRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> deleteUserRoleFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a user account exists in the database by its ID.
     *
     * @param id the ID of the user account to check for existence
     * @return true if a user account with the specified ID exists in the database, false otherwise
     */
    public boolean isUserAccountExistById(String id) {
        try {
            return userAccountRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> isUserAccountExist()" + e);
            return false;
        }
    }

    /**
     * Checks whether a user account exists in the database by its ID.
     *
     * @param token    the Token of the user account to check for existence
     * @param userName the User Name of the user account to check for existence
     * @return true if a user account with the specified Token and User Name exists in the database, false otherwise
     */
    public boolean isUserAccountExistByTokenAndUserName(String token, String userName) {
        try {
            return userAccountRepository.existsByTokenAndUserName(token, userName);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserAccountService -> isUserAccountExistByTokenAndUserName()" + e);
            return false;
        }
    }

    /**
     * Checks if the given authentication token is valid for the user with the given user name.
     *
     * @param token    the authentication token to be checked
     * @param userName the user name of the user whose token is to be checked
     * @return true if the token is valid for the user, false otherwise
     */
    public boolean isValidToken(String token, String userName) {
        return isUserAccountExistByTokenAndUserName(token, userName);
    }

    /**
     * Validates a user account DTO object and returns a list of error messages for any validation errors found.
     *
     * @param userAccountSendDTO the user account DTO object to be validated
     * @return a list of error messages for any validation errors found; an empty list if no validation errors are found
     */
    private List<String> validateUserAccount(UserAccountSendDTO userAccountSendDTO) {
        List<String> validations = new ArrayList<>();
        // Check if user account fields
        validations.addAll(validateUserAccountFields(userAccountSendDTO));
        // Check if user account exists
        validations.addAll(validateUserAccountExistence(userAccountSendDTO));
        return validations;
    }

    /**
     * Validates the user account data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param userAccountSendDTO the user account data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateUserAccountFields(UserAccountSendDTO userAccountSendDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.statusValidator.test(userAccountSendDTO.getUserRoleId())) {
            validations.add(UserAccountValidationMessages.EMPTY_USER_ROLE);
        } else if (ValidatorUtils.stringNullValidator.test(userAccountSendDTO.getUserName())) {
            validations.add(UserAccountValidationMessages.EMPTY_USER_ACCOUNT_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(userAccountSendDTO.getPassword())) {
            validations.add(UserAccountValidationMessages.EMPTY_USER_ACCOUNT_PASSWORD);
        } else if (ValidatorUtils.stringNullValidator.test(userAccountSendDTO.getConfirmPassword())) {
            validations.add(UserAccountValidationMessages.EMPTY_USER_ACCOUNT_CONFIRM_PASSWORD);
        } else if (ValidatorUtils.statusValidator.test(userAccountSendDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!userAccountSendDTO.getPassword().equals(userAccountSendDTO.getConfirmPassword())) {
            validations.add(UserAccountValidationMessages.PASSWORD_MIS_MATCH);
        }
        return validations;
    }

    /**
     * Validates the user account data transfer object by checking if the user account already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param userAccountSendDTO the user account data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the user account is new or valid
     */
    private List<String> validateUserAccountExistence(UserAccountSendDTO userAccountSendDTO) {
        List<String> validations = new ArrayList<>();
        UserAccount userAccount = userAccountRepository.findByUserName(userAccountSendDTO.getUserName());
        if (!ValidatorUtils.entityNullValidator.test(userAccount)) {
            if (!ValidatorUtils.stringNullValidator.test(userAccountSendDTO.getId())) { // Edit user
                if (!userAccountSendDTO.getId().equalsIgnoreCase(userAccount.getId().toString())) {
                    validations.add(UserAccountValidationMessages.EXIT_USER_ACCOUNT);
                }
            } else { // New user
                validations.add(UserAccountValidationMessages.EXIT_USER_ACCOUNT);
            }
        }
        return validations;
    }

    /**
     * Sets the branch code of the provided UserAccount entity to the corresponding values of the UserAccount entity.
     *
     * @param userAccountSendDTO The UserAccountSendDTO entity containing the branch details to set.
     * @param token              The user token used to authorize the operation.
     * @return The updated UserAccountSendDTO.
     */
    public UserAccountSendDTO setBranchDetails(UserAccountSendDTO userAccountSendDTO, String token) {
        userAccountSendDTO.setBranchCode(getUserAccountFromDatabaseByToken(token).getBranchCode());
        return userAccountSendDTO;
    }
}
