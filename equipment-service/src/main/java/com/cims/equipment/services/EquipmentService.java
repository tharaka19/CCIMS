package com.cims.equipment.services;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.constants.enums.Status;
import com.cims.equipment.constants.validationMessages.CommonValidationMessages;
import com.cims.equipment.constants.validationMessages.EquipmentValidationMessages;
import com.cims.equipment.dtos.EquipmentDTO;
import com.cims.equipment.dtos.UserAccountResponseDTO;
import com.cims.equipment.entities.Equipment;
import com.cims.equipment.proxyClients.UserServiceClient;
import com.cims.equipment.repositories.EquipmentRepository;
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
 * Service class that handles operations related to equipment.
 */
@Service
@Transactional
@Slf4j
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a equipment in the database based on the provided equipment DTO.
     *
     * @param equipmentDTO the DTO representing the equipment to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEquipment(String token, EquipmentDTO equipmentDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEquipment(equipmentDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            equipmentDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEquipmentToDatabase(mapperUtils.mapDTOToEntity(equipmentDTO, Equipment.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, equipmentDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> saveUpdateEquipment()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all equipments from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentDTOs representing all equipments from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEquipments(String token) {
        try {
            List<Equipment> equipments = getEquipmentsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipments, EquipmentDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getAllEquipments()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active equipments from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentDTOs representing all active equipments from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEquipments(String token) {
        try {
            List<Equipment> equipments = getActiveEquipmentsFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipments, EquipmentDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getAllActiveEquipments()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active equipments from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentDTOs representing all active equipments from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEquipmentsByEquipmentType(String token, String equipmentTypeId) {
        try {
            List<Equipment> equipments = getActiveEquipmentsFromDatabaseByEquipmentType(token, equipmentTypeId);
            return mapperUtils.mapEntitiesToDTOs(equipments, EquipmentDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getAllActiveEquipments()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a equipment from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the equipment to be retrieved
     * @return a ResponseEntity containing a EquipmentDTO representing the equipment with the provided ID, or an error response if the equipment does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEquipmentById(String id) {
        try {
            if (isEquipmentExistById(id)) {
                Equipment equipments = getEquipmentFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(equipments, EquipmentDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getEquipmentById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a equipment from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the equipment to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEquipmentById(String id) {
        try {
            if (isEquipmentExistById(id)) {
                deleteEquipmentFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> deleteEquipmentById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a equipment entity to the database.
     *
     * @param equipment the equipment entity to be saved
     */
    public void saveEquipmentToDatabase(Equipment equipment) {
        try {
            equipmentRepository.save(equipment);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> saveEquipmentToDatabase()" + e);
        }
    }

    /**
     * Retrieves all equipments from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Equipment entities contain all equipments from the database that belong to the same branch
     */
    public List<Equipment> getEquipmentsFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return equipmentRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getEquipmentsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active equipments from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Equipment entities contain all active equipments from the database that belong to the same branch
     */
    public List<Equipment> getActiveEquipmentsFromDatabase(String token) {
        try {
            Predicate<Equipment> filterOnStatus = e -> e.getStatus().equals(Status.ACTIVE);
            return getEquipmentsFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getActiveEquipmentsFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active equipments by equipment type from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Equipment entities contain all active equipments by equipment type from the database that belong to the same branch
     */
    public List<Equipment> getActiveEquipmentsFromDatabaseByEquipmentType(String token, String equipmentTypeId) {
        try {
            Predicate<Equipment> filterOnEquipmentType = e -> e.getEquipmentType().getId() == Long.parseLong(equipmentTypeId);
            return getActiveEquipmentsFromDatabase(token).stream().filter(filterOnEquipmentType).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentService -> getActiveEquipmentsFromDatabaseByEquipmentType()" + e);
            return null;
        }
    }

    /**
     * Retrieves a equipment entity from the database based on its ID.
     *
     * @param id the ID of the equipment entity to retrieve
     * @return a Equipment entity representing the requested equipment, or null if the ID is not valid or an error occurs
     */
    public Equipment getEquipmentFromDatabaseById(String id) {
        try {
            return equipmentRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentService -> getEquipmentFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Equipment object from the database that matches the given equipment and branch code.
     *
     * @param equipmentNumber a String representing the equipment number to search for
     * @param branchCode      a String representing the branch code to search for
     * @return a Equipment object if one is found, or null if none is found or if an exception occurs during the search
     */
    public Equipment getEquipmentFromDatabaseByEquipmentNumberAndBranchCode(String equipmentNumber, String branchCode) {
        try {
            return equipmentRepository.findByEquipmentNumberAndBranchCode(equipmentNumber, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentService -> getEquipmentFromDatabaseByEquipmentNumberAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a equipment from the database by its ID.
     *
     * @param id the ID of the equipment to be deleted
     */
    public void deleteEquipmentFromDatabase(String id) {
        try {
            equipmentRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentService -> deleteEquipmentFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a equipment exists in the database by its ID.
     *
     * @param id the ID of the equipment to check for existence
     * @return true if a equipment with the specified ID exists in the database, false otherwise
     */
    public boolean isEquipmentExistById(String id) {
        try {
            return equipmentRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentService -> isEquipmentExist()" + e);
            return false;
        }
    }

    /**
     * Validates the equipment data transfer object by checking if the required fields
     * are not empty and if the equipment already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param equipmentDTO the equipment data transfer object to be validated
     * @param branchCode   the branch code for which to validate the equipment
     * @return a list of validation error messages, or an empty list if the equipment is valid
     */
    private List<String> validateEquipment(EquipmentDTO equipmentDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if equipment fields
        validations.addAll(validateEquipmentFields(equipmentDTO));
        // Check if equipment exists
        validations.addAll(validateEquipmentExistence(equipmentDTO, branchCode));
        return validations;
    }

    /**
     * Validates the equipment data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param equipmentDTO the equipment data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEquipmentFields(EquipmentDTO equipmentDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(equipmentDTO.getEquipmentTypeId())) {
            validations.add(EquipmentValidationMessages.EMPTY_EQUIPMENT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentDTO.getEquipmentNumber())) {
            validations.add(EquipmentValidationMessages.EMPTY_EQUIPMENT_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentDTO.getEquipmentName())) {
            validations.add(EquipmentValidationMessages.EMPTY_EQUIPMENT_NAME);
        } else if (ValidatorUtils.statusValidator.test(equipmentDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        }
        return validations;
    }

    /**
     * Validates the equipment data transfer object by checking if the equipment already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param equipmentDTO the equipment data transfer object to be validated
     * @param branchCode   the branch code of the equipment to check for existence
     * @return a list of validation error messages, or an empty list if the equipment is new or valid
     */
    private List<String> validateEquipmentExistence(EquipmentDTO equipmentDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        Equipment equipment = getEquipmentFromDatabaseByEquipmentNumberAndBranchCode(equipmentDTO.getEquipmentNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(equipment)) {
            if (!ValidatorUtils.stringNullValidator.test(equipmentDTO.getId())) { // Edit equipment
                if (!equipmentDTO.getId().equalsIgnoreCase(equipment.getId().toString())) {
                    validations.add(EquipmentValidationMessages.EXIT_EQUIPMENT_NUMBER);
                }
            } else { // New equipment
                validations.add(EquipmentValidationMessages.EXIT_EQUIPMENT_NUMBER);
            }
        }
        return validations;
    }
}
