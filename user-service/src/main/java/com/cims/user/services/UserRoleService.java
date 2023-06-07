package com.cims.user.services;

import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.constants.enums.Status;
import com.cims.user.constants.validationMessages.CommonValidationMessages;
import com.cims.user.constants.validationMessages.UserRoleValidationMessages;
import com.cims.user.dtos.UserRoleDTO;
import com.cims.user.entities.UserAccount;
import com.cims.user.entities.UserRole;
import com.cims.user.repositories.UserRoleRepository;
import com.cims.user.utils.MapperUtils;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.ResponseUtils;
import com.cims.user.utils.ValidatorUtils;
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
 * Service class that handles operations related to user roles.
 */
@Service
@Transactional
@Slf4j
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a user role based on the provided UserRoleDTO and token.
     *
     * @param token       The user token used to authorize the operation.
     * @param userRoleDTO the DTO representing the user role to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateUserRole(String token, UserRoleDTO userRoleDTO) {
        try {
            UserAccount userAccount = userAccountService.getUserAccountFromDatabaseByToken(token);
            List<String> validations = validateUserRole(userRoleDTO, userAccount.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            userRoleDTO = setBranchDetails(userRoleDTO, userAccount);
            saveUserRoleToDatabase(mapperUtils.mapDTOToEntity(userRoleDTO, UserRole.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, userRoleDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> saveUpdateUserRole()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves or updates the user role by an admin. If the user role doesn't exist in the database, a new one will be created.
     * If the user role already exists, the existing one will be updated with the provided values.
     *
     * @param userRoleDTO the user role data transfer object that contains the user role information
     * @param branchName  the name of the branch associated with the user role
     * @param branchCode  the code of the branch associated with the user role
     * @return a response entity that contains a response data transfer object with the result of the operation
     */
    public UserRole saveUpdateUserRoleByAdmin(UserRoleDTO userRoleDTO, String branchName, String branchCode) {
        UserRole userRole = getUserRoleFromDatabaseByRoleNameAndBranchCode(userRoleDTO.getRoleName(), branchCode);
        try {
            if (ValidatorUtils.entityNullValidator.test(userRole)) {
                userRole = new UserRole();
                userRole.setRoleName(userRoleDTO.getRoleName());
                userRole.setAdminRoleId(Long.valueOf(userRoleDTO.getId()));
                userRole.setBranchCode(branchCode);
            }
            userRole.setDescription(userRoleDTO.getDescription());
            userRole.setStatus(userRoleDTO.getStatus());
            userRole.setBranchName(branchName);
            saveUserRoleToDatabase(userRole);
            return userRole;
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> saveUpdateUserRoleByAdmin()" + e);
            return userRole;
        }
    }

    /**
     * Retrieves a list of all user roles from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of UserRoleDTOs representing all user roles from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllUserRoles(String token) {
        try {
            List<UserRole> userRoles = getUserRolesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(userRoles, UserRoleDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> getAllUserRoles()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active user roles from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of UserRoleDTOs representing all active user roles from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveUserRoles(String token) {
        try {
            List<UserRole> userRoles = getActiveUserRolesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(userRoles, UserRoleDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> getAllActiveUserRoles()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a user role from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the user role to be retrieved
     * @return a ResponseEntity containing a UserRoleDTO representing the user role with the provided ID, or an error response if the user role does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getUserRoleById(String id) {
        try {
            if (isUserRoleExistById(id)) {
                UserRole userRole = getUserRoleFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(userRole, UserRoleDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> getUserRoleById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a user role from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the user role to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteUserRoleById(String id) {
        try {
            if (isUserRoleExistById(id)) {
                deleteUserRoleFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> deleteUserRoleById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a user role entity to the database.
     *
     * @param userRole the user role entity to be saved
     */
    public void saveUserRoleToDatabase(UserRole userRole) {
        try {
            userRoleRepository.save(userRole);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> saveUserRoleToDatabase()" + e);
        }
    }

    /**
     * Retrieves all user roles from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of UserRole entities contain all user roles from the database that belong to the same branch
     */
    public List<UserRole> getUserRolesFromDatabase(String token) {
        try {
            String branchCode = userAccountService.getUserAccountFromDatabaseByToken(token).getBranchCode();
            return userRoleRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> getUserRolesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active user roles from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of UserRole entities contain all active user roles from the database that belong to the same branch
     */
    public List<UserRole> getActiveUserRolesFromDatabase(String token) {
        try {
            Predicate<UserRole> filterOnStatus = ur -> ur.getStatus().equals(Status.ACTIVE);
            return getUserRolesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in UserRoleService -> getActiveUserRolesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a user role entity from the database based on its ID.
     *
     * @param id the ID of the user role entity to retrieve
     * @return a UserRole entity representing the requested user role, or null if the ID is not valid or an error occurs
     */
    public UserRole getUserRoleFromDatabaseById(String id) {
        try {
            return userRoleRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserRoleService -> getUserRoleFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves the user role from the database by role name and branch code.
     *
     * @param roleName   the name of the user role
     * @param branchCode the branch code associated with the user role
     * @return the user role object from the database
     */
    public UserRole getUserRoleFromDatabaseByRoleNameAndBranchCode(String roleName, String branchCode) {
        try {
            return userRoleRepository.findByRoleNameAndBranchCode(roleName, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserRoleService -> getUserRoleFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Deletes a user role from the database by its ID.
     *
     * @param id the ID of the user role to be deleted
     */
    public void deleteUserRoleFromDatabase(String id) {
        try {
            userRoleRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserRoleService -> deleteUserRoleFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a user role exists in the database by its ID.
     *
     * @param id the ID of the user role to check for existence
     * @return true if a user role with the specified ID exists in the database, false otherwise
     */
    public boolean isUserRoleExistById(String id) {
        try {
            return userRoleRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in UserRoleService -> isUserRoleExist()" + e);
            return false;
        }
    }

    /**
     * Validates the user role data transfer object by checking if the required fields
     * are not empty and if the user role already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param userRoleDTO the user role data transfer object to be validated
     * @param branchCode  the branch code for which to validate the user role
     * @return a list of validation error messages, or an empty list if the user role is valid
     */
    private List<String> validateUserRole(UserRoleDTO userRoleDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if userRole fields
        validations.addAll(validateUserRoleFields(userRoleDTO));
        // Check if userRole exists
        validations.addAll(validateUserRoleExistence(userRoleDTO, branchCode));
        return validations;
    }

    /**
     * Validates the user role data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param userRoleDTO the user role data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateUserRoleFields(UserRoleDTO userRoleDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(userRoleDTO.getRoleName())) {
            validations.add(UserRoleValidationMessages.EMPTY_USER_ROLE_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(userRoleDTO.getDescription())) {
            validations.add(UserRoleValidationMessages.EMPTY_USER_ROLE_DESCRIPTION);
        } else if (ValidatorUtils.statusValidator.test(userRoleDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the user role data transfer object by checking if the user role already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param userRoleDTO the user role data transfer object to be validated
     * @param branchCode  the branch code of the user role to check for existence
     * @return a list of validation error messages, or an empty list if the user role is new or valid
     */
    private List<String> validateUserRoleExistence(UserRoleDTO userRoleDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        UserRole userRole = getUserRoleFromDatabaseByRoleNameAndBranchCode(userRoleDTO.getRoleName(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(userRole)) {
            if (!ValidatorUtils.stringNullValidator.test(userRoleDTO.getId())) { // Edit user role
                if (!userRoleDTO.getId().equalsIgnoreCase(userRole.getId().toString())) {
                    validations.add(UserRoleValidationMessages.EXIT_USER_ROLE);
                }
            } else { // New user role
                validations.add(UserRoleValidationMessages.EXIT_USER_ROLE);
            }
        }
        return validations;
    }

    /**
     * Sets the branch name and branch code of the provided UserRole entity to the corresponding values of the UserAccount entity.
     *
     * @param userRoleDTO The UserRoleDTO to set the branch details for.
     * @param userAccount The UserAccount entity containing the branch details to set.
     * @return The updated UserRoleDTO.
     */
    public UserRoleDTO setBranchDetails(UserRoleDTO userRoleDTO, UserAccount userAccount) {
        userRoleDTO.setBranchName(userAccount.getUserRole().getBranchName());
        userRoleDTO.setBranchCode(userAccount.getBranchCode());
        return userRoleDTO;
    }
}
