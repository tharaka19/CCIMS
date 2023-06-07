package com.cims.user.services;

import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.dtos.UserProxyDTO;
import com.cims.user.entities.UserRole;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.ResponseUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class that handles data synchronization operations.
 */
@Service
@Transactional
@Slf4j
public class DataSyncService {

  @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Initiates a data synchronization process for a system admin and user account by saving or updating the corresponding user role and user account records in the database.
     *
     * @param userProxyDTO UserProxyDTO object containing the data to synchronize.
     * @return ResponseEntity with a ResponseDTO indicating the result of the synchronization process.
     */
    public ResponseEntity<ResponseDTO> postSystemAdminAndUserAccount(UserProxyDTO userProxyDTO) {
        try {
            UserRole userRole = userRoleService.saveUpdateUserRoleByAdmin(userProxyDTO.getUserRoleDTO(), userProxyDTO.getBranchName(), userProxyDTO.getBranchCode());
            userAccountService.saveUpdateUserAccountByAdmin(userProxyDTO.getUserAccountSendDTO(), userRole, userProxyDTO.getBranchCode());
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SYNCED_SUCCESSFULLY, userProxyDTO, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            log.warn("/**************** Exception in DataSyncService -> postSystemAdminAndUserAccount()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, null, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
