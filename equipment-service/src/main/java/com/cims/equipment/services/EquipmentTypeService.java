package com.cims.equipment.services;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.constants.enums.Status;
import com.cims.equipment.constants.validationMessages.CommonValidationMessages;
import com.cims.equipment.constants.validationMessages.EquipmentTypeValidationMessages;
import com.cims.equipment.dtos.EquipmentTypeDTO;
import com.cims.equipment.dtos.UserAccountResponseDTO;
import com.cims.equipment.entities.EquipmentType;
import com.cims.equipment.proxyClients.UserServiceClient;
import com.cims.equipment.repositories.EquipmentTypeRepository;
import com.cims.equipment.utils.MapperUtils;
import com.cims.equipment.utils.ResponseDTO;
import com.cims.equipment.utils.ResponseUtils;
import com.cims.equipment.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to equipment type.
 */
@Service
@Transactional
@Slf4j
public class EquipmentTypeService {

    @Autowired
    private EquipmentTypeRepository equipmentTypeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a equipment Type in the database based on the provided equipment Type DTO.
     *
     * @param equipmentTypeDTO the DTO representing the equipment Type to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEquipmentType(String token, EquipmentTypeDTO equipmentTypeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEquipmentType(equipmentTypeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            equipmentTypeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEquipmentTypeToDatabase(mapperUtils.mapDTOToEntity(equipmentTypeDTO, EquipmentType.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, equipmentTypeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> saveUpdateEquipmentType()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all equipment Types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentTypeDTOs representing all equipment Types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEquipmentTypes(String token) {
        try {
            List<EquipmentType> equipmentTypes = getEquipmentTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentTypes, EquipmentTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getAllEquipmentTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active equipment Types from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentTypeDTOs representing all active equipment Types from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEquipmentTypes(String token) {
        try {
            List<EquipmentType> equipmentTypes = getActiveEquipmentTypesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentTypes, EquipmentTypeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getAllActiveEquipmentTypes()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a equipment Type from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the equipment Type to be retrieved
     * @return a ResponseEntity containing a EquipmentTypeDTO representing the equipment Type with the provided ID, or an error response if the equipment Type does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEquipmentTypeById(String id) {
        try {
            if (isEquipmentTypeExistById(id)) {
                EquipmentType equipmentTypes = getEquipmentTypeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(equipmentTypes, EquipmentTypeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getEquipmentTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a equipment Type from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the equipment Type to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEquipmentTypeById(String id) {
        try {
            if (isEquipmentTypeExistById(id)) {
                deleteEquipmentTypeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> deleteEquipmentTypeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a equipment Type entity to the database.
     *
     * @param equipmentType the equipment Type entity to be saved
     */
    public void saveEquipmentTypeToDatabase(EquipmentType equipmentType) {
        try {
            equipmentTypeRepository.save(equipmentType);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> saveEquipmentTypeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all equipment Types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentType entities contain all equipment Types from the database that belong to the same branch
     */
    public List<EquipmentType> getEquipmentTypesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return equipmentTypeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getEquipmentTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active equipment Types from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentType entities contain all active equipment Types from the database that belong to the same branch
     */
    public List<EquipmentType> getActiveEquipmentTypesFromDatabase(String token) {
        try {
            Predicate<EquipmentType> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEquipmentTypesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getActiveEquipmentTypesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a equipment Type entity from the database based on its ID.
     *
     * @param id the ID of the equipment Type entity to retrieve
     * @return a EquipmentType entity representing the requested equipment Type, or null if the ID is not valid or an error occurs
     */
    public EquipmentType getEquipmentTypeFromDatabaseById(String id) {
        try {
            return equipmentTypeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getEquipmentTypeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EquipmentType object from the database that matches the given equipment Type and branch code.
     *
     * @param equipmentType a String representing the equipment Type to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EquipmentType object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EquipmentType getEquipmentTypeFromDatabaseByEquipmentTypeAndBranchCode(String equipmentType, String branchCode) {
        try {
            return equipmentTypeRepository.findByEquipmentTypeAndBranchCode(equipmentType, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentTypeService -> getEquipmentTypeFromDatabaseByEquipmentTypeAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a equipment Type from the database by its ID.
     *
     * @param id the ID of the equipment Type to be deleted
     */
    public void deleteEquipmentTypeFromDatabase(String id) {
        try {
            equipmentTypeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentTypeService -> deleteEquipmentTypeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a equipment Type exists in the database by its ID.
     *
     * @param id the ID of the equipment Type to check for existence
     * @return true if a equipment Type with the specified ID exists in the database, false otherwise
     */
    public boolean isEquipmentTypeExistById(String id) {
        try {
            return equipmentTypeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentTypeService -> isEquipmentTypeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the equipment Type data transfer object by checking if the required fields
     * are not empty and if the equipment Type already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param equipmentTypeDTO the equipment Type data transfer object to be validated
     * @param branchCode      the branch code for which to validate the equipment Type
     * @return a list of validation error messages, or an empty list if the equipment Type is valid
     */
    private List<String> validateEquipmentType(EquipmentTypeDTO equipmentTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if equipmentType fields
        validations.addAll(validateEquipmentTypeFields(equipmentTypeDTO));
        // Check if equipmentType exists
        validations.addAll(validateEquipmentTypeExistence(equipmentTypeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the equipment Type data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param equipmentTypeDTO the equipment Type data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEquipmentTypeFields(EquipmentTypeDTO equipmentTypeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(equipmentTypeDTO.getEquipmentType())) {
            validations.add(EquipmentTypeValidationMessages.EMPTY_EQUIPMENT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentTypeDTO.getDescription())) {
            validations.add(EquipmentTypeValidationMessages.EMPTY_DESCRIPTION);
        } else if (ValidatorUtils.statusValidator.test(equipmentTypeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the equipment Type data transfer object by checking if the equipment Type already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param equipmentTypeDTO the equipment Type data transfer object to be validated
     * @param branchCode      the branch code of the equipment Type to check for existence
     * @return a list of validation error messages, or an empty list if the equipment Type is new or valid
     */
    private List<String> validateEquipmentTypeExistence(EquipmentTypeDTO equipmentTypeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        EquipmentType equipmentType = getEquipmentTypeFromDatabaseByEquipmentTypeAndBranchCode(equipmentTypeDTO.getEquipmentType(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(equipmentType)) {
            if (!ValidatorUtils.stringNullValidator.test(equipmentTypeDTO.getId())) { // Edit equipment Type
                if (!equipmentTypeDTO.getId().equalsIgnoreCase(equipmentType.getId().toString())) {
                    validations.add(EquipmentTypeValidationMessages.EXIT_EQUIPMENT_TYPE);
                }
            } else { // New equipment Type
                validations.add(EquipmentTypeValidationMessages.EXIT_EQUIPMENT_TYPE);
            }
        }
        return validations;
    }
}
