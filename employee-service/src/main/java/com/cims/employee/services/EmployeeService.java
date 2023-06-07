package com.cims.employee.services;

import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Status;
import com.cims.employee.constants.validationMessages.CommonValidationMessages;
import com.cims.employee.constants.validationMessages.EmployeeValidationMessages;
import com.cims.employee.dtos.EmployeeDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.Employee;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeRepository;
import com.cims.employee.utils.MapperUtils;
import com.cims.employee.utils.ResponseDTO;
import com.cims.employee.utils.ResponseUtils;
import com.cims.employee.utils.ValidatorUtils;
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
 * Service class that handles operations related to employee.
 */
@Service
@Transactional
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee in the database based on the provided employee DTO.
     *
     * @param employeeDTO the DTO representing the employee to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployee(String token, EmployeeDTO employeeDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployee(employeeDTO, userAccountResponseDTO.getBranchCode());
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeToDatabase(mapperUtils.mapDTOToEntity(employeeDTO, Employee.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> saveUpdateEmployee()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employees from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeDTOs representing all employees from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployees(String token) {
        try {
            List<Employee> employees = getEmployeesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employees, EmployeeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> getAllEmployees()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all active employees from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeDTOs representing all active employees from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllActiveEmployees(String token) {
        try {
            List<Employee> employees = getActiveEmployeesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employees, EmployeeDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> getAllActiveEmployees()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee to be retrieved
     * @return a ResponseEntity containing a EmployeeDTO representing the employee with the provided ID, or an error response if the employee does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeById(String id) {
        try {
            if (isEmployeeExistById(id)) {
                Employee employees = getEmployeeFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employees, EmployeeDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> getEmployeeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeById(String id) {
        try {
            if (isEmployeeExistById(id)) {
                deleteEmployeeFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> deleteEmployeeById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee entity to the database.
     *
     * @param employee the employee entity to be saved
     */
    public void saveEmployeeToDatabase(Employee employee) {
        try {
            employeeRepository.save(employee);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> saveEmployeeToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employees from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Employee entities contain all employees from the database that belong to the same branch
     */
    public List<Employee> getEmployeesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> getEmployeesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves all active employees from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of Employee entities contain all active employees from the database that belong to the same branch
     */
    public List<Employee> getActiveEmployeesFromDatabase(String token) {
        try {
            Predicate<Employee> filterOnStatus = fy -> fy.getStatus().equals(Status.ACTIVE);
            return getEmployeesFromDatabase(token).stream().filter(filterOnStatus).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeService -> getActiveEmployeesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee entity from the database based on its ID.
     *
     * @param id the ID of the employee entity to retrieve
     * @return a Employee entity representing the requested employee, or null if the ID is not valid or an error occurs
     */
    public Employee getEmployeeFromDatabaseById(String id) {
        try {
            return employeeRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeService -> getEmployeeFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Employee object from the database that matches the given employee nic.
     *
     * @param nic a String representing the employee nic to search for
     * @return a Employee object if one is found, or null if none is found or if an exception occurs during the search
     */
    public Employee getEmployeeFromDatabaseByNIC(String nic) {
        try {
            return employeeRepository.findByNic(nic);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeService -> getEmployeeFromDatabaseByNIC()" + e);
            return null;
        }
    }

    /**
     * Retrieves a Employee object from the database that matches the given equipment and branch code.
     *
     * @param employeeNumber a String representing the employee number to search for
     * @param branchCode     a String representing the branch code to search for
     * @return a Employee object if one is found, or null if none is found or if an exception occurs during the search
     */
    public Employee getEmployeeFromDatabaseByEmployeeNumberAndBranchCode(String employeeNumber, String branchCode) {
        try {
            return employeeRepository.findByEmployeeNumberAndBranchCode(employeeNumber, branchCode);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EquipmentService -> getEmployeeFromDatabaseByEmployeeNumberAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee from the database by its ID.
     *
     * @param id the ID of the employee to be deleted
     */
    public void deleteEmployeeFromDatabase(String id) {
        try {
            employeeRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeService -> deleteEmployeeFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee exists in the database by its ID.
     *
     * @param id the ID of the employee to check for existence
     * @return true if a employee with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeExistById(String id) {
        try {
            return employeeRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeService -> isEmployeeExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee data transfer object by checking if the required fields
     * are not empty and if the employee already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeDTO the employee data transfer object to be validated
     * @param branchCode  the branch code for which to validate the equipmen
     * @return a list of validation error messages, or an empty list if the employee is valid
     */
    private List<String> validateEmployee(EmployeeDTO employeeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        // Check if employee fields
        validations.addAll(validateEmptyEmployeeFields(employeeDTO));
        // Check if employee exists
        validations.addAll(validateEmployeeExistence(employeeDTO, branchCode));
        return validations;
    }

    /**
     * Validates the employee data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeDTO the employee data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmptyEmployeeFields(EmployeeDTO employeeDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeDTO.getEmployeeNumber())) {
            validations.add(EmployeeValidationMessages.EMPTY_EMPLOYEE_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getNameWithInitials())) {
            validations.add(EmployeeValidationMessages.EMPTY_NAME_WITH_INITIALS);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getFirstName())) {
            validations.add(EmployeeValidationMessages.EMPTY_FIRST_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getLastName())) {
            validations.add(EmployeeValidationMessages.EMPTY_LAST_NAME);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getFullName())) {
            validations.add(EmployeeValidationMessages.EMPTY_FULL_NAME);
        } else if (ValidatorUtils.dateValidator.test(employeeDTO.getDob())) {
            validations.add(EmployeeValidationMessages.EMPTY_DOB);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getNic())) {
            validations.add(EmployeeValidationMessages.EMPTY_NIC);
        } else if (ValidatorUtils.dateValidator.test(employeeDTO.getJoinDate())) {
            validations.add(EmployeeValidationMessages.EMPTY_JOIN_DATE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getMobileNumber())) {
            validations.add(EmployeeValidationMessages.EMPTY_MOBILE_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getHomeNumber())) {
            validations.add(EmployeeValidationMessages.EMPTY_HOME_NUMBER);
        } else if (ValidatorUtils.stringNullValidator.test(employeeDTO.getHomeAddress())) {
            validations.add(EmployeeValidationMessages.EMPTY_HOME_ADDRESS);
        } else if (ValidatorUtils.statusValidator.test(employeeDTO.getGender().toString())) {
            validations.add(EmployeeValidationMessages.EMPTY_GENDER);
        } else if (ValidatorUtils.statusValidator.test(employeeDTO.getStatus().toString())) {
            validations.add(CommonValidationMessages.EMPTY_STATUS);
        } else if (CollectionUtils.isEmpty(validations)) {
            validations.addAll(validateEmployeeFields(employeeDTO));
        }
        return validations;
    }

    /**
     * Validates the employee data transfer object by checking if the required fields
     * are not invalid. Returns a list of validation error messages.
     *
     * @param employeeDTO the employee data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmployeeFields(EmployeeDTO employeeDTO) {
        List<String> validations = new ArrayList<>();
        if (LocalDate.now().isBefore(employeeDTO.getDob())) {
            validations.add(EmployeeValidationMessages.INVALID_DOB);
        } else if (!ValidatorUtils.nicValidator.test(employeeDTO.getNic())) {
            validations.add(EmployeeValidationMessages.INVALID_NIC);
        } else if (LocalDate.now().isBefore(employeeDTO.getJoinDate())) {
            validations.add(EmployeeValidationMessages.INVALID_JOIN_DATE);
        } else if (!ValidatorUtils.phoneNumberValidator.test(employeeDTO.getMobileNumber())) {
            validations.add(EmployeeValidationMessages.INVALID_MOBILE_NUMBER);
        } else if (!ValidatorUtils.phoneNumberValidator.test(employeeDTO.getHomeNumber())) {
            validations.add(EmployeeValidationMessages.INVALID_HOME_NUMBER);
        }
        return validations;
    }

    /**
     * Validates the employee data transfer object by checking if the employee already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param employeeDTO the employee data transfer object to be validated
     * @param branchCode  the branch code of the equipment to check for existence
     * @return a list of validation error messages, or an empty list if the employee is new or valid
     */
    private List<String> validateEmployeeExistence(EmployeeDTO employeeDTO, String branchCode) {
        List<String> validations = new ArrayList<>();
        Employee employee = getEmployeeFromDatabaseByNIC(employeeDTO.getNic());
        if (!ValidatorUtils.entityNullValidator.test(employee)) {
            if (!ValidatorUtils.stringNullValidator.test(employeeDTO.getId())) { // Edit employee
                if (!employeeDTO.getId().equalsIgnoreCase(employee.getId().toString())) {
                    validations.add(EmployeeValidationMessages.EXIT_NIC);
                }
            } else { // New employee
                validations.add(EmployeeValidationMessages.EXIT_NIC);
            }
        }

        Employee employee1 = getEmployeeFromDatabaseByEmployeeNumberAndBranchCode(employeeDTO.getEmployeeNumber(), branchCode);
        if (!ValidatorUtils.entityNullValidator.test(employee1)) {
            if (!ValidatorUtils.stringNullValidator.test(employeeDTO.getId())) { // Edit employee
                if (!employeeDTO.getId().equalsIgnoreCase(employee1.getId().toString())) {
                    validations.add(EmployeeValidationMessages.EXIT_EMPLOYEE_NUMBER);
                }
            } else { // New employee
                validations.add(EmployeeValidationMessages.EXIT_EMPLOYEE_NUMBER);
            }
        }

        return validations;
    }
}
