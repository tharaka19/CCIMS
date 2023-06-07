package com.cims.project.controllers;

import com.cims.project.dtos.ProjectDTO;
import com.cims.project.services.ProjectService;
import com.cims.project.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ProjectController class handles HTTP requests for Project management endpoints.
 */
@RestController
@RequestMapping("/project/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    /**
     * Saves or updates a Project in the database.
     *
     * @param token      the authorization token for the request.
     * @param projectDTO the DTO representing the Project to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody ProjectDTO projectDTO) {
        return projectService.saveUpdateProject(token, projectDTO);
    }

    /**
     * Gets all Projects from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return projectService.getAllProjects(token);
    }

    /**
     * Gets all active Projects from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return projectService.getAllActiveProjects(token);
    }

    /**
     * Gets all active Projects by Project Type from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActiveByProjectType/{projectTypeId}")
    public ResponseEntity<ResponseDTO> getAllActiveByProjectType(@RequestHeader(name = "Authorization") String token, @PathVariable String projectTypeId) {
        return projectService.getAllActiveProjectsByProjectType(token, projectTypeId);
    }

    /**
     * Gets a Project by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Project to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Project DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return projectService.getProjectById(id);
    }

    /**
     * Deletes a Project by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Project to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return projectService.deleteProjectById(id);
    }
}
