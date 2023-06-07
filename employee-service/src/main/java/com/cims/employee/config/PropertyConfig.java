package com.cims.employee.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * This class defines the properties configuration for the pay slip.
 */
@Data
@Configuration
public class PropertyConfig {

    /**
     * Sets the location of the pay slip.
     *
     * @param paySlipLocation the location of the pay slip
     */
    @Value("${document.paySlip}")
    private String paySlipLocation;
}
