package com.cims.project.services;

import com.cims.project.constants.CommonMessages;
import com.cims.project.constants.VarList;
import com.cims.project.constants.validationMessages.ClientProjectEquipmentStockValidationMessages;
import com.cims.project.dtos.ClientProjectEquipmentStockDTO;
import com.cims.project.dtos.UserAccountResponseDTO;
import com.cims.project.entities.ClientProjectEquipmentStock;
import com.cims.project.proxyClients.EquipmentServiceClient;
import com.cims.project.proxyClients.UserServiceClient;
import com.cims.project.repositories.ClientProjectEquipmentStockRepository;
import com.cims.project.utils.MapperUtils;
import com.cims.project.utils.ResponseDTO;
import com.cims.project.utils.ResponseUtils;
import com.cims.project.utils.ValidatorUtils;
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
 * Service class that handles operations related to client project equipment stock.
 */
@Service
@Transactional
@Slf4j
public class ClientProjectEquipmentStockService {

    @Autowired
    private ClientProjectEquipmentStockRepository clientProjectEquipmentStockRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private EquipmentServiceClient equipmentServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a client project equipment stock in the database based on the provided client project equipment stock DTO.
     *
     * @param clientProjectEquipmentStockDTO the DTO representing the client project equipment stock to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateClientProjectEquipmentStock(String token, ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateClientProjectEquipmentStock(clientProjectEquipmentStockDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            clientProjectEquipmentStockDTO.setDate(LocalDate.now());
            clientProjectEquipmentStockDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveClientProjectEquipmentStockToDatabase(mapperUtils.mapDTOToEntity(clientProjectEquipmentStockDTO, ClientProjectEquipmentStock.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, clientProjectEquipmentStockDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectEquipmentStockService -> saveUpdateClientProjectEquipmentStock()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all client project equipment stock by equipment stock from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of ClientProjectEquipmentStockDTOs representing all client project equipment stock by equipment stock from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllClientProjectEquipmentStockByClientProject(String token, String equipmentId) {
        try {
            List<ClientProjectEquipmentStock> equipmentStockHistories = getClientProjectEquipmentStockFromDatabaseByClientProject(token, equipmentId);
            return mapperUtils.mapEntitiesToDTOs(equipmentStockHistories, ClientProjectEquipmentStockDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectEquipmentStockService -> getAllClientProjectEquipmentStockByClientProject()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a client project equipment stock entity to the database.
     *
     * @param clientProjectEquipmentStock the client project equipment stock entity to be saved
     */
    public void saveClientProjectEquipmentStockToDatabase(ClientProjectEquipmentStock clientProjectEquipmentStock) {
        try {
            clientProjectEquipmentStockRepository.save(clientProjectEquipmentStock);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectEquipmentStockService -> saveClientProjectEquipmentStockToDatabase()" + e);
        }
    }

    /**
     * Retrieves all client project equipment stock from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of ClientProjectEquipmentStock entities contain all equipment stocks history from the database that belong to the same branch
     */
    public List<ClientProjectEquipmentStock> getClientProjectEquipmentStockFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return clientProjectEquipmentStockRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectEquipmentStockService -> getClientProjectEquipmentStockFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all client project equipment stock from the database that belong to the same branch as the user with the provided token.
     *
     * @param clientProjectId the ID of the client project for which to retrieve the equipment stock records.
     * @param token            The user token used to authorize the operation.
     * @return a List of ClientProjectEquipmentStock entities contain all client project equipment stock from the database that belong to the same branch
     */
    public List<ClientProjectEquipmentStock> getClientProjectEquipmentStockFromDatabaseByClientProject(String token, String clientProjectId) {
        try {
            Predicate<ClientProjectEquipmentStock> filterOnEquipmentStock = es -> es.getClientProject().getId() == Long.parseLong(clientProjectId);
            return getClientProjectEquipmentStockFromDatabase(token).stream().filter(filterOnEquipmentStock).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in ClientProjectEquipmentStockService -> getClientProjectEquipmentStockFromDatabaseByClientProject()" + e);
            return null;
        }
    }

    /**
     * Validates the client project equipment stock data transfer object by checking if the required fields
     * are not empty and if the client project equipment stock already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param clientProjectEquipmentStockDTO the client project equipment stock data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the client project equipment stock is valid
     */
    private List<String> validateClientProjectEquipmentStock(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        List<String> validations = new ArrayList<>();
        // Check if equipmentStock fields
        validations.addAll(validateClientProjectEquipmentStockFields(clientProjectEquipmentStockDTO));
        return validations;
    }

    /**
     * Validates the client project equipment stock data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param clientProjectEquipmentStockDTO the client project equipment stock data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateClientProjectEquipmentStockFields(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.statusValidator.test(clientProjectEquipmentStockDTO.getEquipmentId())) {
            validations.add(ClientProjectEquipmentStockValidationMessages.EMPTY_EQUIPMENT);
        } else if (ValidatorUtils.statusValidator.test(clientProjectEquipmentStockDTO.getOperation().toString())) {
            validations.add(ClientProjectEquipmentStockValidationMessages.EMPTY_OPERATION);
        } else if (ValidatorUtils.numericNullValidator.test(clientProjectEquipmentStockDTO.getEquipmentQuantity())) {
            validations.add(ClientProjectEquipmentStockValidationMessages.EMPTY_EQUIPMENT_QUANTITY);
        } else if (clientProjectEquipmentStockDTO.getEquipmentQuantity() <= 0) {
            validations.add(ClientProjectEquipmentStockValidationMessages.INVALID_EQUIPMENT_QUANTITY);
        } else if (CollectionUtils.isEmpty(validations)) {
            validations.addAll(calculateEquipmentHistory(clientProjectEquipmentStockDTO));
        }
        return validations;
    }

    /**
     * Calculates and updates the available quantity of an equipment stock based on a history DTO.
     *
     * @param clientProjectEquipmentStockDTO the DTO containing the client project equipment stock to calculate
     * @return a list of validation messages if any validation errors occur, otherwise an empty list
     */
    private List<String> calculateEquipmentHistory(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        List<String> validations = validateOperation(clientProjectEquipmentStockDTO);
        if (!validations.isEmpty()) {
            return validations;
        }
        Integer availableQuantity = calculateAvailableQuantity(clientProjectEquipmentStockDTO);
        updateEquipmentQuantity(clientProjectEquipmentStockDTO, availableQuantity);
        return validations;
    }

    /**
     * Validates the operation in an client project equipment stock DTO and returns any validation errors as a list of strings.
     *
     * @param clientProjectEquipmentStockDTO the DTO containing the client project equipment stock to validate
     * @return a list of validation messages if any validation errors occur, otherwise an empty list
     */
    private List<String> validateOperation(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        List<String> validations = new ArrayList<>();
        switch (clientProjectEquipmentStockDTO.getOperation()) {
            case ADD:
                if (clientProjectEquipmentStockDTO.getAvailableQuantity() < clientProjectEquipmentStockDTO.getEquipmentQuantity()) {
                    validations.add(ClientProjectEquipmentStockValidationMessages.INVALID_ADD_EQUIPMENT_QUANTITY);
                }
                break;
            case REMOVE:
                if (clientProjectEquipmentStockDTO.getAvailableQuantity() < clientProjectEquipmentStockDTO.getEquipmentQuantity()) {
                    validations.add(ClientProjectEquipmentStockValidationMessages.INVALID_REMOVE_EQUIPMENT_QUANTITY);
                }
                break;
            default:
                validations.add(ClientProjectEquipmentStockValidationMessages.EMPTY_OPERATION);
        }
        return validations;
    }

    /**
     * Calculates the available quantity of an equipment based on its stock history.
     * The available quantity is updated based on the operation performed on the equipment
     * as indicated by the ClientProjectEquipmentStockDTO object.
     *
     * @param clientProjectEquipmentStockDTO the client project equipment stock containing information
     */
    private Integer calculateAvailableQuantity(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO) {
        Integer availableQuantity = clientProjectEquipmentStockDTO.getAvailableQuantity();
        Integer equipmentQuantity = clientProjectEquipmentStockDTO.getEquipmentQuantity();
        switch (clientProjectEquipmentStockDTO.getOperation()) {
            case ADD:
                availableQuantity -= equipmentQuantity;
                break;
            case DEFECT:
            case REMOVE:
                availableQuantity += equipmentQuantity;
                break;
        }
        return availableQuantity;
    }

    /**
     * Updates the quantity of equipment in the equipment stock table based on the
     * available quantity of the equipment.
     *
     * @param clientProjectEquipmentStockDTO the client project equipment stock containing information
     */
    private void updateEquipmentQuantity(ClientProjectEquipmentStockDTO clientProjectEquipmentStockDTO, Integer availableQuantity) {
        equipmentServiceClient.updateEquipmentQuantity(clientProjectEquipmentStockDTO.getEquipmentStockId(), availableQuantity);
    }
}
