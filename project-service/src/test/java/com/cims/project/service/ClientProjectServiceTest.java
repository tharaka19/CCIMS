package com.cims.project.service;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.dtos.ClientProjectDTO;
import com.cims.project.dtos.ProjectDTO;
import com.cims.project.entities.ClientProject;
import com.cims.project.entities.Project;
import com.cims.project.repositories.ClientProjectRepository;
import com.cims.project.repositories.ProjectRepository;
import com.cims.project.services.ClientProjectService;
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
public class ClientProjectServiceTest {

    @InjectMocks
    private ClientProjectService clientProjectService;

    @Mock
    private ClientProjectRepository clientProjectRepository;

    @Mock
    private MapperUtils mapperUtils;

    @Mock
    private ResponseUtils responseUtils;

    @Test
     void testSaveUpdateClientProject() {
        ClientProject clientProject = new ClientProject();
        clientProject.setId(1L);
        Mockito.when(clientProjectRepository.save(clientProject)).thenReturn(clientProject);
        assertEquals(clientProject.getId(), clientProjectRepository.save(clientProject).getId());
    }

    @Test
    public void testGetClientProjectById_ExistingId() {
        // Arrange
        long id = 1L;
        ClientProject clientProject = new ClientProject();
        clientProject.setId(1L);
        ClientProjectDTO clientProjectDTO = new ClientProjectDTO();
        clientProjectDTO.setId("1");

        when(clientProjectRepository.existsById(id)).thenReturn(true);
        when(clientProjectRepository.findById(id)).thenReturn(Optional.of(clientProject));
        when(mapperUtils.mapEntityToDTO(clientProject, ProjectDTO.class))
                .thenReturn(new ResponseEntity<ResponseDTO>(new ResponseDTO(
                        VarList.RSP_SUCCESS,
                        CommonMessages.RETRIEVED_SUCCESSFULLY,
                        clientProjectDTO
                ), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = clientProjectService.getClientProjectById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.RETRIEVED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testGetClientProjectById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(clientProjectRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = clientProjectService.getClientProjectById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

    @Test
    public void testDeleteClientProjectById_ExistingId() {
        // Arrange
        long id = 1L;

        when(clientProjectRepository.existsById(id)).thenReturn(true);
        when(responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null), HttpStatus.OK));

        // Act
        ResponseEntity<ResponseDTO> response = clientProjectService.deleteClientProjectById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(VarList.RSP_SUCCESS, response.getBody().getCode());
        assertEquals(CommonMessages.DELETED_SUCCESSFULLY, response.getBody().getMessage());
    }

    @Test
    public void testDeleteClientProjectById_NonExistingId() {
        // Arrange
        long id = 2L;

        when(clientProjectRepository.existsById(id)).thenReturn(false);
        when(responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED
        )).thenReturn(new ResponseEntity<>(new ResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null), HttpStatus.ACCEPTED));

        // Act
        ResponseEntity<ResponseDTO> response = clientProjectService.deleteClientProjectById(String.valueOf(id));

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals(VarList.RSP_NO_DATA_FOUND, response.getBody().getCode());
        assertEquals(CommonMessages.NO_DATA, response.getBody().getMessage());
    }

}
