package com.cims.user.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class defines the properties configuration for the document, record, and max image size.
 */
@Data
@Configuration
public class PropertyConfig {

    /**
     * Sets the location of the document.
     * @param documentLocation the location of the document
     */
    @Value("${document.location}")
    private String documentLocation;

    /**
     * Sets the location of the record.
     * @param recordLocation the location of the record
     */
    @Value("${document.history}")
    private String historyLocation;

    /**
     * Sets the project of the record.
     * @param projectLocation the location of the project
     */
    @Value("${document.project}")
    private String projectLocation;

    /**
     * Sets the maximum size of the image.
     * @param maxImageSize the maximum size of the image
     */
    @Value("${document.max.image}")
    private String maxImageSize;
}
