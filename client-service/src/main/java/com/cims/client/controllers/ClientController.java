package com.cims.client.controllers;

import com.cims.client.dtos.ClientDTO;
import com.cims.client.services.ClientService;
import com.cims.client.utils.ResponseDTO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ClientController class handles HTTP requests for Client management endpoints.
 */
@RestController
@RequestMapping("/client/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * Saves or updates a Client in the database.
     *
     * @param token     the authorization token for the request.
     * @param clientDTO the DTO representing the Client to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody ClientDTO clientDTO) {
        return clientService.saveUpdateClient(token, clientDTO);
    }

    /**
     * Gets all Clients from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Client DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return clientService.getAllClients(token);
    }

    /**
     * Gets all active Clients from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Client DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return clientService.getAllActiveClients(token);
    }

    /**
     * Gets a Client by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Client to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Client DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return clientService.getClientById(id);
    }

    /**
     * Deletes a Client by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Client to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return clientService.deleteClientById(id);
    }
}
