package com.cims.employee.services;

import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.entities.FinancialYear;
import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.FinancialYearValidationMessages;
import com.cims.employee.dtos.FinancialYearDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.FinancialYearRepository;
import com.cims.employee.utils.*;
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
 * Service class that handles operations related to financial year.
 */
@Service
@Transactional
@Slf4j
public class FinancialYearService {

    @Autowired
    private FinancialYearRepository financialYearRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a financial year in the database based on the provided financial year DTO.
     *
     * @param financialYearDTO the DTO representing the financial year to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateFinancialYear(String token, FinancialYearDTO financialYearDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateFinancialYear(financialYearDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            financialYearDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveFinancialYearToDatabase(mapperUtils.mapDTOToEntity(financialYearDTO, FinancialYear.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, financialYearDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> saveUpdateFinancialYear()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all financial years from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of FinancialYearDTOs representing all financial years from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllFinancialYears(String token) {
        try {
            List<FinancialYear> financialYears = getFinancialYearsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(financialYears, FinancialYearDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> getAllFinancialYears()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active financial years from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of FinancialYearDTOs representing all active financial years from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveFinancialYears(String token) {
        try {
            List<FinancialYear> financialYears = getActiveFinancialYearsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(financialYears, FinancialYearDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> getAllActiveFinancialYears()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a financial year from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the financial year to be retrieved
     * @return a ResponseEntity containing a FinancialYearDTO representing the financial year with the provided ID, or an error response if the financial year does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getFinancialYearById(String id) {
        try {
            if (isFinancialYearExistById(id)) {
                FinancialYear financialYears = getFinancialYearFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(financialYears, FinancialYearDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> getFinancialYearById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a financial year from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the financial year to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteFinancialYearById(String id) {
        try {
            if (isFinancialYearExistById(id)) {
                deleteFinancialYearFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> deleteFinancialYearById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a financial year entity to the database.
     *
     * @param financialYear the financial year entity to be saved
     */
    public void saveFinancialYearToDatabase(FinancialYear financialYear) {
        try {
            financialYearRepository.save(financialYear);
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> saveFinancialYearToDatabase()" + e);
        }
    }

    /**
     * Retrieves all financial years from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of FinancialYear entities contain all financial years from the database that belong to the same branch
     */
    public List<FinancialYear> getFinancialYearsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return financialYearRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> getFinancialYearsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active financial years from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of FinancialYear entities contain all active financial years from the database that belong to the same branch
     */
    public List<FinancialYear> getActiveFinancialYearsFromDatabase(String token) {
        try {
            Predicate<FinancialYear> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getFinancialYearsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in FinancialYearService -> getActiveFinancialYearsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a financial year entity from the database based on its ID.
     *
     * @param id the ID of the financial year entity to retrieve
     * @return a FinancialYear entity representing the requested financial year, or null if the ID is not valid or an error occurs
     */
    public FinancialYear getFinancialYearFromDatabaseById(String id) {
        try {
            return financialYearRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in FinancialYearService -> getFinancialYearFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a FinancialYear object from the database that matches the given financial year and branch code.
     *
     * @param financialYear a String representing the financial year to search for
     * @param branchCode    a String representing the branch code to search for
     * @return a FinancialYear object if one is found, or null if none is found or if an exception occurs during the search
     */
    public FinancialYear getFinancialYearFromDatabaseByFinancialYearAndBranchCode(String financialYear, String branchCode) {
        try {
            return financialYearRepository.findByFinancialYearAndBranchCode(financialYear, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in FinancialYearService -> getFinancialYearFromDatabaseByFinancialYearAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a financial year from the database by its ID.
     *
     * @param id the ID of the financial year to be deleted
     */
    public void deleteFinancialYearFromDatabase(String id) {
        try {
            financialYearRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in FinancialYearService -> deleteFinancialYearFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a financial year exists in the database by its ID.
     *
     * @param id the ID of the financial year to check for existence
     * @return true if a financial year with the specified ID exists in the database, false otherwise
     */
    public boolean isFinancialYearExistById(String id) {
        try {
            return financialYearRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in FinancialYearService -> isFinancialYearExist()" + e);
            return false;
        }
    }

    /**
     * Validates the financial year data transfer object by checking if the required fields
     * are not empty and if the financial year already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param financialYearDTO the financial year data transfer object to be validated
     * @param branchCode       the branch code for which to validate the financial year
     * @return a list of validation error messages, or an empty list if the financial year is valid
     */
    private List<String> validateFinancialYear(FinancialYearDTO financialYearDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if financialYear fields
        validations.addAll(validateFinancialYearFields(financialYearDTO));
        // Check if financialYear exists
        validations.addAll(validateFinancialYearExistence(financialYearDTO, branchCode));
        return validations;
    }

    /**
     * Validates the financial year data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param financialYearDTO the financial year data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateFinancialYearFields(FinancialYearDTO financialYearDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(StringUtils.extractFirstPart(financialYearDTO.getFinancialYear()))) {
            validations.add(FinancialYearValidationMessages.EMPTY_FINANCIAL_YEAR_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(StringUtils.extractSecondPart(financialYearDTO.getFinancialYear()))) {
            validations.add(FinancialYearValidationMessages.EMPTY_TERM_NAME);
        } else if (ValidatorUtils.dateValidator.test(financialYearDTO.getStartDate())) {
            validations.add(FinancialYearValidationMessages.EMPTY_START_DATE);
        } else if (ValidatorUtils.dateValidator.test(financialYearDTO.getEndDate())) {
            validations.add(FinancialYearValidationMessages.EMPTY_END_DATE);
        } else if (ValidatorUtils.statusValidator.test(financialYearDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!ValidatorUtils.numericValidator.test(StringUtils.extractFirstPart(financialYearDTO.getFinancialYear()))) {
            validations.add(FinancialYearValidationMessages.INVALID_FINANCIAL_YEAR);
        } else if (StringUtils.extractFirstPart(financialYearDTO.getFinancialYear()).length() != 4) {
            validations.add(FinancialYearValidationMessages.INVALID_FINANCIAL_YEAR);
        } else if (financialYearDTO.getStartDate().isAfter(financialYearDTO.getEndDate())) {
            validations.add(FinancialYearValidationMessages.INVALID_DATE_RANGE);
        }
        return validations;
    }

    /**
     * Validates the financial year data transfer object by checking if the financial year already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param financialYearDTO the financial year data transfer object to be validated
     * @param branchCode       the branch code of the financial year to check for existence
     * @return a list of validation error messages, or an empty list if the financial year is new or valid
     */
    private List<String> validateFinancialYearExistence(FinancialYearDTO financialYearDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        FinancialYear financialYear = getFinancialYearFromDatabaseByFinancialYearAndBranchCode(financialYearDTO.getFinancialYear(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(financialYear)) {
            if (!ValidatorUtils.stringNullValidator.test(financialYearDTO.getId())) { // Edit financial year
                if (!financialYearDTO.getId().equalsIgnoreCase(financialYear.getId().toString())) {
                    validations.add(FinancialYearValidationMessages.EXIT_FINANCIAL_YEAR);
                }
            } else { // New financial year
                validations.add(FinancialYearValidationMessages.EXIT_FINANCIAL_YEAR);
            }
        }
        return validations;
    }
}
