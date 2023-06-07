package com.cims.equipment.service;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.dtos.EquipmentStockDTO;
import com.cims.equipment.entities.EquipmentStock;
import com.cims.equipment.repositories.EquipmentStockRepository;
import com.cims.equipment.services.EquipmentStockService;
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
public class EquipmentStockServiceTest {

    @InjectMocks
    private EquipmentStockService equipmentStockService;

    @Mock
    private EquipmentStockRepository equipmentStockRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateEquipmentStock() {
        EquipmentStock equipmentStock = new EquipmentStock();
        equipmentStock.setId(1L);
        Mockito.when(equipmentStockRepository.save(equipmentStock)).thenReturn(equipmentStock);
        assertEquals(equipmentStock.getId(), equipmentStockRepository.save(equipmentStock).getId());
    }

    @Test
    public void testGetEquipmentStockById_ExistingId() {
        // Arrange
        long id = 1L;
        EquipmentStock equipmentStock = new EquipmentStock();
        equipmentStock.setId(1L);
        EquipmentStockDTO equipmentStockDTO = new EquipmentStockDTO();
        equipmentStockDTO.setId("1");

        when(equipmentStockRepository.existsById(id)).thenReturn(true);
        when(equipmentStockRepository.findById(id)).thenReturn(Optional.of(equipmentStock));
        when(mapperUtils.mapEntityToDTO(equipmentStock, EquipmentStockDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        equipmentStockDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentStockService.getEquipmentStockById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetEquipmentStockById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentStockRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentStockService.getEquipmentStockById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentStockById_ExistingId() {
        // Arrange
        long id = 1L;

        when(equipmentStockRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentStockService.deleteEquipmentStockById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteEquipmentStockById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(equipmentStockRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = equipmentStockService.deleteEquipmentStockById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
