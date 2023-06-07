package com.cims.project.services;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.constants.enums.Status;
import com.cims.project.constants.validationMessages.CommonValidationMessages;
import com.cims.project.constants.validationMessages.ProjectTypeValidationMessages;
import com.cims.project.dtos.ProjectTypeDTO;
import com.cims.project.dtos.UserAccountResponseDTO;
import com.cims.project.entities.ProjectType;
import com.cims.project.proxyClients.UserServiceClient;
import com.cims.project.repositories.ProjectTypeRepository;
import com.cims.project.utils.MapperUtils;
import com.cims.project.utils.ResponseDTO;
import com.cims.project.utils.ResponseUtils;
import com.cims.project.utils.ValidatorUtils;
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
 * Service class that handles operations related to project type.
 */
@Service
@Transactional
@Slf4j
public class ProjectTypeService {

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a project type in the database based on the provided project type DTO.
     *
     * @param projectTypeDTO the DTO representing the project type to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateProjectType(String token, ProjectTypeDTO projectTypeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateProjectType(projectTypeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            projectTypeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveProjectTypeToDatabase(mapperUtils.mapDTOToEntity(projectTypeDTO, ProjectType.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, projectTypeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> saveUpdateProjectType()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all project types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ProjectTypeDTOs representing all project types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllProjectTypes(String token) {
        try {
            List<ProjectType> projectTypes = getProjectTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(projectTypes, ProjectTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> getAllProjectTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active project types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ProjectTypeDTOs representing all active project types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveProjectTypes(String token) {
        try {
            List<ProjectType> projectTypes = getActiveProjectTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(projectTypes, ProjectTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> getAllActiveProjectTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a project type from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the project type to be retrieved
     * @return a ResponseEntity containing a ProjectTypeDTO representing the project type with the provided ID, or an error response if the project type does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getProjectTypeById(String id) {
        try {
            if (isProjectTypeExistById(id)) {
                ProjectType projectTypes = getProjectTypeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(projectTypes, ProjectTypeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> getProjectTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a project type from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the project type to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteProjectTypeById(String id) {
        try {
            if (isProjectTypeExistById(id)) {
                deleteProjectTypeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> deleteProjectTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a project type entity to the database.
     *
     * @param projectType the project type entity to be saved
     */
    public void saveProjectTypeToDatabase(ProjectType projectType) {
        try {
            projectTypeRepository.save(projectType);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> saveProjectTypeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all project types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of ProjectType entities contain all project types from the database that belong to the same branch
     */
    public List<ProjectType> getProjectTypesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return projectTypeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> getProjectTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active project types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of ProjectType entities contain all active project types from the database that belong to the same branch
     */
    public List<ProjectType> getActiveProjectTypesFromDatabase(String token) {
        try {
            Predicate<ProjectType> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getProjectTypesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectTypeService -> getActiveProjectTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a project type entity from the database based on its ID.
     *
     * @param id the ID of the project type entity to retrieve
     * @return a ProjectType entity representing the requested project type, or null if the ID is not valid or an error occurs
     */
    public ProjectType getProjectTypeFromDatabaseById(String id) {
        try {
            return projectTypeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectTypeService -> getProjectTypeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a ProjectType object from the database that matches the given project type and branch code.
     *
     * @param projectType a String representing the project type to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a ProjectType object if one is found, or null if none is found or if an exception occurs during the search
     */
    public ProjectType getProjectTypeFromDatabaseByProjectTypeAndBranchCode(String projectType, String branchCode) {
        try {
            return projectTypeRepository.findByProjectTypeAndBranchCode(projectType, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectTypeService -> getProjectTypeFromDatabaseByProjectTypeAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a project type from the database by its ID.
     *
     * @param id the ID of the project type to be deleted
     */
    public void deleteProjectTypeFromDatabase(String id) {
        try {
            projectTypeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectTypeService -> deleteProjectTypeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a project type exists in the database by its ID.
     *
     * @param id the ID of the project type to check for existence
     * @return true if a project type with the specified ID exists in the database, false otherwise
     */
    public boolean isProjectTypeExistById(String id) {
        try {
            return projectTypeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectTypeService -> isProjectTypeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the project type data transfer object by checking if the required fields
     * are not empty and if the project type already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param projectTypeDTO the project type data transfer object to be validated
     * @param branchCode      the branch code for which to validate the project type
     * @return a list of validation error messages, or an empty list if the project type is valid
     */
    private List<String> validateProjectType(ProjectTypeDTO projectTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if projectType fields
        validations.addAll(validateProjectTypeFields(projectTypeDTO));
        // Check if projectType exists
        validations.addAll(validateProjectTypeExistence(projectTypeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the project type data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param projectTypeDTO the project type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateProjectTypeFields(ProjectTypeDTO projectTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(projectTypeDTO.getProjectType())) {
            validations.add(ProjectTypeValidationMessages.EMPTY_PROJECT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(projectTypeDTO.getDescription())) {
            validations.add(ProjectTypeValidationMessages.EMPTY_DESCRIPTION);
        } else if (ValidatorUtils.statusValidator.test(projectTypeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the project type data transfer object by checking if the project type already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param projectTypeDTO the project type data transfer object to be validated
     * @param branchCode      the branch code of the project type to check for existence
     * @return a list of validation error messages, or an empty list if the project type is new or valid
     */
    private List<String> validateProjectTypeExistence(ProjectTypeDTO projectTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        ProjectType projectType = getProjectTypeFromDatabaseByProjectTypeAndBranchCode(projectTypeDTO.getProjectType(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(projectType)) {
            if (!ValidatorUtils.stringNullValidator.test(projectTypeDTO.getId())) { // Edit project type
                if (!projectTypeDTO.getId().equalsIgnoreCase(projectType.getId().toString())) {
                    validations.add(ProjectTypeValidationMessages.EXIT_PROJECT_TYPE);
                }
            } else { // New project type
                validations.add(ProjectTypeValidationMessages.EXIT_PROJECT_TYPE);
            }
        }
        return validations;
    }
}
