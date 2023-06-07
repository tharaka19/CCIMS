package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.EmployeeDocumentValidationMessages;
import com.cims.employee.dtos.EmployeeDocumentDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.EmployeeDocument;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeDocumentRepository;
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
 * Service class that handles operations related to employee document.
 */
@Service
@Transactional
@Slf4j
public class EmployeeDocumentService {

    @Autowired
    private EmployeeDocumentRepository employeeDocumentRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee document in the database based on the provided employee document DTO.
     *
     * @param employeeDocumentDTO the DTO representing the employee document to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployeeDocument(String token, EmployeeDocumentDTO employeeDocumentDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployeeDocument(employeeDocumentDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeDocumentDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeDocumentToDatabase(mapperUtils.mapDTOToEntity(employeeDocumentDTO, EmployeeDocument.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeDocumentDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> saveUpdateEmployeeDocument()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employee documents from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeDocumentDTOs representing all employee documents from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployeeDocuments(String token) {
        try {
            List<EmployeeDocument> employeeDocuments = getEmployeeDocumentsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeDocuments, EmployeeDocumentDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getAllEmployeeDocuments()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employee documents from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeDocumentDTOs representing all active employee documents from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployeeDocuments(String token) {
        try {
            List<EmployeeDocument> employeeDocuments = getActiveEmployeeDocumentsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeDocuments, EmployeeDocumentDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getAllActiveEmployeeDocuments()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee document from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee document to be retrieved
     * @return a ResponseEntity containing a EmployeeDocumentDTO representing the employee document with the provided ID, or an error response if the employee document does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeDocumentById(String id) {
        try {
            if (isEmployeeDocumentExistById(id)) {
                EmployeeDocument employeeDocuments = getEmployeeDocumentFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employeeDocuments, EmployeeDocumentDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getEmployeeDocumentById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee document from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee document to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeDocumentById(String id) {
        try {
            if (isEmployeeDocumentExistById(id)) {
                deleteEmployeeDocumentFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> deleteEmployeeDocumentById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee document entity to the database.
     *
     * @param employeeDocument the employee document entity to be saved
     */
    public void saveEmployeeDocumentToDatabase(EmployeeDocument employeeDocument) {
        try {
            employeeDocumentRepository.save(employeeDocument);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> saveEmployeeDocumentToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employee documents from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeDocument entities contain all employee documents from the database that belong to the same branch
     */
    public List<EmployeeDocument> getEmployeeDocumentsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeDocumentRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getEmployeeDocumentsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employee documents from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeDocument entities contain all active employee documents from the database that belong to the same branch
     */
    public List<EmployeeDocument> getActiveEmployeeDocumentsFromDatabase(String token) {
        try {
            Predicate<EmployeeDocument> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEmployeeDocumentsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getActiveEmployeeDocumentsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee document entity from the database based on its ID.
     *
     * @param id the ID of the employee document entity to retrieve
     * @return a EmployeeDocument entity representing the requested employee document, or null if the ID is not valid or an error occurs
     */
    public EmployeeDocument getEmployeeDocumentFromDatabaseById(String id) {
        try {
            return employeeDocumentRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getEmployeeDocumentFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EmployeeDocument object from the database that matches the given employee document and branch code.
     *
     * @param employeeDocument a String representing the employee document to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EmployeeDocument object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EmployeeDocument getEmployeeDocumentFromDatabaseByFinancialYearAndBranchCode(String employeeDocument, String branchCode) {
        try {
            return null;//employeeDocumentRepository.findByFinancialYearAndBranchCode(employeeDocument, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> getEmployeeDocumentFromDatabaseByFinancialYearAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee document from the database by its ID.
     *
     * @param id the ID of the employee document to be deleted
     */
    public void deleteEmployeeDocumentFromDatabase(String id) {
        try {
            employeeDocumentRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> deleteEmployeeDocumentFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee document exists in the database by its ID.
     *
     * @param id the ID of the employee document to check for existence
     * @return true if a employee document with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeDocumentExistById(String id) {
        try {
            return employeeDocumentRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeDocumentService -> isEmployeeDocumentExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee document data transfer object by checking if the required fields
     * are not empty and if the employee document already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeDocumentDTO the employee document data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the employee document is valid
     */
    private List<String> validateEmployeeDocument(EmployeeDocumentDTO employeeDocumentDTO) {
        List<String> validations = new ArrayList<>();
        // Check if employeeDocument fields
        validations.addAll(validateEmployeeDocumentFields(employeeDocumentDTO));
        return validations;
    }

    /**
     * Validates the employee document data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeDocumentDTO the employee document data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmployeeDocumentFields(EmployeeDocumentDTO employeeDocumentDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeDocumentDTO.getEmployeeId())) {
            validations.add(EmployeeDocumentValidationMessages.EMPTY_EMPLOYEE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDocumentDTO.getDocumentTypeId())) {
            validations.add(EmployeeDocumentValidationMessages.EMPTY_DOCUMENT_TYPE);
        } else if (ValidatorUtils.statusValidator.test(employeeDocumentDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }
}
