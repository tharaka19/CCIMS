package com.cims.equipment.service;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.dtos.EquipmentTypeDTO;
import com.cims.equipment.entities.EquipmentType;
import com.cims.equipment.repositories.EquipmentTypeRepository;
import com.cims.equipment.services.EquipmentTypeService;
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
public class EquipmentTypeServiceTest {

    @InjectMocks
    private EquipmentTypeService equipmentTypeService;

    @Mock
    private EquipmentTypeRepository equipmentTypeRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEquipmentType() {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setId(1L);
        Mockito.when(equipmentTypeRepository.save(equipmentType)).thenReturn(equipmentType);
        assertEquals(equipmentType.getId(), equipmentTypeRepository.save(equipmentType).getId());
    }

    @Test
    public void testGetEquipmentTypeById_ExistingId() {
        // Arrange
        long id = 1L;
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setId(1L);
        EquipmentTypeDTO equipmentTypeDTO = new EquipmentTypeDTO();
        equipmentTypeDTO.setId("1");

        when(equipmentTypeRepository.existsById(id)).thenReturn(true);
        when(equipmentTypeRepository.findById(id)).thenReturn(Optional.of(equipmentType));
        when(mapperUtils.mapEntityToDTO(equipmentType, EquipmentTypeDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        equipmentTypeDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentTypeService.getEquipmentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEquipmentTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentTypeService.getEquipmentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentTypeById_ExistingId() {
        // Arrange
        long id = 1L;

        when(equipmentTypeRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentTypeService.deleteEquipmentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentTypeService.deleteEquipmentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
