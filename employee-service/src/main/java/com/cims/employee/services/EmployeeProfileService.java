package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.EmployeeProfileValidationMessages;
import com.cims.employee.dtos.EmployeeProfileDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.Employee;
import com.cims.employee.entities.EmployeeProfile;
import com.cims.employee.entities.FinancialYear;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeProfileRepository;
import com.cims.employee.utils.MapperUtils;
import com.cims.employee.utils.ResponseDTO;
import com.cims.employee.utils.ResponseUtils;
import com.cims.employee.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to employee profile.
 */
@Service
@Transactional
@Slf4j
public class EmployeeProfileService {

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee profile in the database based on the provided employee profile DTO.
     *
     * @param employeeProfileDTO the DTO representing the employee profile to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployeeProfile(String token, EmployeeProfileDTO employeeProfileDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployeeProfile(employeeProfileDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeProfileDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeProfileToDatabase(mapperUtils.mapDTOToEntity(employeeProfileDTO, EmployeeProfile.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeProfileDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> saveUpdateEmployeeProfile()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employee profiles from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeProfileDTOs representing all employee profiles from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployeeProfiles(String token) {
        try {
            List<EmployeeProfile> employeeProfiles = getEmployeeProfilesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeProfiles, EmployeeProfileDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getAllEmployeeProfiles()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employee profiles from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeProfileDTOs representing all active employee profiles from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployeeProfiles(String token) {
        try {
            List<EmployeeProfile> employeeProfiles = getActiveEmployeeProfilesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeProfiles, EmployeeProfileDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getAllActiveEmployeeProfiles()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employee profiles by financial year from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeProfileDTOs representing all active employee profiles  by financial year from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployeeProfilesByFinancialYear(String token, String financialYearId) {
        try {
            List<EmployeeProfile> employeeProfiles = getActiveEmployeeProfilesFromDatabaseByFinancialYear(token, financialYearId);
            return mapperUtils.mapEntitiesToDTOs(employeeProfiles, EmployeeProfileDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getAllActiveEmployeeProfilesByFinancialYear()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Retrieves a employee profile from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee profile to be retrieved
     * @return a ResponseEntity containing a EmployeeProfileDTO representing the employee profile with the provided ID, or an error response if the employee profile does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeProfileById(String id) {
        try {
            if (isEmployeeProfileExistById(id)) {
                EmployeeProfile employeeProfiles = getEmployeeProfileFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employeeProfiles, EmployeeProfileDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfileById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee profile from the database based on the provided Employee ID and FinancialYear ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param financialYearId the FinancialYear ID of the employee profile to be retrieved
     * @param employeeId      the Employee ID of the employee profile to be retrieved
     * @return a ResponseEntity containing a EmployeeProfileDTO representing the employee profile with the provided Employee ID and FinancialYear ID, or an error response if the employee profile does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeProfileByFinancialYearAndEmployee(String financialYearId, String employeeId) {
        try {
            FinancialYear financialYear = financialYearService.getFinancialYearFromDatabaseById(financialYearId);
            Employee employee = employeeService.getEmployeeFromDatabaseById(employeeId);
            EmployeeProfile employeeProfiles = getEmployeeProfileFromDatabaseByEmployeeAndFinancialYear(financialYear, employee);
            return mapperUtils.mapEntityToDTO(employeeProfiles, EmployeeProfileDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfileEmployeeAndFinancialYear()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee profile from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee profile to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeProfileById(String id) {
        try {
            if (isEmployeeProfileExistById(id)) {
                deleteEmployeeProfileFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> deleteEmployeeProfileById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee profile entity to the database.
     *
     * @param employeeProfile the employee profile entity to be saved
     */
    public void saveEmployeeProfileToDatabase(EmployeeProfile employeeProfile) {
        try {
            employeeProfileRepository.save(employeeProfile);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> saveEmployeeProfileToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employee profiles from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeProfile entities contain all employee profiles from the database that belong to the same branch
     */
    public List<EmployeeProfile> getEmployeeProfilesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeProfileRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfilesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employee profiles from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeProfile entities contain all active employee profiles from the database that belong to the same branch
     */
    public List<EmployeeProfile> getActiveEmployeeProfilesFromDatabase(String token) {
        try {
            Predicate<EmployeeProfile> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEmployeeProfilesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getActiveEmployeeProfilesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employee profiles from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeProfile entities contain all active employee profiles from the database that belong to the same branch
     */
    public List<EmployeeProfile> getActiveEmployeeProfilesFromDatabaseByFinancialYear(String token, String financialYearId) {
        try {
            Predicate<EmployeeProfile> filterOnFinancialYear = fy -> fy.getFinancialYear().getId() == Long.parseLong(financialYearId);
            return getActiveEmployeeProfilesFromDatabase(token).stream().filter(filterOnFinancialYear).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getActiveEmployeeProfilesFromDatabaseByFinancialYear()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee profile entity from the database based on its ID.
     *
     * @param id the ID of the employee profile entity to retrieve
     * @return a EmployeeProfile entity representing the requested employee profile, or null if the ID is not valid or an error occurs
     */
    public EmployeeProfile getEmployeeProfileFromDatabaseById(String id) {
        try {
            return employeeProfileRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfileFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee profile entity from the database based on its Employee ID and FinancialYear ID.
     *
     * @param financialYear the FinancialYear of the employee profile entity to retrieve
     * @param employee      the Employee of the employee profile entity to retrieve
     * @return a EmployeeProfile entity representing the requested employee profile, or null if the Employee and FinancialYear is not valid or an error occurs
     */
    public EmployeeProfile getEmployeeProfileFromDatabaseByEmployeeAndFinancialYear(FinancialYear financialYear, Employee employee) {
        try {
            return employeeProfileRepository.findByFinancialYearAndEmployee(financialYear, employee);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfileFromDatabaseByEmployeeAndFinancialYear()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EmployeeProfile object from the database that matches the given employee profile and branch code.
     *
     * @param employeeProfile a String representing the employee profile to search for
     * @param branchCode      a String representing the branch code to search for
     * @return a EmployeeProfile object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EmployeeProfile getEmployeeProfileFromDatabaseByFinancialYearAndBranchCode(String employeeProfile, String branchCode) {
        try {
            return employeeProfileRepository.findByFinancialYearAndBranchCode(employeeProfile, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeProfileService -> getEmployeeProfileFromDatabaseByFinancialYearAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee profile from the database by its ID.
     *
     * @param id the ID of the employee profile to be deleted
     */
    public void deleteEmployeeProfileFromDatabase(String id) {
        try {
            employeeProfileRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeProfileService -> deleteEmployeeProfileFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee profile exists in the database by its ID.
     *
     * @param id the ID of the employee profile to check for existence
     * @return true if a employee profile with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeProfileExistById(String id) {
        try {
            return employeeProfileRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeProfileService -> isEmployeeProfileExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee profile data transfer object by checking if the required fields
     * are not empty and if the employee profile already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeProfileDTO the employee profile data transfer object to be validated
     * @param branchCode         the branch code for which to validate the employee profile
     * @return a list of validation error messages, or an empty list if the employee profile is valid
     */
    private List<String> validateEmployeeProfile(EmployeeProfileDTO employeeProfileDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if employeeProfile fields
        validations.addAll(validateEmployeeProfileFields(employeeProfileDTO));
        // Check if employeeProfile exists
        //validations.addAll(validateEmployeeProfileExistence(employeeProfileDTO, branchCode));
        return validations;
    }

    /**
     * Validates the employee profile data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeProfileDTO the employee profile data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmployeeProfileFields(EmployeeProfileDTO employeeProfileDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeProfileDTO.getFinancialYearId())) {
            validations.add(EmployeeProfileValidationMessages.EMPTY_FINANCIAL_YEAR);
        } else if (ValidatorUtils.stringNullValidator.test(employeeProfileDTO.getEmployeeId())) {
            validations.add(EmployeeProfileValidationMessages.EMPTY_EMPLOYEE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeProfileDTO.getEmployeeTypeId())) {
            validations.add(EmployeeProfileValidationMessages.EMPTY_EMPLOYEE_TYPE);
        } else if (CollectionUtils.isEmpty(employeeProfileDTO.getAllowanceTypes())) {
            validations.add(EmployeeProfileValidationMessages.EMPTY_ALLOWANCE_TYPE);
        } else if (ValidatorUtils.statusValidator.test(employeeProfileDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the employee profile data transfer object by checking if the employee profile already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param employeeProfileDTO the employee profile data transfer object to be validated
     * @param branchCode         the branch code of the employee profile to check for existence
     * @return a list of validation error messages, or an empty list if the employee profile is new or valid
     */
    private List<String> validateEmployeeProfileExistence(EmployeeProfileDTO employeeProfileDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        EmployeeProfile employeeProfile = getEmployeeProfileFromDatabaseByFinancialYearAndBranchCode(employeeProfileDTO.getFinancialYearId(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(employeeProfile)) {
            if (!ValidatorUtils.stringNullValidator.test(employeeProfileDTO.getId())) { // Edit employee profile
                if (!employeeProfileDTO.getId().equalsIgnoreCase(employeeProfile.getId().toString())) {
                    validations.add(EmployeeProfileValidationMessages.EXIT_PROFILE);
                }
            } else { // New employee profile
                validations.add(EmployeeProfileValidationMessages.EXIT_PROFILE);
            }
        }
        return validations;
    }
}
