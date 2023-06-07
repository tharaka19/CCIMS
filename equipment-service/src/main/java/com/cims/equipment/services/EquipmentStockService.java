package com.cims.equipment.services;

import com.cims.equipment.constants.CommonMessages;
import com.cims.equipment.constants.VarList;
import com.cims.equipment.constants.enums.Status;
import com.cims.equipment.constants.validationMessages.CommonValidationMessages;
import com.cims.equipment.constants.validationMessages.EquipmentStockValidationMessages;
import com.cims.equipment.dtos.EquipmentStockDTO;
import com.cims.equipment.dtos.UserAccountResponseDTO;
import com.cims.equipment.entities.Equipment;
import com.cims.equipment.entities.EquipmentStock;
import com.cims.equipment.proxyClients.UserServiceClient;
import com.cims.equipment.repositories.EquipmentStockRepository;
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
 * Service class that handles operations related to equipment stock.
 */
@Service
@Transactional
@Slf4j
public class EquipmentStockService {

    @Autowired
    private EquipmentStockRepository equipmentStockRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a equipment stock in the database based on the provided equipment stock DTO.
     *
     * @param equipmentStockDTO the DTO representing the equipment stock to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEquipmentStock(String token, EquipmentStockDTO equipmentStockDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEquipmentStock(equipmentStockDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            equipmentStockDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            equipmentStockDTO.setAvailableQuantity(0);
            saveEquipmentStockToDatabase(mapperUtils.mapDTOToEntity(equipmentStockDTO, EquipmentStock.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, equipmentStockDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> saveUpdateEquipmentStock()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the available quantity of an equipment stock in the database.
     *
     * @param equipmentStockId the ID of the equipment stock to update
     * @param availableQuantity the new available quantity to set
     * @return a ResponseEntity containing a ResponseDTO with a success or error message and status code
     */
    public ResponseEntity<ResponseDTO> updateEquipmentQuantity(String equipmentStockId, Integer availableQuantity) {
        try {
            EquipmentStock equipmentStock = getEquipmentStockFromDatabaseById(equipmentStockId);
            equipmentStock.setAvailableQuantity(availableQuantity);
            saveEquipmentStockToDatabase(equipmentStock);
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, null, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> updateEquipmentQuantity()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all equipment stocks from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentStockDTOs representing all equipment stocks from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEquipmentStocks(String token) {
        try {
            List<EquipmentStock> equipmentStocks = getEquipmentStocksFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentStocks, EquipmentStockDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getAllEquipmentStocks()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active equipment stocks from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EquipmentStockDTOs representing all active equipment stocks from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEquipmentStocks(String token) {
        try {
            List<EquipmentStock> equipmentStocks = getActiveEquipmentStocksFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(equipmentStocks, EquipmentStockDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getAllActiveEquipmentStocks()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a equipment stock from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the equipment stock to be retrieved
     * @return a ResponseEntity containing a EquipmentStockDTO representing the equipment stock with the provided ID, or an error response if the equipment stock does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEquipmentStockById(String id) {
        try {
            if (isEquipmentStockExistById(id)) {
                EquipmentStock equipmentStocks = getEquipmentStockFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(equipmentStocks, EquipmentStockDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStockById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a equipment stock from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param equipmentId the Equipment ID of the equipment stock to be retrieved
     * @return a ResponseEntity containing a EquipmentStockDTO representing the equipment stock with the provided Equipment ID, or an error response if the equipment stock does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEquipmentStockByEquipmentId(String equipmentId) {
        try {
            Equipment equipment = equipmentService.getEquipmentFromDatabaseById(equipmentId);
            if (isEquipmentStockExistByEquipment(equipment)) {
                EquipmentStock equipmentStocks = getEquipmentStockFromDatabaseByEquipment(equipment);
                return mapperUtils.mapEntityToDTO(equipmentStocks, EquipmentStockDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStockByEquipmentId()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a equipment stock from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the equipment stock to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEquipmentStockById(String id) {
        try {
            if (isEquipmentStockExistById(id)) {
                deleteEquipmentStockFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> deleteEquipmentStockById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a equipment stock entity to the database.
     *
     * @param equipmentStock the equipment stock entity to be saved
     */
    public void saveEquipmentStockToDatabase(EquipmentStock equipmentStock) {
        try {
            equipmentStockRepository.save(equipmentStock);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> saveEquipmentStockToDatabase()" + e);
        }
    }

    /**
     * Retrieves all equipment stocks from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentStock entities contain all equipment stocks from the database that belong to the same branch
     */
    public List<EquipmentStock> getEquipmentStocksFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return equipmentStockRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStocksFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active equipment stocks from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EquipmentStock entities contain all active equipment stocks from the database that belong to the same branch
     */
    public List<EquipmentStock> getActiveEquipmentStocksFromDatabase(String token) {
        try {
            Predicate<EquipmentStock> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEquipmentStocksFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EquipmentStockService -> getActiveEquipmentStocksFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a equipment stock entity from the database based on its ID.
     *
     * @param id the ID of the equipment stock entity to retrieve
     * @return a EquipmentStock entity representing the requested equipment stock, or null if the ID is not valid or an error occurs
     */
    public EquipmentStock getEquipmentStockFromDatabaseById(String id) {
        try {
            return equipmentStockRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStockFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a equipment stock entity from the database based on its Equipment ID.
     *
     * @param equipment the Equipment of the equipment stock entity to retrieve
     * @return a EquipmentStock entity representing the requested equipment stock, or null if the Equipment is not valid or an error occurs
     */
    public EquipmentStock getEquipmentStockFromDatabaseByEquipment(Equipment equipment) {
        try {
            return equipmentStockRepository.findByEquipment(equipment);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStockFromDatabaseByEquipment()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EquipmentStock object from the database that matches the given equipment stock and branch code.
     *
     * @param equipmentStock a String representing the equipment stock to search for
     * @param branchCode   a String representing the branch code to search for
     * @return a EquipmentStock object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EquipmentStock getEquipmentStockFromDatabaseByStockNumberAndBranchCode(String equipmentStock, String branchCode) {
        try {
            return equipmentStockRepository.findByStockNumberAndBranchCode(equipmentStock, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> getEquipmentStockFromDatabaseByStockNumberAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a equipment stock from the database by its ID.
     *
     * @param id the ID of the equipment stock to be deleted
     */
    public void deleteEquipmentStockFromDatabase(String id) {
        try {
            equipmentStockRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> deleteEquipmentStockFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a equipment stock exists in the database by its ID.
     *
     * @param id the ID of the equipment stock to check for existence
     * @return true if a equipment stock with the specified ID exists in the database, false otherwise
     */
    public boolean isEquipmentStockExistById(String id) {
        try {
            return equipmentStockRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> isEquipmentStockExist()" + e);
            return false;
        }
    }

    /**
     * Checks whether a equipment stock exists in the database by its Equipment ID.
     *
     * @param equipment the Equipment of the equipment stock to check for existence
     * @return true if a equipment stock with the specified Equipment Equipment exists in the database, false otherwise
     */
    public boolean isEquipmentStockExistByEquipment(Equipment equipment) {
        try {
            return equipmentStockRepository.existsByEquipment(equipment);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentStockService -> isEquipmentStockExistByEquipment()" + e);
            return false;
        }
    }


    /**
     * Validates the equipment stock data transfer object by checking if the required fields
     * are not empty and if the equipment stock already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param equipmentStockDTO the equipment stock data transfer object to be validated
     * @param branchCode      the branch code for which to validate the equipment stock
     * @return a list of validation error messages, or an empty list if the equipment stock is valid
     */
    private List<String> validateEquipmentStock(EquipmentStockDTO equipmentStockDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if equipmentStock fields
        validations.addAll(validateEquipmentStockFields(equipmentStockDTO));
        // Check if equipmentStock exists
        validations.addAll(validateEquipmentStockExistence(equipmentStockDTO, branchCode));
        return validations;
    }

    /**
     * Validates the equipment stock data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param equipmentStockDTO the equipment stock data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEquipmentStockFields(EquipmentStockDTO equipmentStockDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(equipmentStockDTO.getEquipmentTypeId())) {
            validations.add(EquipmentStockValidationMessages.EMPTY_EQUIPMENT_TYPE);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentStockDTO.getEquipmentId())) {
            validations.add(EquipmentStockValidationMessages.EMPTY_EQUIPMENT);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentStockDTO.getEquipmentSupplierId())) {
            validations.add(EquipmentStockValidationMessages.EMPTY_EQUIPMENT_SUPPLIER);
        } else if (ValidatorUtils.stringNullValidator.test(equipmentStockDTO.getStockNumber())) {
            validations.add(EquipmentStockValidationMessages.EMPTY_STOCK_NUMBER);
        } else if (ValidatorUtils.priceNullValidator.test(equipmentStockDTO.getPurchasePrice())) {
            validations.add(EquipmentStockValidationMessages.EMPTY_PURCHASE_PRISE);
        } else if (ValidatorUtils.statusValidator.test(equipmentStockDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (!ValidatorUtils.priceValidator.test(equipmentStockDTO.getPurchasePrice())) {
            validations.add(EquipmentStockValidationMessages.INVALID_PURCHASE_PRISE);
        }
        return validations;
    }

    /**
     * Validates the equipment stock data transfer object by checking if the equipment stock already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param equipmentStockDTO the equipment stock data transfer object to be validated
     * @param branchCode      the branch code of the equipment stock to check for existence
     * @return a list of validation error messages, or an empty list if the equipment stock is new or valid
     */
    private List<String> validateEquipmentStockExistence(EquipmentStockDTO equipmentStockDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        EquipmentStock equipmentStock = getEquipmentStockFromDatabaseByStockNumberAndBranchCode(equipmentStockDTO.getStockNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(equipmentStock)) {
            if (!ValidatorUtils.stringNullValidator.test(equipmentStockDTO.getId())) { // Edit equipment stock
                if (!equipmentStockDTO.getId().equalsIgnoreCase(equipmentStock.getId().toString())) {
                    validations.add(EquipmentStockValidationMessages.EXIT_STOCK_NUMBER);
                }
            } else { // New equipment stock
                validations.add(EquipmentStockValidationMessages.EXIT_STOCK_NUMBER);
            }
        }
        return validations;
    }
}
