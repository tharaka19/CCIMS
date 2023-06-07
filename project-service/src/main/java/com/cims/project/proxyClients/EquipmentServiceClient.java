package com.cims.project.proxyClients;

import com.cims.project.utils.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for making HTTP requests to the EQUIPMENT-SERVICE.
 */
@FeignClient(name = "EQUIPMENT-SERVICE")
public interface EquipmentServiceClient {

    @GetMapping("equipment/equipmentStock/updateEquipmentQuantity/{id}/{availableQuantity}")
    ResponseEntity<ResponseDTO> updateEquipmentQuantity(@PathVariable String id, @PathVariable Integer availableQuantity);
}
