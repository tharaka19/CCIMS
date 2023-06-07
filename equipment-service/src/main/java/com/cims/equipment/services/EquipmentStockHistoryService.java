package com.cims.equipment.services;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.constants.validationMessages.EquipmentStockHistoryValidationMessages;
import com.cims.equipment.dtos.EquipmentStockHistoryDTO;
import com.cims.equipment.dtos.UserAccountResponseDTO;
import com.cims.equipment.entities.EquipmentStockHistory;
import com.cims.equipment.proxyClients.UserServiceClient;
import com.cims.equipment.repositories.EquipmentStockHistoryRepository;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class that handles operations related to equipment stock history.
 */
@Service
@Transactional
@Slf4j
public class EquipmentStockHistoryService {

    @Autowired
    private EquipmentStockHistoryRepository equipmentStockHistoryRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private EquipmentStockService equipmentStockService;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a equipment stock history in the database based on the provided equipment stock history DTO.
     *
     * @param equipmentStockHistoryDTO the DTO representing the equipment stock history to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEquipmentStockHistory(String token, EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEquipmentStockHistory(equipmentStockHistoryDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            equipmentStockHistoryDTO.setDate(LocalDate.now());
            equipmentStockHistoryDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEquipmentStockHistoryToDatabase(mapperUtils.mapDTOToEntity(equipmentStockHistoryDTO, EquipmentStockHistory.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, equipmentStockHistoryDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockHistoryService -> saveUpdateEquipmentStockHistory()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all equipment stock history by equipment stock from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentStockHistoryDTOs representing all equipment stock history by equipment stock from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEquipmentStockHistoryByEquipmentHistory(String token, String equipmentStockId) {
        try {
            List<EquipmentStockHistory> equipmentStockHistories = getEquipmentStockHistoryFromDatabaseByEquipmentStock(token, equipmentStockId);
            return mapperUtils.mapEntitiesToDTOs(equipmentStockHistories, EquipmentStockHistoryDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockHistoryService -> getAllEquipmentStockHistoryByEquipmentHistory()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a equipment stock history entity to the database.
     *
     * @param equipmentStockHistory the equipment stock history entity to be saved
     */
    public void saveEquipmentStockHistoryToDatabase(EquipmentStockHistory equipmentStockHistory) {
        try {
            equipmentStockHistoryRepository.save(equipmentStockHistory);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockHistoryService -> saveEquipmentStockHistoryToDatabase()" + e);
        }
    }

    /**
     * Retrieves all equipment stock history from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentStockHistory entities contain all equipment stocks history from the database that belong to the same branch
     */
    public List<EquipmentStockHistory> getEquipmentStockHistoryFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return equipmentStockHistoryRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockHistoryService -> getEquipmentStockHistoryFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all equipment stock history from the database that belong to the same branch as the user with the provided token.
     *
     * @param equipmentStockId the ID of the equipment stock for which to retrieve the history records.
     * @param token            The user token used to authorize the operation.
     * @return a List of EquipmentStockHistory entities contain all equipment stock history from the database that belong to the same branch
     */
    public List<EquipmentStockHistory> getEquipmentStockHistoryFromDatabaseByEquipmentStock(String token, String equipmentStockId) {
        try {
            Predicate<EquipmentStockHistory> filterOnEquipmentStock = fy -> fy.getEquipmentStock().getId() == Long.parseLong(equipmentStockId);
            return getEquipmentStockHistoryFromDatabase(token).stream().filter(filterOnEquipmentStock).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockHistoryService -> getEquipmentStockHistoryFromDatabaseByEquipmentStock()" + e);
            return null;
        }
    }

    /**
     * Validates the equipment stock history data transfer object by checking if the required fields
     * are not empty and if the equipment stock history already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param equipmentStockHistoryDTO the equipment stock history data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the equipment stock history is valid
     */
    private List<String> validateEquipmentStockHistory(EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        List<String> validations = new ArrayList<>();
        // Check if equipmentStock fields
        validations.addAll(validateEquipmentStockHistoryFields(equipmentStockHistoryDTO));
        return validations;
    }

    /**
     * Validates the equipment stock history data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param equipmentStockHistoryDTO the equipment stock history data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEquipmentStockHistoryFields(EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.statusValidator.test(equipmentStockHistoryDTO.getOperation().toString())) {
            validations.add(EquipmentStockHistoryValidationMessages.EMPTY_OPERATION);
        } else if (ValidatorUtils.numericNullValidator.test(equipmentStockHistoryDTO.getEquipmentQuantity())) {
            validations.add(EquipmentStockHistoryValidationMessages.EMPTY_EQUIPMENT_QUANTITY);
        } else if (equipmentStockHistoryDTO.getEquipmentQuantity() <= 0) {
            validations.add(EquipmentStockHistoryValidationMessages.INVALID_EQUIPMENT_QUANTITY);
        } else if (CollectionUtils.isEmpty(validations)) {
            validations.addAll(calculateEquipmentHistory(equipmentStockHistoryDTO));
        }
        return validations;
    }

    /**
     * Calculates and updates the available quantity of an equipment stock based on a history DTO.
     *
     * @param equipmentStockHistoryDTO the DTO containing the equipment stock history to calculate
     * @return a list of validation messages if any validation errors occur, otherwise an empty list
     */
    private List<String> calculateEquipmentHistory(EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        List<String> validations = validateOperation(equipmentStockHistoryDTO);
        if (!validations.isEmpty()) {
            return validations;
        }
        Integer availableQuantity = calculateAvailableQuantity(equipmentStockHistoryDTO);
        updateEquipmentQuantity(equipmentStockHistoryDTO, availableQuantity);
        return validations;
    }

    /**
     * Validates the operation in an equipment stock history DTO and returns any validation errors as a list of strings.
     *
     * @param equipmentStockHistoryDTO the DTO containing the equipment stock history to validate
     * @return a list of validation messages if any validation errors occur, otherwise an empty list
     */
    private List<String> validateOperation(EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        List<String> validations = new ArrayList<>();
        switch (equipmentStockHistoryDTO.getOperation()) {
            case ADD:
                break;
            case DEFECT:
                if (equipmentStockHistoryDTO.getAvailableQuantity() < equipmentStockHistoryDTO.getEquipmentQuantity()) {
                    validations.add(EquipmentStockHistoryValidationMessages.INVALID_DEFECT_EQUIPMENT_QUANTITY);
                }
                break;
            case REMOVE:
                if (equipmentStockHistoryDTO.getAvailableQuantity() < equipmentStockHistoryDTO.getEquipmentQuantity()) {
                    validations.add(EquipmentStockHistoryValidationMessages.INVALID_REMOVE_EQUIPMENT_QUANTITY);
                }
                break;
            default:
                validations.add(EquipmentStockHistoryValidationMessages.EMPTY_OPERATION);
        }
        return validations;
    }

    /**
     * Calculates the available quantity of an equipment based on its stock history.
     * The available quantity is updated based on the operation performed on the equipment
     * as indicated by the EquipmentStockHistoryDTO object.
     *
     * @param equipmentStockHistoryDTO the equipment stock history containing information
     */
    private Integer calculateAvailableQuantity(EquipmentStockHistoryDTO equipmentStockHistoryDTO) {
        Integer availableQuantity = equipmentStockHistoryDTO.getAvailableQuantity();
        Integer equipmentQuantity = equipmentStockHistoryDTO.getEquipmentQuantity();
        switch (equipmentStockHistoryDTO.getOperation()) {
            case ADD:
                availableQuantity += equipmentQuantity;
                break;
            case DEFECT:
            case REMOVE:
                availableQuantity -= equipmentQuantity;
                break;
        }
        return availableQuantity;
    }

    /**
     * Updates the quantity of equipment in the equipment stock table based on the
     * available quantity of the equipment.
     *
     * @param equipmentStockHistoryDTO the equipment stock history containing information
     */
    private void updateEquipmentQuantity(EquipmentStockHistoryDTO equipmentStockHistoryDTO, Integer availableQuantity) {
        equipmentStockService.updateEquipmentQuantity(equipmentStockHistoryDTO.getEquipmentStockId(), availableQuantity);
    }

}
