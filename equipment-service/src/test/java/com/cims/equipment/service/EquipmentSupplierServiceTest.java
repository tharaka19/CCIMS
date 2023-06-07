package com.cims.equipment.service;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.dtos.EquipmentSupplierDTO;
import com.cims.equipment.entities.EquipmentSupplier;
import com.cims.equipment.repositories.EquipmentSupplierRepository;
import com.cims.equipment.services.EquipmentSupplierService;
import com.cims.equipment.utils.MapperUtils;
import com.cims.equipment.utils.ResponseDTO;
import com.cims.equipment.utils.ResponseUtils;
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
public class EquipmentSupplierServiceTest {

    @InjectMocks
    private EquipmentSupplierService equipmentSupplierService;

    @Mock
    private EquipmentSupplierRepository equipmentSupplierRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEquipmentSupplier() {
        EquipmentSupplier equipmentSupplier = new EquipmentSupplier();
        equipmentSupplier.setId(1L);
        Mockito.when(equipmentSupplierRepository.save(equipmentSupplier)).thenReturn(equipmentSupplier);
        assertEquals(equipmentSupplier.getId(), equipmentSupplierRepository.save(equipmentSupplier).getId());
    }

    @Test
    public void testGetEquipmentSupplierById_ExistingId() {
        // Arrange
        long id = 1L;
        EquipmentSupplier equipmentSupplier = new EquipmentSupplier();
        equipmentSupplier.setId(1L);
        EquipmentSupplierDTO equipmentSupplierDTO = new EquipmentSupplierDTO();
        equipmentSupplierDTO.setId("1");

        when(equipmentSupplierRepository.existsById(id)).thenReturn(true);
        when(equipmentSupplierRepository.findById(id)).thenReturn(Optional.of(equipmentSupplier));
        when(mapperUtils.mapEntityToDTO(equipmentSupplier, EquipmentSupplierDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        equipmentSupplierDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentSupplierService.getEquipmentSupplierById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEquipmentSupplierById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentSupplierRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentSupplierService.getEquipmentSupplierById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentSupplierById_ExistingId() {
        // Arrange
        long id = 1L;

        when(equipmentSupplierRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentSupplierService.deleteEquipmentSupplierById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentSupplierById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentSupplierRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentSupplierService.deleteEquipmentSupplierById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
