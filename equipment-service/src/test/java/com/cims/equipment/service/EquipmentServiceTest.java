package com.cims.equipment.service;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.dtos.EquipmentDTO;
import com.cims.equipment.entities.Equipment;
import com.cims.equipment.repositories.EquipmentRepository;
import com.cims.equipment.services.EquipmentService;
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
public class EquipmentServiceTest {

    @InjectMocks
    private EquipmentService equipmentService;

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        Mockito.when(equipmentRepository.save(equipment)).thenReturn(equipment);
        assertEquals(equipment.getId(), equipmentRepository.save(equipment).getId());
    }

    @Test
    public void testGetEquipmentById_ExistingId() {
        // Arrange
        long id = 1L;
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        EquipmentDTO equipmentDTO = new EquipmentDTO();
        equipmentDTO.setId("1");

        when(equipmentRepository.existsById(id)).thenReturn(true);
        when(equipmentRepository.findById(id)).thenReturn(Optional.of(equipment));
        when(mapperUtils.mapEntityToDTO(equipment, EquipmentDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        equipmentDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentService.getEquipmentById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEquipmentById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentService.getEquipmentById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentById_ExistingId() {
        // Arrange
        long id = 1L;

        when(equipmentRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentService.deleteEquipmentById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentService.deleteEquipmentById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
