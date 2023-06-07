package com.cims.project.controllers;

import com.cims.project.dtos.ProjectTypeDTO;
import com.cims.project.services.ProjectTypeService;
import com.cims.project.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ProjectTypeController class handles HTTP requests for Project Type management endpoints.
 */
@RestController
@RequestMapping("/project/projectType")
public class ProjectTypeController {

    @Autowired
    private ProjectTypeService projectTypeService;

    /**
     * Saves or updates a Project Type in the database.
     *
     * @param token            the authorization token for the request.
     * @param projectTypeDTO the DTO representing the Project Type to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody ProjectTypeDTO projectTypeDTO) {
        return projectTypeService.saveUpdateProjectType(token, projectTypeDTO);
    }

    /**
     * Gets all Project Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Project Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return projectTypeService.getAllProjectTypes(token);
    }

    /**
     * Gets all active Project Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Project Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return projectTypeService.getAllActiveProjectTypes(token);
    }

    /**
     * Gets a Project Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Project Type to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Project Type DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return projectTypeService.getProjectTypeById(id);
    }

    /**
     * Deletes a Project Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Project Type to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return projectTypeService.deleteProjectTypeById(id);
    }
}
