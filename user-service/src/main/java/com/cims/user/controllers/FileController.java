package com.cims.user.controllers;

import com.cims.user.constants.enums.FileType;
import com.cims.user.utils.PdfResponseDTO;
import com.cims.user.services.FileService;
import com.cims.user.utils.ResponseDTO;
import com.cims.user.utils.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Controller class for handling file related operations.
 */
@RestController
@RequestMapping("/user/file")
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * Handles the HTTP POST request to upload files.
     *
     * @param file     the file to upload
     * @param fileType the type of the file to upload
     * @return a ResponseEntity containing a ResponseDTO indicating the status of the file upload
     */
    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> upload(MultipartFile[] file, FileType fileType) {
        return fileService.uploadFile(file, fileType);
    }

    /**
     * Handles the HTTP GET request to download a PDF file.
     *
     * @param token   the authorization token for the user
     * @param id      the ID of the file to download
     * @param docName the name of the file to download
     * @param docType the type of the file to download
     * @return a ResponseEntity containing a list of PdfResponseDTOs for the downloaded file
     */
    @GetMapping("/pdfDownloader/{id}/{docName}/{docType}")
    public ResponseEntity<List<PdfResponseDTO>> downloadPDF(@RequestHeader(name = "Authorization") String token, @PathVariable String id, @PathVariable String docName, @PathVariable FileType docType) {
        return new ResponseEntity<>(fileService.getPdfFile(id, docName, docType), HttpStatus.OK);
    }

    /**
     * Handles the HTTP GET request to download an image file.
     *
     * @param response the HTTP response object
     * @param id       the ID of the file to download
     * @param docName  the name of the file to download
     * @param docType  the type of the file to download
     */
    @GetMapping("/imgDownloader/{id}/{docName}/{docType}")
    public void downloadIMG(HttpServletResponse response, @PathVariable String id, @PathVariable String docName, @PathVariable FileType docType) {
        try {
            String documentPath = fileService.downloadFile(id, docName, docType);
            if (documentPath != null) {
                String fileName = StringUtils.extractFileName(docName);
                fileName = (fileName != null) ? fileName : documentPath;

                response.setHeader("Content-Disposition", "inline;filename=" + fileName);
                response.setContentType(docType.toString());

                InputStream in = new FileInputStream(documentPath);
                OutputStream out = response.getOutputStream();
                IOUtils.copy(in, out);
                out.flush();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
