package com.cims.equipment.utils;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class for mapping entities to DTOs and vice versa.
 */
@Component
public class MapperUtils {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Maps a list of entities to a list of DTOs.
     *
     * @param entities the list of entities to be mapped
     * @param dtoClass the class of the DTO to be mapped to
     * @param <T>      the type of the entity
     * @param <U>      the type of the DTO
     * @return a ResponseEntity object containing the mapped list of DTOs and an HTTP status
     */
    public <T, U> ResponseEntity<ResponseDTO> mapEntitiesToDTOs(List<T> entities, Class<U> dtoClass) {
        if (entities != null) {
            List<U> dTOS = entities.stream()
                    .map(entity -> modelMapper.map(entity, dtoClass))
                    .collect(Collectors.toList());
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.RETRIEVED_SUCCESSFULLY, dTOS, HttpStatus.OK);
        }
        return responseUtils.createResponseDTO(VarList.RSP_FAIL, CommonMessages.RETRIEVAL_FAILED, null, HttpStatus.ACCEPTED);
    }

    /**
     * Maps an entity to a DTO.
     *
     * @param entity   the entity to be mapped
     * @param dtoClass the class of the DTO to be mapped to
     * @param <T>      the type of the entity
     * @param <U>      the type of the DTO
     * @return a ResponseEntity object containing the mapped DTO and an HTTP status
     */
    public <T, U> ResponseEntity<ResponseDTO> mapEntityToDTO(T entity, Class<U> dtoClass) {
        U dto = modelMapper.map(entity, dtoClass);
        return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.RETRIEVED_SUCCESSFULLY, dto, HttpStatus.OK);
    }

    /**
     * Maps a DTO to an entity.
     *
     * @param dto         the DTO to be mapped
     * @param entityClass the class of the entity to be mapped to
     * @param <T>         the type of the DTO
     * @param <U>         the type of the entity
     * @return the mapped entity
     */
    public <T, U> U mapDTOToEntity(T dto, Class<U> entityClass) {
        return modelMapper.map(dto, entityClass);
    }
}
