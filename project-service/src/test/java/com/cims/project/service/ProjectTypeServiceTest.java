package com.cims.project.service;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.dtos.ProjectTypeDTO;
import com.cims.project.entities.ProjectType;
import com.cims.project.repositories.ProjectTypeRepository;
import com.cims.project.services.ProjectTypeService;
import com.cims.project.utils.MapperUtils;
import com.cims.project.utils.ResponseDTO;
import com.cims.project.utils.ResponseUtils;
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
public class ProjectTypeServiceTest {

    @InjectMocks
    private ProjectTypeService projectTypeService;

    @Mock
    private ProjectTypeRepository projectTypeRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateProjectType() {
        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        Mockito.when(projectTypeRepository.save(projectType)).thenReturn(projectType);
        assertEquals(projectType.getId(), projectTypeRepository.save(projectType).getId());
    }

    @Test
    public void testGetProjectTypeById_ExistingId() {
        // Arrange
        long id = 1L;
        ProjectType projectType = new ProjectType();
        projectType.setId(1L);
        ProjectTypeDTO projectTypeDTO = new ProjectTypeDTO();
        projectTypeDTO.setId("1");

        when(projectTypeRepository.existsById(id)).thenReturn(true);
        when(projectTypeRepository.findById(id)).thenReturn(Optional.of(projectType));
        when(mapperUtils.mapEntityToDTO(projectType, ProjectTypeDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        projectTypeDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = projectTypeService.getProjectTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetProjectTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(projectTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = projectTypeService.getProjectTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteProjectTypeById_ExistingId() {
        // Arrange
        long id = 1L;

        when(projectTypeRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = projectTypeService.deleteProjectTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteProjectTypeById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(projectTypeRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = projectTypeService.deleteProjectTypeById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
