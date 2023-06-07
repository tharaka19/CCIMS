package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.DocumentTypeValidationMessages;
import com.cims.employee.dtos.DocumentTypeDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.DocumentType;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.DocumentTypeRepository;
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
 * Service class that handles operations related to document type.
 */
@Service
@Transactional
@Slf4j
public class DocumentTypeService {

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a document type in the database based on the provided document type DTO.
     *
     * @param documentTypeDTO the DTO representing the document type to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateDocumentType(String token, DocumentTypeDTO documentTypeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateDocumentType(documentTypeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            documentTypeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveDocumentTypeToDatabase(mapperUtils.mapDTOToEntity(documentTypeDTO, DocumentType.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, documentTypeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> saveUpdateDocumentType()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all document types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of DocumentTypeDTOs representing all document types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllDocumentTypes(String token) {
        try {
            List<DocumentType> documentTypes = getDocumentTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(documentTypes, DocumentTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> getAllDocumentTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active document types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of DocumentTypeDTOs representing all active document types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveDocumentTypes(String token) {
        try {
            List<DocumentType> documentTypes = getActiveDocumentTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(documentTypes, DocumentTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> getAllActiveDocumentTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a document type from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the document type to be retrieved
     * @return a ResponseEntity containing a DocumentTypeDTO representing the document type with the provided ID, or an error response if the document type does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getDocumentTypeById(String id) {
        try {
            if (isDocumentTypeExistById(id)) {
                DocumentType documentTypes = getDocumentTypeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(documentTypes, DocumentTypeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> getDocumentTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a document type from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the document type to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteDocumentTypeById(String id) {
        try {
            if (isDocumentTypeExistById(id)) {
                deleteDocumentTypeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> deleteDocumentTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a document type entity to the database.
     *
     * @param documentType the document type entity to be saved
     */
    public void saveDocumentTypeToDatabase(DocumentType documentType) {
        try {
            documentTypeRepository.save(documentType);
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> saveDocumentTypeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all document types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of DocumentType entities contain all document types from the database that belong to the same branch
     */
    public List<DocumentType> getDocumentTypesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return documentTypeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> getDocumentTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active document types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of DocumentType entities contain all active document types from the database that belong to the same branch
     */
    public List<DocumentType> getActiveDocumentTypesFromDatabase(String token) {
        try {
            Predicate<DocumentType> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getDocumentTypesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentTypeService -> getActiveDocumentTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a document type entity from the database based on its ID.
     *
     * @param id the ID of the document type entity to retrieve
     * @return a DocumentType entity representing the requested document type, or null if the ID is not valid or an error occurs
     */
    public DocumentType getDocumentTypeFromDatabaseById(String id) {
        try {
            return documentTypeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in DocumentTypeService -> getDocumentTypeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a DocumentType object from the database that matches the given document type and branch code.
     *
     * @param documentType a String representing the document type to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a DocumentTyp object if one is found, or null if none is found or if an exception occurs during the search
     */
    public DocumentType getDocumentTypeFromDatabaseByDocumentTypeAndBranchCode(String documentType, String branchCode) {
        try {
            return documentTypeRepository.findByDocumentTypeAndBranchCode(documentType, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in DocumentTypeService -> getDocumentTypeFromDatabaseByDocumentTypeAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a document type from the database by its ID.
     *
     * @param id the ID of the document type to be deleted
     */
    public void deleteDocumentTypeFromDatabase(String id) {
        try {
            documentTypeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in DocumentTypeService -> deleteDocumentTypeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a document type exists in the database by its ID.
     *
     * @param id the ID of the document type to check for existence
     * @return true if a document type with the specified ID exists in the database, false otherwise
     */
    public boolean isDocumentTypeExistById(String id) {
        try {
            return documentTypeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in DocumentTypeService -> isDocumentTypeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the document type data transfer object by checking if the required fields
     * are not empty and if the document type already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param documentTypeDTO the document type data transfer object to be validated
     * @param branchCode      the branch code for which to validate the document type
     * @return a list of validation error messages, or an empty list if the document type is valid
     */
    private List<String> validateDocumentType(DocumentTypeDTO documentTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if documentType fields
        validations.addAll(validateDocumentTypeFields(documentTypeDTO));
        // Check if documentType exists
        validations.addAll(validateDocumentTypeExistence(documentTypeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the document type data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param documentTypeDTO the document type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateDocumentTypeFields(DocumentTypeDTO documentTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(documentTypeDTO.getDocumentType())) {
            validations.add(DocumentTypeValidationMessages.EMPTY_DOCUMENT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(documentTypeDTO.getDescription())) {
            validations.add(DocumentTypeValidationMessages.EMPTY_DESCRIPTION);
        } else if (ValidatorUtils.statusValidator.test(documentTypeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the document type data transfer object by checking if the document type already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param documentTypeDTO the document type data transfer object to be validated
     * @param branchCode      the branch code of the document type to check for existence
     * @return a list of validation error messages, or an empty list if the document type is new or valid
     */
    private List<String> validateDocumentTypeExistence(DocumentTypeDTO documentTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        DocumentType documentType = getDocumentTypeFromDatabaseByDocumentTypeAndBranchCode(documentTypeDTO.getDocumentType(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(documentType)) {
            if (!ValidatorUtils.stringNullValidator.test(documentTypeDTO.getId())) { // Edit document type
                if (!documentTypeDTO.getId().equalsIgnoreCase(documentType.getId().toString())) {
                    validations.add(DocumentTypeValidationMessages.EXIT_DOCUMENT_TYPE);
                }
            } else { // New document type
                validations.add(DocumentTypeValidationMessages.EXIT_DOCUMENT_TYPE);
            }
        }
        return validations;
    }
}
