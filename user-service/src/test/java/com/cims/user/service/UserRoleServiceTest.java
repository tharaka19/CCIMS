package com.cims.user.service;

import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.constants.enums.Status;
import com.cims.user.dtos.UserRoleDTO;
import com.cims.user.entities.UserRole;
import com.cims.user.repositories.UserAccountRepository;
import com.cims.user.repositories.UserRoleRepository;
import com.cims.user.services.UserAccountService;
import com.cims.user.services.UserRoleService;
import com.cims.user.utils.MapperUtils;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.ResponseUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceTest {

    @InjectMocks
    private UserRoleService userRoleService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateUserRole() {
        UserRole userRole = new UserRole(1L, "ADMIN", "This is admin", "Mirigama", "CM", 1L,Status.ACTIVE);
        Mockito.when(userRoleRepository.save(userRole)).thenReturn(userRole);
        assertEquals(userRole.getId(), userRoleRepository.save(userRole).getId());
    }

    @Test
    public void testGetAllUserRoles() {
        Mockito.when(userRoleRepository.findAll()).thenReturn(Stream.of(new UserRole(1L, "ADMIN", "This is admin", "Mirigama", "CM", 1L,Status.ACTIVE),
                new UserRole(1L, "ADMIN", "This is admin", "Mirigama", "CM", 1L,Status.INACTIVE)).collect(Collectors.toList()));
        assertEquals(2, userRoleRepository.findAll().size());
    }

    @Test
    public void testGetUserRoleById_ExistingId() {
        // Arrange
        long id = 1L;
        UserRole userRole = new UserRole(1L, "ADMIN", "This is admin", "Mirigama", "CM", 1L,Status.ACTIVE);
        UserRoleDTO userRoleDTO = new UserRoleDTO(null, "ADMIN", "This is admin", "", "", Status.ACTIVE);

        when(userRoleRepository.existsById(id)).thenReturn(true);
        when(userRoleRepository.findById(id)).thenReturn(Optional.of(userRole));
        when(mapperUtils.mapEntityToDTO(userRole, UserRoleDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        userRoleDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = userRoleService.getUserRoleById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetUserRoleById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(userRoleRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = userRoleService.getUserRoleById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteUserRoleById_ExistingId() {
        // Arrange
        long id = 1L;

        when(userRoleRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = userRoleService.deleteUserRoleById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteUserRoleById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(userRoleRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = userRoleService.deleteUserRoleById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
