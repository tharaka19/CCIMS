package com.cims.client.proxyClients;

import com.cims.client.dtos.UserAccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for making HTTP requests to the USER-SERVICE.
 */
@FeignClient(name = "USER-SERVICE")
public interface UserServiceClient {

    /**
     * Retrieves the UserAccountResponseDTO associated with the given token for the client.
     *
     * @param token The token used to retrieve the UserAccountResponseDTO.
     * @return The UserAccountResponseDTO associated with the given token.
     */
    @GetMapping("/user/userAccount/getByToken/{token}")
    UserAccountResponseDTO getByTokenForClient(@PathVariable final String token);
}
