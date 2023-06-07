package com.cims.user.services;

import com.cims.user.config.PropertyConfig;
import com.cims.user.constants.CommonMessages;
import com.cims.user.constants.VarList;
import com.cims.user.constants.enums.FileType;
import com.cims.user.constants.validationMessages.FileValidationMessages;
import com.cims.user.utils.PdfResponseDTO;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.ResponseUtils;
import com.cims.user.utils.ValidatorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class that handles operations related to files.
 */
@Service
@Slf4j
public class FileService {

    @Autowired
    PropertyConfig propertyConfig;

    @Autowired
    private ResponseUtils responseUtils;

    /**
     * Uploads one or more files of a given type, performs basic validation, saves the file(s) to disk, and returns a response
     * with the status of the operation and the saved file path(s).
     *
     * @param files    an array of MultipartFile objects representing the file(s) to be uploaded
     * @param fileType the FileType enum value representing the type of file being uploaded
     * @return a ResponseEntity object with a ResponseDTO payload containing the status of the operation, any validation errors,
     * and the saved file path(s), if applicable
     */
    public ResponseEntity<ResponseDTO> uploadFile(MultipartFile[] files, FileType fileType) {
        try {
            List<String> validations = validateFiles(files, fileType);
            if (!validations.isEmpty()) {
                return responseUtils.createResponseDTO(VarList.RSP_FAIL, validations, null, HttpStatus.ACCEPTED);
            }
            String savedFilePath;
            switch (fileType) {
                case DOCUMENT:
                    savedFilePath = saveDocument(files[0]);
                    break;
                case HISTORY:
                    savedFilePath = saveHistory(files[0]);
                    break;
                case PROJECT:
                    savedFilePath = saveProject(files[0]);
                    break;
                default:
                    return responseUtils.createResponseDTO(VarList.RSP_FAIL, CommonMessages.ERROR, null, HttpStatus.ACCEPTED);
            }
            return responseUtils.createResponseDTO(VarList.RSP_SUCCESS, CommonMessages.SAVED_SUCCESSFULLY, savedFilePath, HttpStatus.OK);
        } catch (Exception e) {
            log.warn("/**************** Exception in FileService -> uploadFile()" + e);
            return responseUtils.createResponseDTO(VarList.RSP_ERROR, e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves the file name for a given document id and document type, based on the
     * configured file locations. The file name is constructed by concatenating the file
     * name with the configured document or history location. If no file name is found for
     * the given document type and id, a log message is printed and null is returned.
     *
     * @param id      the document id to retrieve the file name for
     * @param docName the original name of the document file
     * @param docType the type of the document (either FileType.DOCUMENT or FileType.HISTORY)
     * @return the file name for the given document id and type, or null if no file is found
     */
    public String downloadFile(String id, String docName, FileType fileType) {
        String fileName;
        switch (fileType) {
            case DOCUMENT:
                fileName = docName;
                fileName = (fileName == null) ? null : propertyConfig.getDocumentLocation().concat(File.separator).concat(fileName);
                break;
            case HISTORY:
                fileName = docName;
                fileName = (fileName == null) ? null : propertyConfig.getHistoryLocation().concat(File.separator).concat(fileName);
                break;
            case PROJECT:
                fileName = docName;
                fileName = (fileName == null) ? null : propertyConfig.getProjectLocation().concat(File.separator).concat(fileName);
                break;
            default:
                log.info("/************* No File Found :" + id + "\t :" + fileType);
                fileName = null;
        }
        return fileName;
    }

    /**
     * Retrieves a PDF file as a byte array and returns it as a list of PdfResponseDTO objects.
     *
     * @param id      the ID of the document to download
     * @param docName the name of the document to download
     * @param docType the type of the document to download (e.g. "PDF", "DOCX", "XLSX")
     * @return a list of PdfResponseDTO objects containing the downloaded PDF file as a byte array
     */
    public List<PdfResponseDTO> getPdfFile(String id, String docName, FileType docType) {
        PdfResponseDTO pdfResponseDTO = new PdfResponseDTO();
        List<PdfResponseDTO> pdf = new ArrayList<>();
        try {
            String documentPath = this.downloadFile(id, docName, docType);
            if (documentPath != null) {
                byte[] contents = Files.readAllBytes(new File(documentPath).toPath());
                pdfResponseDTO.setByteArray(contents);
                pdfResponseDTO.setType(docType.toString());
                pdf.add(pdfResponseDTO);
            }
        } catch (Exception e) {
            log.warn("/**************** Exception in DocumentController -> downloader() {}", e);
        }
        return pdf;
    }

    /**
     * Saves a document file to the location specified in the application's properties file and returns the generated document name.
     *
     * @param file the document file to save
     * @return the generated name of the saved document file
     * @throws IOException if an error occurs while saving the file
     */
    public String saveDocument(MultipartFile file) throws IOException {
        String documentName = generateFileName(file);
        String filePath = propertyConfig.getDocumentLocation().concat(File.separator).concat(documentName);
        saveFile(file, filePath);
        return documentName;
    }

    /**
     * Saves a history file to the location specified in the application's properties file and returns the generated history name.
     *
     * @param file the history file to save
     * @return the generated name of the saved history file
     * @throws IOException if an error occurs while saving the file
     */
    public String saveHistory(MultipartFile file) throws IOException {
        String historyName = generateFileName(file);
        String filePath = propertyConfig.getHistoryLocation().concat(File.separator).concat(historyName);
        saveFile(file, filePath);
        return historyName;
    }

    /**
     * Saves a project file to the location specified in the application's properties file and returns the generated project name.
     *
     * @param file the project file to save
     * @return the generated name of the saved project file
     * @throws IOException if an error occurs while saving the file
     */
    public String saveProject(MultipartFile file) throws IOException {
        String projectName = generateFileName(file);
        String filePath = propertyConfig.getProjectLocation().concat(File.separator).concat(projectName);
        saveFile(file, filePath);
        return projectName;
    }

    /**
     * Generates a file name for the specified file by concatenating the original file name with a timestamp.
     *
     * @param file the file to generate a name for
     * @return the generated name of the file
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return originalFilename.concat("_").concat(timestamp);
    }

    /**
     * Saves a file to the specified file path.
     *
     * @param file     the file to save
     * @param filePath the location to save the file to
     * @throws IOException if an error occurs while saving the file
     */
    public void saveFile(MultipartFile file, String filePath) throws IOException {
        File destination = new File(filePath);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn("/**************** Exception in FileService -> saveFile()", e);
            throw e;
        }
    }

    /**
     * Validates an array of uploaded files to ensure they are of the specified file type and do not exceed the maximum allowed size.
     * Returns a list of error messages if any validations fail.
     *
     * @param files    the array of files to validate
     * @param fileType the expected file type of the uploaded files
     * @return a list of error messages, if any validations fail
     */
    private List<String> validateFiles(MultipartFile[] files, FileType fileType) {
        List<String> validations = new ArrayList<>();
        int maxSize = Integer.parseInt(propertyConfig.getMaxImageSize());
        if (!ValidatorUtils.fileTypeValidator.test(fileType)) {
            validations.add(FileValidationMessages.INVALID_FILE_TYPE);
        }
        if (files == null || files.length == 0) {
            validations.add(FileValidationMessages.EMPTY_FILE_TYPE);
            return validations;
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                validations.add(FileValidationMessages.EMPTY_FILE_TYPE);
                return validations;
            }
            if (!isValidFileType(file)) {
                validations.add(FileValidationMessages.INVALID_FILE_TYPE);
                return validations;
            }
            if (file.getSize() > maxSize) {
                validations.add(FileValidationMessages.LARGE_FILE);
            }
        }
        return validations;
    }

    /**
     * Determines whether a single uploaded file is of a valid file type based on its content type.
     *
     * @param file the file to validate
     * @return true if the file is of a valid type, false otherwise
     */
    private boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        String[] allowedTypes = {"image/jpeg", "image/png", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
        for (String allowedType : allowedTypes) {
            if (contentType.equals(allowedType)) {
                return true;
            }
        }
        return false;
    }
}
