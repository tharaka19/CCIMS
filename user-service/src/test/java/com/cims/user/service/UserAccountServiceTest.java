package com.cims.user.service;

import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.constants.enums.Status;
import com.cims.user.dtos.UserAccountResponseDTO;
import com.cims.user.entities.UserAccount;
import com.cims.user.repositories.UserAccountRepository;
import com.cims.user.services.UserAccountService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTest {

    @InjectMocks
    private UserAccountService userAccountService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateUserAccount() {
        UserAccount userAccount = new UserAccount(1L, "ADMIN", "sf2143", "token", "CM", 1L, null, Status.ACTIVE);
        Mockito.when(userAccountRepository.save(userAccount)).thenReturn(userAccount);
        assertEquals(userAccount.getId(), userAccountRepository.save(userAccount).getId());
    }

    @Test
    public void testGetAllUserAccounts() {
        Mockito.when(userAccountRepository.findAll()).thenReturn(Stream.of(new UserAccount(1L, "ADMIN", "sf2143", "token", "CM", 1L, null, Status.ACTIVE),
                new UserAccount(1L, "ADMIN", "sf2143", "token", "CM", 1L, null, Status.ACTIVE)).collect(Collectors.toList()));
        assertEquals(2, userAccountRepository.findAll().size());
    }

    @Test
    public void testGetUserAccountById_ExistingId() {
        // Arrange
        long id = 1L;
        UserAccount userAccount = new UserAccount(1L, "ADMIN", "sf2143", "token", "CM", 1L, null, Status.ACTIVE);
        UserAccountResponseDTO userAccountDTO = new UserAccountResponseDTO(null, "Shehan", "CM", "1", Status.ACTIVE);

        when(userAccountRepository.existsById(id)).thenReturn(true);
        when(userAccountRepository.findById(id)).thenReturn(Optional.of(userAccount));
        when(mapperUtils.mapEntityToDTO(userAccount, UserAccountResponseDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        userAccountDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = userAccountService.getUserAccountById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetUserAccountById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(userAccountRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = userAccountService.getUserAccountById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteUserAccountById_ExistingId() {
        // Arrange
        long id = 1L;

        when(userAccountRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = userAccountService.deleteUserAccountById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteUserAccountById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(userAccountRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = userAccountService.deleteUserAccountById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
