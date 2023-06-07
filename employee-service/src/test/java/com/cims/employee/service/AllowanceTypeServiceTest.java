package com.cims.employee.service;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.dtos.AllowanceTypeDTO;
import com.cims.employee.entities.AllowanceType;
import com.cims.employee.repositories.AllowanceTypeRepository;
import com.cims.employee.services.AllowanceTypeService;
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
public class AllowanceTypeServiceTest {

    @InjectMocks
    private AllowanceTypeService allowanceTypeService;

    @Mock
    private AllowanceTypeRepository allowanceTypeRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateAllowanceType() {
        AllowanceType allowanceType = new AllowanceType(1L, "Food", "Food", 2000d, "CM", null, Status.ACTIVE);
        Mockito.when(allowanceTypeRepository.save(allowanceType)).thenReturn(allowanceType);
        assertEquals(allowanceType.getId(), allowanceTypeRepository.save(allowanceType).getId());
    }

    @Test
    public void testGetAllAllowanceTypes() {
        Mockito.when(allowanceTypeRepository.findAll()).thenReturn(Stream.of(new AllowanceType(1L, "Food", "Food", 2000d, "CM", null, Status.ACTIVE),
                new AllowanceType(2L, "Food", "Food", 2000d, "CM", null, Status.ACTIVE)).collect(Collectors.toList()));
        assertEquals(2, allowanceTypeRepository.findAll().size());
    }

    @Test
    public void testGetAllowanceTypeById_ExistingId() {
        // Arrange
        long id = 1L;
        AllowanceType allowanceType = new AllowanceType(1L, "Food", "Food", 2000d, "CM", null, Status.ACTIVE);
        AllowanceTypeDTO allowanceTypeDTO = new AllowanceTypeDTO("1", "Food", "Food", 2000d, "CM", Status.ACTIVE);

        when(allowanceTypeRepository.existsById(id)).thenReturn(true);
        when(allowanceTypeRepository.findById(id)).thenReturn(Optional.of(allowanceType));
        when(mapperUtils.mapEntityToDTO(allowanceType, AllowanceTypeDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        allowanceTypeDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = allowanceTypeService.getAllowanceTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetAllowanceTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(allowanceTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = allowanceTypeService.getAllowanceTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteAllowanceTypeById_ExistingId() {
        // Arrange
        long id = 1L;

        when(allowanceTypeRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = allowanceTypeService.deleteAllowanceTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteAllowanceTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(allowanceTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = allowanceTypeService.deleteAllowanceTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
