package com.cims.auth.service.service;

import com.cims.auth.service.constants.enums.ServiceName;
import com.cims.auth.service.dto.UserAccountDTO;
import com.cims.auth.service.dto.UserTokenDTO;
import com.cims.auth.service.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * The AuthService class provides services related to authentication and authorization.
 * It uses a JwtService to generate and validate JSON Web Tokens (JWTs) and a RestTemplate
 * to make REST API calls to other services.
 */
@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Generates a JWT for the specified user and saves it to the database via a REST API call.
     *
     * @param userName the name of the user to generate the token for
     * @return the generated token
     */
    public String generateToken(String userName) {
        String token = jwtService.generateToken(userName);
        postUserToken(userName, token);
        return token;
    }

    /**
     * Sends a POST request to the admin service to save the user token to the database.
     *
     * @param userName the name of the user associated with the token
     * @param token    the token to be saved
     */
    public void postUserToken(String userName, String token) {
        UserTokenDTO userTokenDTO = new UserTokenDTO(AuthenticationUtils.getUserName(userName), token);
        if(AuthenticationUtils.getServiceName(userName).equals(ServiceName.ADMIN.toString())){
            restTemplate.postForObject("http://localhost:8764/admin/userAccount/saveToken", userTokenDTO, UserAccountDTO.class);
        } else {
            restTemplate.postForObject("http://localhost:8764/user/userAccount/saveToken", userTokenDTO, UserAccountDTO.class);
        }
    }

    /**
     * Validates a JWT.
     *
     * @param token the token to be validated
     */
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
