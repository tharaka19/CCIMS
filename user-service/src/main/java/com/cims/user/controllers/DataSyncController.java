package com.cims.user.controllers;

import com.cims.user.dtos.UserProxyDTO;
import com.cims.user.services.DataSyncService;
import com.cims.user.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The DataSyncController class handles HTTP requests for data sync management endpoints.
 */
@RestController
@RequestMapping("/user/dataSync")
public class DataSyncController {

    @Autowired
    private DataSyncService dataSyncService;

    /**
     * POST endpoint to receive a UserProxyDTO object and initiate a data synchronization process for a system admin and user account.
     * @param userProxyDTO UserProxyDTO object containing the data to synchronize.
     * @return ResponseEntity with a ResponseDTO indicating the result of the synchronization process.
     */
    @PostMapping("/postSystemAdminAndUserAccount")
    public ResponseEntity<ResponseDTO> postSystemAdminAndUserAccount(@RequestBody UserProxyDTO userProxyDTO) {
        return dataSyncService.postSystemAdminAndUserAccount(userProxyDTO);
    }
}
