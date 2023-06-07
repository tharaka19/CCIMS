package com.cims.project.controllers;

import com.cims.project.dtos.ClientProjectDTO;
import com.cims.project.services.ClientProjectService;
import com.cims.project.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ProjectController class handles HTTP requests for Client Client Project management endpoints.
 */
@RestController
@RequestMapping("/project/clientProject")
public class ClientProjectController {

    @Autowired
    private ClientProjectService clientProjectService;

    /**
     * Saves or updates a Client Project in the database.
     *
     * @param token            the authorization token for the request.
     * @param clientProjectDTO the DTO representing the Client Project to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody ClientProjectDTO clientProjectDTO) {
        return clientProjectService.saveUpdateClientProject(token, clientProjectDTO);
    }

    /**
     * Gets all Client Projects from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return clientProjectService.getAllClientProjects(token);
    }

    /**
     * Gets all active Client Projects from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Client Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return clientProjectService.getAllActiveClientProjects(token);
    }

    /**
     * Gets all active Client Projects by Project from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Client Project DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActiveByProjectType/{projectId}")
    public ResponseEntity<ResponseDTO> getAllActiveByProject(@RequestHeader(name = "Authorization") String token, @PathVariable String projectId) {
        return clientProjectService.getAllActiveClientProjectsByProject(token, projectId);
    }

    /**
     * Gets a Client Project by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Client Project to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Client Project DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return clientProjectService.getClientProjectById(id);
    }

    /**
     * Deletes a Client Project by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Client Project to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return clientProjectService.deleteClientProjectById(id);
    }
}
