package com.cims.employee.controllers;

import com.cims.employee.dtos.DocumentTypeDTO;
import com.cims.employee.services.DocumentTypeService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The DocumentTypeController class handles HTTP requests for Document Type management endpoints.
 */
@RestController
@RequestMapping("/employee/documentType")
public class DocumentTypeController {

    @Autowired
    private DocumentTypeService documentTypeService;

    /**
     * Saves or updates a Document Type in the database.
     *
     * @param token           the authorization token for the request.
     * @param documentTypeDTO the DTO representing the Document Type to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody DocumentTypeDTO documentTypeDTO) {
        return documentTypeService.saveUpdateDocumentType(token, documentTypeDTO);
    }

    /**
     * Gets all Document Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Document Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return documentTypeService.getAllDocumentTypes(token);
    }

    /**
     * Gets all active Document Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Document Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return documentTypeService.getAllActiveDocumentTypes(token);
    }

    /**
     * Gets a Document Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Document Type to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Document Type DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return documentTypeService.getDocumentTypeById(id);
    }

    /**
     * Deletes a Document Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Document Type to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return documentTypeService.deleteDocumentTypeById(id);
    }
}
