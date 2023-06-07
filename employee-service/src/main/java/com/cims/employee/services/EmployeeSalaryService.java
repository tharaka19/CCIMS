package com.cims.employee.services;

import com.cims.employee.config.PropertyConfig;
import com.cims.employee.constants.CommonMessages;
import com.cims.employee.constants.VarList;
import com.cims.employee.constants.enums.Month;
import com.cims.employee.constants.validationMessages.EmployeeSalaryValidationMessages;
import com.cims.employee.dtos.EmployeeSalaryDTO;
import com.cims.employee.dtos.UserAccountResponseDTO;
import com.cims.employee.entities.*;
import com.cims.employee.proxyClients.UserServiceClient;
import com.cims.employee.repositories.EmployeeSalaryRepository;
import com.cims.employee.utils.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * Service class that handles operations related to employee salary.
 */
@Service
@Transactional
@Slf4j
public class EmployeeSalaryService {

    @Autowired
    private EmployeeSalaryRepository employeeSalaryRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private FinancialYearService financialYearService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeProfileService employeeProfileService;

    @Autowired
    private EmployeeTypeService employeeTypeService;

    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private MapperUtils mapperUtils;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Saves or updates a employee salary in the database based on the provided employee salary DTO.
     *
     * @param employeeSalaryDTO the DTO representing the employee salary to be saved or updated
     * @return a ResponseEntity containing a ResponseDTO with details of the operation's success or failure
     */
    public ResponseEntity<ResponseDTO> saveUpdateEmployeeSalary(String token, EmployeeSalaryDTO employeeSalaryDTO) {
        try {
            UserAccountResponseDTO userAccountResponseDTO = userServiceClient.getByTokenForClient(token);
            List<String> validations = validateEmployeeSalary(employeeSalaryDTO);
            if (!CollectionUtils.isEmpty(validations)) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            employeeSalaryDTO.setBranchCode(userAccountResponseDTO.getBranchCode());
            saveEmployeeSalaryToDatabase(mapperUtils.mapDTOToEntity(employeeSalaryDTO, EmployeeSalary.class));
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, employeeSalaryDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> saveUpdateEmployeeSalary()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a list of all employee salaries from the database that belong to the same branch as the user with the provided token
     * and maps them to DTOs before returning them in a ResponseEntity.
     *
     * @param token The user token used to authorize the operation.
     * @return a ResponseEntity containing a List of EmployeeSalaryDTOs representing all employee salaries from the database that belong to the same branch
     */
    public ResponseEntity<ResponseDTO> getAllEmployeeSalaries(String token) {
        try {
            List<EmployeeSalary> employeeSalaries = getEmployeeSalariesFromDatabase(token);
            return mapperUtils.mapEntitiesToDTOs(employeeSalaries, EmployeeSalaryDTO.class);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> getAllEmployeeSalaries()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a employee salary from the database based on the provided ID and maps it to a DTO before returning it in a ResponseEntity.
     *
     * @param id the ID of the employee salary to be retrieved
     * @return a ResponseEntity containing a EmployeeSalaryDTO representing the employee salary with the provided ID, or an error response if the employee salary does not exist or an error occurs
     */
    public ResponseEntity<ResponseDTO> getEmployeeSalaryById(String id) {
        try {
            if (isEmployeeSalaryExistById(id)) {
                EmployeeSalary employeeSalaries = getEmployeeSalaryFromDatabaseById(id);
                return mapperUtils.mapEntityToDTO(employeeSalaries, EmployeeSalaryDTO.class);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> getEmployeeSalaryById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a employee salary from the database based on the provided ID and returns a ResponseEntity with details of the operation's success or failure.
     *
     * @param id the ID of the employee salary to be deleted
     * @return a ResponseEntity indicating whether the deletion was successful or if an error occurred
     */
    public ResponseEntity<ResponseDTO> deleteEmployeeSalaryById(String id) {
        try {
            if (isEmployeeSalaryExistById(id)) {
                deleteEmployeeSalaryFromDatabase(id);
                return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.DELETED_SUCCESSFULLY, null, HttpStatus.OK);
            } else {
                return responseUtils.createResponseDTO(VarList.RSP_NO_DATA_FOUND, CommonMessages.NO_DATA, null, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> deleteEmployeeSalaryById()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves a employee salary entity to the database.
     *
     * @param employeeSalary the employee salary entity to be saved
     */
    public void saveEmployeeSalaryToDatabase(EmployeeSalary employeeSalary) {
        try {
            employeeSalaryRepository.save(employeeSalary);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> saveEmployeeSalaryToDatabase()" + e);
        }
    }

    /**
     * Retrieves all employee salaries from the database that belong to the same branch as the user with the provided token.
     *
     * @param token The user token used to authorize the operation.
     * @return a List of EmployeeSalary entities contain all employee salaries from the database that belong to the same branch
     */
    public List<EmployeeSalary> getEmployeeSalariesFromDatabase(String token) {
        try {
            String branchCode = userServiceClient.getByTokenForClient(token).getBranchCode();
            return employeeSalaryRepository.findAllByBranchCode(branchCode);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> getEmployeeSalariesFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a employee salary entity from the database based on its ID.
     *
     * @param id the ID of the employee salary entity to retrieve
     * @return a EmployeeSalary entity representing the requested employee salary, or null if the ID is not valid or an error occurs
     */
    public EmployeeSalary getEmployeeSalaryFromDatabaseById(String id) {
        try {
            return employeeSalaryRepository.findById(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> getEmployeeSalaryFromDatabase()" + e);
            return null;
        }
    }

    /**
     * Retrieves a EmployeeSalary object from the database that matches the given employee salary and branch code.
     *
     * @param financialYearId a String representing the employee salary to search for
     * @param employeeId      a String representing the branch code to search for
     * @return a EmployeeSalary object if one is found, or null if none is found or if an exception occurs during the search
     */
    public EmployeeSalary getEmployeeSalaryFromDatabaseByFinancialYearAndEmployeeAndSalaryMonth(String financialYearId, String employeeId, Month salaryMonth) {
        try {
            FinancialYear financialYear = financialYearService.getFinancialYearFromDatabaseById(financialYearId);
            Employee employee = employeeService.getEmployeeFromDatabaseById(employeeId);
            return employeeSalaryRepository.findByFinancialYearAndEmployeeAndSalaryMonth(financialYear, employee, salaryMonth);
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> getEmployeeSalaryFromDatabaseByEmployeeSalaryAndBranchCode()" + e);
            return null;
        }
    }

    /**
     * Deletes a employee salary from the database by its ID.
     *
     * @param id the ID of the employee salary to be deleted
     */
    public void deleteEmployeeSalaryFromDatabase(String id) {
        try {
            employeeSalaryRepository.deleteById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> deleteEmployeeSalaryFromDatabase()" + e);
        }
    }

    /**
     * Checks whether a employee salary exists in the database by its ID.
     *
     * @param id the ID of the employee salary to check for existence
     * @return true if a employee salary with the specified ID exists in the database, false otherwise
     */
    public boolean isEmployeeSalaryExistById(String id) {
        try {
            return employeeSalaryRepository.existsById(Long.parseLong(id));
        } catch (NumberFormatException e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> isEmployeeSalaryExist()" + e);
            return false;
        }
    }

    /**
     * Validates the employee salary data transfer object by checking if the required fields
     * are not empty and if the employee salary already exists in the database. Returns a list
     * of validation error messages.
     *
     * @param employeeSalaryDTO the employee salary data transfer object to be validated
     * @param branchCode        the branch code for which to validate the employee salary
     * @return a list of validation error messages, or an empty list if the employee salary is valid
     */
    private List<String> validateEmployeeSalary(EmployeeSalaryDTO employeeSalaryDTO) {
        List<String> validations = new ArrayList<>();
        // Check if employeeSalary fields
        validations.addAll(validateEmptyEmployeeSalaryFields(employeeSalaryDTO));
        // Check if employeeSalary exists
        validations.addAll(validateEmployeeSalaryExistence(employeeSalaryDTO));
        return validations;
    }

    /**
     * Validates the employee salary data transfer object by checking if the required fields
     * are not empty. Returns a list of validation error messages.
     *
     * @param employeeSalaryDTO the employee salary data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the fields are valid
     */
    private List<String> validateEmptyEmployeeSalaryFields(EmployeeSalaryDTO employeeSalaryDTO) {
        List<String> validations = new ArrayList<>();
        if (ValidatorUtils.stringNullValidator.test(employeeSalaryDTO.getFinancialYearId())) {
            validations.add(EmployeeSalaryValidationMessages.EMPTY_FINANCIAL_YEAR);
        } else if (ValidatorUtils.stringNullValidator.test(employeeSalaryDTO.getEmployeeId())) {
            validations.add(EmployeeSalaryValidationMessages.EMPTY_EMPLOYEE);
        } else if (ValidatorUtils.stringNullValidator.test(employeeSalaryDTO.getSalaryMonth().toString())) {
            validations.add(EmployeeSalaryValidationMessages.EMPTY_SALARY_MONTH);
        } else if (!ValidatorUtils.priceNullValidator.test(employeeSalaryDTO.getTotalOT()) && !ValidatorUtils.priceValidator.test(employeeSalaryDTO.getTotalOT())) {
            validations.add(EmployeeSalaryValidationMessages.INVALID_TOTAL_OT);
        }
        return validations;
    }

    /**
     * Validates the employee salary data transfer object by checking if the employee salary already
     * exists in the database. Returns a list of validation error messages.
     *
     * @param employeeSalaryDTO the employee salary data transfer object to be validated
     * @return a list of validation error messages, or an empty list if the employee salary is new or valid
     */
    private List<String> validateEmployeeSalaryExistence(EmployeeSalaryDTO employeeSalaryDTO) {
        List<String> validations = new ArrayList<>();
        EmployeeSalary employeeSalary = getEmployeeSalaryFromDatabaseByFinancialYearAndEmployeeAndSalaryMonth(employeeSalaryDTO.getFinancialYearId(), employeeSalaryDTO.getEmployeeId(), employeeSalaryDTO.getSalaryMonth());
        if (!ValidatorUtils.entityNullValidator.test(employeeSalary)) {
            if (!ValidatorUtils.stringNullValidator.test(employeeSalaryDTO.getId())) { // Edit employee salary
                if (!employeeSalaryDTO.getId().equalsIgnoreCase(employeeSalary.getId().toString())) {
                    validations.add(EmployeeSalaryValidationMessages.EXIT_SALARY_MONTH);
                }
            } else { // New employee salary
                validations.add(EmployeeSalaryValidationMessages.EXIT_SALARY_MONTH);
            }
        }
        return validations;
    }

    /**
     * This method exports the payslip of an employee based on the given employee ID.
     * It fetches the employee's salary and profile details from the database using the ID,
     * retrieves the relevant allowance types from the employee profile, compiles a JasperReport,
     * populates the report with the data, and exports the report as a PDF file to the configured location.
     * The method then reads the PDF file and returns it as a List of PdfResponseDTO objects containing the PDF file's byte array and file path.
     *
     * @param id The ID of the employee whose payslip is to be exported.
     * @return A List of PdfResponseDTO objects containing the PDF file's byte array and file path.
     */
    public List<PdfResponseDTO> exportPaySlip(String id) {
        PdfResponseDTO pdfResponseDTO = new PdfResponseDTO();
        List<PdfResponseDTO> pdf = new ArrayList<>();
        try {
            EmployeeSalary employeeSalary = getEmployeeSalaryFromDatabaseById(id);
            EmployeeProfile employeeProfile = employeeProfileService.getEmployeeProfileFromDatabaseByEmployeeAndFinancialYear(employeeSalary.getFinancialYear(), employeeSalary.getEmployee());
            List<AllowanceType> allowanceTypes = employeeProfile.getAllowanceTypes();

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(allowanceTypes);
            File file = ResourceUtils.getFile(propertyConfig.getPaySlipLocation().concat("/paySlip.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());

            Map<String, Object> parameters = getPaySlipParameters(employeeSalary, employeeProfile);
            String report = "pay_slip_.pdf_".concat(String.valueOf(new Date().getTime()));
            String filePath = propertyConfig.getPaySlipLocation().concat(File.separator).concat(report);
            pdfResponseDTO.setFilePath(filePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);

            byte[] contents = Files.readAllBytes(new File(filePath).toPath());
            pdfResponseDTO.setByteArray(contents);
            pdf.add(pdfResponseDTO);
        } catch (Exception e) {
            log.warn("/**************** Exception in EmployeeSalaryService -> exportPaySlip()" + e);
        }
        return pdf;
    }

    /**
     * This method generates a Map of parameters required to populate the payslip report template with data.
     * It takes in an EmployeeSalary object and an EmployeeProfile object and retrieves the employee, employee type,
     * and various details required to calculate gross pay, deductions, and net pay. It then populates a Map with
     * these details and returns it.
     *
     * @param employeeSalary  The EmployeeSalary object representing the employee's salary details.
     * @param employeeProfile The EmployeeProfile object representing the employee's profile details.
     * @return A Map of parameters required to populate the payslip report template with data.
     */
    public Map<String, Object> getPaySlipParameters(EmployeeSalary employeeSalary, EmployeeProfile employeeProfile) {
        Employee employee = employeeService.getEmployeeFromDatabaseById(employeeProfile.getEmployee().getId().toString());
        EmployeeType employeeType = employeeTypeService.getEmployeeTypeFromDatabaseById(employeeProfile.getEmployeeType().getId().toString());
        Double grossPay = employeeType.getBasicPay() + employeeSalary.getTotalOT() + employeeSalary.getTotalAllowance();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("employeeName", employee.getNameWithInitials());
        parameters.put("employeeNumber", employee.getEmployeeNumber());
        parameters.put("basicPay", employeeType.getBasicPay());
        parameters.put("totalOT", employeeSalary.getTotalOT());
        parameters.put("grossPay", grossPay);
        parameters.put("epf", employeeType.getEpf());
        parameters.put("totalDeductions", employeeType.getEpf());
        parameters.put("netPay", grossPay - employeeType.getEpf());
        parameters.put("epfCoContribution", employeeType.getEpfCoContribution());
        parameters.put("totalEpf", employeeType.getTotalEpf());
        parameters.put("etf", employeeType.getEtf());
        return parameters;
    }
}
