package com.cims.employee.service;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.dtos.EmployeeTypeDTO;
import com.cims.employee.entities.EmployeeType;
import com.cims.employee.repositories.EmployeeTypeRepository;
import com.cims.employee.services.EmployeeTypeService;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeTypeServiceTest {

    @InjectMocks
    private EmployeeTypeService employeeTypeService;

    @Mock
    private EmployeeTypeRepository employeeTypeRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEmployeeType() {
        EmployeeType employeeType = new EmployeeType(1L, "Manager", "This is manager", 20000d, 2000d, 2000d, 4000d, 2000d, "CM", null, Status.ACTIVE);
        Mockito.when(employeeTypeRepository.save(employeeType)).thenReturn(employeeType);
        assertEquals(employeeType.getId(), employeeTypeRepository.save(employeeType).getId());
    }

    @Test
    public void testGetAllEmployeeTypes() {
        Mockito.when(employeeTypeRepository.findAll()).thenReturn(Stream.of(new EmployeeType(1L, "Manager", "This is manager", 20000d, 2000d, 2000d, 4000d, 2000d, "CM", null, Status.ACTIVE),
                new EmployeeType(2L, "Manager", "This is manager", 20000d, 2000d, 2000d, 4000d, 2000d, "CM", null, Status.ACTIVE)).collect(Collectors.toList()));
        assertEquals(2, employeeTypeRepository.findAll().size());
    }

    @Test
    public void testGetEmployeeTypeById_ExistingId() {
        // Arrange
        long id = 1L;
        EmployeeType employeeType = new EmployeeType(1L, "Manager", "This is manager", 20000d, 2000d, 2000d, 4000d, 2000d, "CM", null, Status.ACTIVE);
        EmployeeTypeDTO employeeTypeDTO = new EmployeeTypeDTO("1", "Manager", "This is manager", 20000d, 2000d, 2000d, 4000d, 2000d, "CM", Status.ACTIVE);

        when(employeeTypeRepository.existsById(id)).thenReturn(true);
        when(employeeTypeRepository.findById(id)).thenReturn(Optional.of(employeeType));
        when(mapperUtils.mapEntityToDTO(employeeType, EmployeeTypeDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        employeeTypeDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = employeeTypeService.getEmployeeTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEmployeeTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(employeeTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = employeeTypeService.getEmployeeTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEmployeeTypeById_ExistingId() {
        // Arrange
        long id = 1L;

        when(employeeTypeRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = employeeTypeService.deleteEmployeeTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEmployeeTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(employeeTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = employeeTypeService.deleteEmployeeTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
