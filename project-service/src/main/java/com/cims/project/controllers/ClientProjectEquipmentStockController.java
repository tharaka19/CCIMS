package com.cims.project.controllers;

import com.cims.project.dtos.ClientProjectEquipmentStockDTO;
import com.cims.project.services.ClientProjectEquipmentStockService;
import com.cims.project.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The ClientProjectEquipmentStockController class handles HTTP requests for Client Project Equipment Stock management endpoints.
 */
@RestController
@RequestMapping("/project/clientProjectStock")
public class ClientProjectEquipmentStockController {

    @Autowired
    private ClientProjectEquipmentStockService clientProjectEquipmentStockService;

    /**
     * Saves or updates a Client Project Equipment Stock in the database.
     *
     * @param token            the authorization token for the request.
     * @param clientProjectEquipmentStockDTO the DTO representing the Client Project Equipment Stock to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        return clientProjectEquipmentStockService.saveUpdateClientProjectEquipmentStock(token, clientProjectEquipmentStockDTO);
    }

    /**
     * Gets all Equipment Stock History from the database by Client Project.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Client Project Equipment Stock DTOs and an HTTP status code.
     */
    @GetMapping("/getAllByClientProject/{clientProjectId}")
    public ResponseEntity<ResponseDTO> getAllByClientProject(@RequestHeader(name = "Authorization") String token, @PathVariable String clientProjectId) {
        return clientProjectEquipmentStockService.getAllClientProjectEquipmentStockByClientProject(token, clientProjectId);
    }
}
