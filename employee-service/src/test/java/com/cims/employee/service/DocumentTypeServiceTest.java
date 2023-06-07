package com.cims.employee.service;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.dtos.DocumentTypeDTO;
import com.cims.employee.entities.DocumentType;
import com.cims.employee.repositories.DocumentTypeRepository;
import com.cims.employee.services.DocumentTypeService;
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
public class DocumentTypeServiceTest {

    @InjectMocks
    private DocumentTypeService documentTypeService;

    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateDocumentType() {
        DocumentType documentType = new DocumentType(1L, "NIC", "NIC", "CM", null, Status.ACTIVE);
        Mockito.when(documentTypeRepository.save(documentType)).thenReturn(documentType);
        assertEquals(documentType.getId(), documentTypeRepository.save(documentType).getId());
    }

    @Test
    public void testGetAllDocumentTypes() {
        Mockito.when(documentTypeRepository.findAll()).thenReturn(Stream.of(new DocumentType(1L, "NIC", "NIC", "CM", null, Status.ACTIVE),
                new DocumentType(2L, "NIC", "NIC", "CM", null, Status.ACTIVE)).collect(Collectors.toList()));
        assertEquals(2, documentTypeRepository.findAll().size());
    }

    @Test
    public void testGetDocumentTypeById_ExistingId() {
        // Arrange
        long id = 1L;
        DocumentType documentType = new DocumentType(1L, "NIC", "NIC", "CM", null, Status.ACTIVE);
        DocumentTypeDTO documentTypeDTO = new DocumentTypeDTO("1", "NIC", "NIC", "CM", Status.ACTIVE);

        when(documentTypeRepository.existsById(id)).thenReturn(true);
        when(documentTypeRepository.findById(id)).thenReturn(Optional.of(documentType));
        when(mapperUtils.mapEntityToDTO(documentType, DocumentTypeDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        documentTypeDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = documentTypeService.getDocumentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetDocumentTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(documentTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = documentTypeService.getDocumentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteDocumentTypeById_ExistingId() {
        // Arrange
        long id = 1L;

        when(documentTypeRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = documentTypeService.deleteDocumentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteDocumentTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(documentTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = documentTypeService.deleteDocumentTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
