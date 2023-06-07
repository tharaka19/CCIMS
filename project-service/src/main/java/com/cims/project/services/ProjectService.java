package com.cims.project.services;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.constants.enums.Status;
import com.cims.project.constants.validationMessages.CommonValidationMessages;
import com.cims.project.constants.validationMessages.ProjectValidationMessages;
import com.cims.project.dtos.ProjectDTO;
import com.cims.project.dtos.UserAccountResponseDTO;
import com.cims.project.entities.Project;
import com.cims.project.proxyClients.UserServiceClient;
import com.cims.project.repositories.ProjectRepository;
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
 * Service class that handles operations related to project.
 */
@Service
@Transactional
@Slf4j
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a project in the database based on the provided project DTO.
     *
     * @param projectDTO the DTO representing the project to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateProject(String token, ProjectDTO projectDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateProject(projectDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            projectDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveProjectToDatabase(mapperUtils.mapDTOToEntity(projectDTO, Project.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, projectDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> saveUpdateProject()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ProjectDTOs representing all projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllProjects(String token) {
        try {
            List<Project> projects = getProjectsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(projects, ProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getAllProjects()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ProjectDTOs representing all active projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveProjects(String token) {
        try {
            List<Project> projects = getActiveProjectsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(projects, ProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getAllActiveProjects()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active projects from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ProjectDTOs representing all active projects from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveProjectsByProjectType(String token, String projectTypeId) {
        try {
            List<Project> projects = getActiveProjectsFromDatabaseByProjectType(token, projectTypeId);
            return mapperUtils.mapEntitiesToDTOs(projects, ProjectDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getAllActiveProjects()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a project from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the project to be retrieved
     * @return a ResponseEntity containing a ProjectDTO representing the project with the provided ID, or an error response if the project does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getProjectById(String id) {
        try {
            if (isProjectExistById(id)) {
                Project projects = getProjectFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(projects, ProjectDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getProjectById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a project from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the project to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteProjectById(String id) {
        try {
            if (isProjectExistById(id)) {
                deleteProjectFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> deleteProjectById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a project entity to the database.
     *
     * @param project the project entity to be saved
     */
    public void saveProjectToDatabase(Project project) {
        try {
            projectRepository.save(project);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> saveProjectToDatabase()" + e);
        }
    }

    /**
     * Retrieves all projects from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of project entities contain all projects from the database that belong to the same branch
     */
    public List<Project> getProjectsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return projectRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getProjectsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active projects from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Project entities contain all active projects from the database that belong to the same branch
     */
    public List<Project> getActiveProjectsFromDatabase(String token) {
        try {
            Predicate<Project> filterOnStatus = e -> e.getStatus().equals(Status.ACTIVE);
            return getProjectsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getActiveProjectsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active projects by project type from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Project entities contain all active projects by project type from the database that belong to the same branch
     */
    public List<Project> getActiveProjectsFromDatabaseByProjectType(String token, String projectTypeId) {
        try {
            Predicate<Project> filterOnProjectType = e -> e.getProjectType().getId() == Long.parseLong(projectTypeId);
            return getActiveProjectsFromDatabase(token).stream().filter(filterOnProjectType).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ProjectService -> getActiveProjectsFromDatabaseByProjectType()" + e);
            return null;
        }
    }

    /**
     * Retrieves a project entity from the database based on its ID.
     *
     * @param id the ID of the project entity to retrieve
     * @return a Project entity representing the requested project, or null if the ID is not valid or an error occurs
     */
    public Project getProjectFromDatabaseById(String id) {
        try {
            return projectRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectService -> getProjectFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Project object from the database that matches the given project and branch code.
     *
     * @param projectNumber a String representing the project number to search for
     * @param branchCode    a String representing the branch code to search for
     * @return a Project object if one is found, or null if none is found or if an exception occurs during the search
     */
    public Project getProjectFromDatabaseByProjectNumberAndBranchCode(String projectNumber, String branchCode) {
        try {
            return projectRepository.findByProjectNumberAndBranchCode(projectNumber, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectService -> getProjectFromDatabaseByProjectNumberAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a project from the database by its ID.
     *
     * @param id the ID of the project to be deleted
     */
    public void deleteProjectFromDatabase(String id) {
        try {
            projectRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectService -> deleteProjectFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a project exists in the database by its ID.
     *
     * @param id the ID of the project to check for existence
     * @return true if a project with the specified ID exists in the database, false otherwise
     */
    public boolean isProjectExistById(String id) {
        try {
            return projectRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in ProjectService -> isProjectExist()" + e);
            return false;
        }
    }

    /**
     * Validates the project data transfer object by checking if the required fields
     * are not empty and if the project already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param projectDTO the project data transfer object to be validated
     * @param branchCode the branch code for which to validate the project
     * @return a list of validation error messages, or an empty list if the project is valid
     */
    private List<String> validateProject(ProjectDTO projectDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if project fields
        validations.addAll(validateProjectFields(projectDTO));
        // Check if project exists
        validations.addAll(validateProjectExistence(projectDTO, branchCode));
        return validations;
    }

    /**
     * Validates the project data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param projectDTO the project data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateProjectFields(ProjectDTO projectDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(projectDTO.getProjectTypeId())) {
            validations.add(ProjectValidationMessages.EMPTY_PROJECT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(projectDTO.getProjectNumber())) {
            validations.add(ProjectValidationMessages.EMPTY_PROJECT_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(projectDTO.getProjectName())) {
            validations.add(ProjectValidationMessages.EMPTY_PROJECT_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(projectDTO.getProjectDetails())) {
            validations.add(ProjectValidationMessages.EMPTY_PROJECT_DETAILS);
        } else if (ValidatorUtils.priceNullValidator.test(projectDTO.getProjectPrice())) {
            validations.add(ProjectValidationMessages.EMPTY_PROJECT_PRICE);
        } else if (ValidatorUtils.statusValidator.test(projectDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!ValidatorUtils.priceValidator.test(projectDTO.getProjectPrice())) {
            validations.add(ProjectValidationMessages.INVALID_PROJECT_PRICE);
        }
        return validations;
    }

    /**
     * Validates the project data transfer object by checking if the project already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param projectDTO the project data transfer object to be validated
     * @param branchCode the branch code of the project to check for existence
     * @return a list of validation error messages, or an empty list if the project is new or valid
     */
    private List<String> validateProjectExistence(ProjectDTO projectDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        Project project = getProjectFromDatabaseByProjectNumberAndBranchCode(projectDTO.getProjectNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(project)) {
            if (!ValidatorUtils.stringNullValidator.test(projectDTO.getId())) { // Edit project
                if (!projectDTO.getId().equalsIgnoreCase(project.getId().toString())) {
                    validations.add(ProjectValidationMessages.EXIT_PROJECT_NUMBER);
                }
            } else { // New project
                validations.add(ProjectValidationMessages.EXIT_PROJECT_NUMBER);
            }
        }
        return validations;
    }
}
