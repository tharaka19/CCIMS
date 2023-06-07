package com.cims.employee.service;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.dtos.EmployeeProfileDTO;
import com.cims.employee.entities.EmployeeProfile;
import com.cims.employee.repositories.EmployeeProfileRepository;
import com.cims.employee.services.EmployeeProfileService;
import com.cims.employee.utils.MapperUtils;
import com.cims.employee.utils.ResponseDTO;
import com.cims.employee.utils.ResponseUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeProfileServiceTest {

    @InjectMocks
    private EmployeeProfileService employeeProfileService;

    @Mock
    private EmployeeProfileRepository employeeProfileRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEmployeeProfile() {
        EmployeeProfile employeeProfile = new EmployeeProfile();
        employeeProfile.setId(1L);
        Mockito.when(employeeProfileRepository.save(employeeProfile)).thenReturn(employeeProfile);
        assertEquals(employeeProfile.getId(), employeeProfileRepository.save(employeeProfile).getId());
    }

    @Test
    public void testGetEmployeeProfileById_ExistingId() {
        // Arrange
        long id = 1L;
        EmployeeProfile employeeProfile = new EmployeeProfile();
        employeeProfile.setId(1L);
        EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
        employeeProfileDTO.setId("1");

        when(employeeProfileRepository.existsById(id)).thenReturn(true);
        when(employeeProfileRepository.findById(id)).thenReturn(Optional.of(employeeProfile));
        when(mapperUtils.mapEntityToDTO(employeeProfile, EmployeeProfileDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        employeeProfileDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = employeeProfileService.getEmployeeProfileById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEmployeeProfileById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(employeeProfileRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = employeeProfileService.getEmployeeProfileById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEmployeeProfileById_ExistingId() {
        // Arrange
        long id = 1L;

        when(employeeProfileRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = employeeProfileService.deleteEmployeeProfileById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEmployeeProfileById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(employeeProfileRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = employeeProfileService.deleteEmployeeProfileById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
