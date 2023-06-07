package com.cims.project.services;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.constants.enums.Status;
import com.cims.project.constants.validationMessages.ClientProjectValidationMessages;
import com.cims.project.dtos.ClientProjectDTO;
import com.cims.project.dtos.UserAccountResponseDTO;
import com.cims.project.entities.ClientProject;
import com.cims.project.proxyClients.UserServiceClient;
import com.cims.project.repositories.ClientProjectRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to clientProject.
 */
@Service
@Transactional
@Slf4j
public class ClientProjectService {

    @Autowired
    private ClientProjectRepository clientProjectRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a client project in the database based on the provided client project DTO.
     *
     * @param clientProjectDTO the DTO representing the client project to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateClientProject(String token, ClientProjectDTO clientProjectDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateClientProject(clientProjectDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            clientProjectDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            clientProjectDTO.setStatus(Status.PENDING);
            saveClientProjectToDatabase(mapperUtils.mapDTOToEntity(clientProjectDTO, ClientProject.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, clientProjectDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> saveUpdateProject()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all client projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientProjectDTOs representing all client projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllClientProjects(String token) {
        try {
            List<ClientProject> clientProjects = getClientProjectsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(clientProjects, ClientProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getAllClientProjects()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active client projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientProjectDTOs representing all active client projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveClientProjects(String token) {
        try {
            List<ClientProject> clientProjects = getActiveClientProjectsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(clientProjects, ClientProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getAllActiveClientProjects()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active client projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientProjectDTOs representing all active client projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveClientProjectsByProject(String token, String projectId) {
        try {
            List<ClientProject> clientProjects = getActiveClientProjectsFromDatabaseByProject(token, projectId);
            return mapperUtils.mapEntitiesToDTOs(clientProjects, ClientProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getAllActiveClientProjectsByProject()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a client project from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the client projects to be retrieved
     * @return a ResponseEntity containing a ClientProjectDTO representing the client projects with the provided ID, or an error response if the client projects does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getClientProjectById(String id) {
        try {
            if (isClientProjectExistById(id)) {
                ClientProject clientProjects = getClientProjectFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(clientProjects, ClientProjectDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getClientProjectById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a client project from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the client project to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteClientProjectById(String id) {
        try {
            if (isClientProjectExistById(id)) {
                deleteProjectFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> deleteClientProjectById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a client project entity to the database.
     *
     * @param clientProject the client project entity to be saved
     */
    public void saveClientProjectToDatabase(ClientProject clientProject) {
        try {
            clientProjectRepository.save(clientProject);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> saveClientProjectToDatabase()" + e);
        }
    }

    /**
     * Retrieves all client project from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of clientProject entities contain all client projects from the database that belong to the same branch
     */
    public List<ClientProject> getClientProjectsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return clientProjectRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getClientProjectsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active client projects from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of ClientProject entities contain all active client projects from the database that belong to the same branch
     */
    public List<ClientProject> getActiveClientProjectsFromDatabase(String token) {
        try {
            Predicate<ClientProject> filterOnStatus = e -> e.getStatus().equals(Status.ACTIVE);
            return getClientProjectsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getActiveClientProjectsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active client projects by project type from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of ClientProject entities contain all active client projects by project from the database that belong to the same branch
     */
    public List<ClientProject> getActiveClientProjectsFromDatabaseByProject(String token, String projectId) {
        try {
            Predicate<ClientProject> filterOnProjectType = e -> e.getProject().getId() == Long.parseLong(projectId);
            return getActiveClientProjectsFromDatabase(token).stream().filter(filterOnProjectType).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectService -> getActiveClientProjectsFromDatabaseByProject()" + e);
            return null;
        }
    }

    /**
     * Retrieves a client project entity from the database based on its ID.
     *
     * @param id the ID of the client project entity to retrieve
     * @return a ClientProject entity representing the requested client project, or null if the ID is not valid or an error occurs
     */
    public ClientProject getClientProjectFromDatabaseById(String id) {
        try {
            return clientProjectRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientProjectService  -> getClientProjectFromDatabaseById()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Client Project object from the database that matches the given client project and branch code.
     *
     * @param projectNumber a String representing the client project number to search for
     * @param branchCode    a String representing the branch code to search for
     * @return a Client Project object if one is found, or null if none is found or if an exception occurs during the search
     */
    public ClientProject getClientProjectFromDatabaseByProjectNumberAndBranchCode(String projectNumber, String branchCode) {
        try {
            return clientProjectRepository.findByProjectNumberAndBranchCode(projectNumber, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientProjectService -> getClientProjectFromDatabaseByProjectNumberAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a client project from the database by its ID.
     *
     * @param id the ID of the client project to be deleted
     */
    public void deleteProjectFromDatabase(String id) {
        try {
            clientProjectRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientProjectService -> deleteProjectFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a client project exists in the database by its ID.
     *
     * @param id the ID of the client project to check for existence
     * @return true if a client project with the specified ID exists in the database, false otherwise
     */
    public boolean isClientProjectExistById(String id) {
        try {
            return clientProjectRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ClientProjectService -> isClientProjectExistById()" + e);
            return false;
        }
    }

    /**
     * Validates the client project data transfer object by checking if the required fields
     * are not empty and if the client project already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param clientProjectDTO the client project data transfer object to be validated
     * @param branchCode       the branch code for which to validate the client project
     * @return a list of validation error messages, or an empty list if the client project is valid
     */
    private List<String> validateClientProject(ClientProjectDTO clientProjectDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if project fields
        validations.addAll(validateClientProjectFields(clientProjectDTO));
        // Check if project exists
        validations.addAll(validateClientProjectExistence(clientProjectDTO, branchCode));
        return validations;
    }

    /**
     * Validates the client project data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param clientProjectDTO the client project data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateClientProjectFields(ClientProjectDTO clientProjectDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(clientProjectDTO.getProjectId())) {
            validations.add(ClientProjectValidationMessages.EMPTY_PROJECT);
        } else if (ValidatorUtils.stringNullValidator.test(clientProjectDTO.getClientId())) {
            validations.add(ClientProjectValidationMessages.EMPTY_CLIENT);
        } else if (ValidatorUtils.stringNullValidator.test(clientProjectDTO.getProjectNumber())) {
            validations.add(ClientProjectValidationMessages.EMPTY_PROJECT_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(clientProjectDTO.getProjectDetails())) {
            validations.add(ClientProjectValidationMessages.EMPTY_PROJECT_DETAILS);
        } else if (ValidatorUtils.dateValidator.test(clientProjectDTO.getProjectStartDate())) {
            validations.add(ClientProjectValidationMessages.EMPTY_PROJECT_START_DATE);
        } else if (LocalDate.now().isAfter(clientProjectDTO.getProjectStartDate())) {
            validations.add(ClientProjectValidationMessages.INVALID_PROJECT_START_DATE);
        }
        return validations;
    }

    /**
     * Validates the client project data transfer object by checking if the client project already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param clientProjectDTO the client project data transfer object to be validated
     * @param branchCode       the branch code of the client project to check for existence
     * @return a list of validation error messages, or an empty list if the client project is new or valid
     */
    private List<String> validateClientProjectExistence(ClientProjectDTO clientProjectDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        ClientProject clientProject = getClientProjectFromDatabaseByProjectNumberAndBranchCode(clientProjectDTO.getProjectNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(clientProject)) {
            if (!ValidatorUtils.stringNullValidator.test(clientProjectDTO.getId())) { // Edit client project
                if (!clientProjectDTO.getId().equalsIgnoreCase(clientProject.getId().toString())) {
                    validations.add(ClientProjectValidationMessages.EXIT_PROJECT_NUMBER);
                }
            } else { // New client project
                validations.add(ClientProjectValidationMessages.EXIT_PROJECT_NUMBER);
            }
        }
        return validations;
    }
}
