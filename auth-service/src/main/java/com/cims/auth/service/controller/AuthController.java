package com.cims.auth.service.controller;

import com.cims.auth.service.dto.AuthRequestDTO;
import com.cims.auth.service.service.AuthService;
import com.cims.auth.service.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * The AuthController class is a Spring REST controller that handles authentication
 * requests for a web application. It exposes two endpoints - one for generating an
 * authentication token and one for validating a token.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * This method handles POST requests to /auth/token and generates an authentication
     * token for the specified user. It uses the AuthenticationManager to authenticate the
     * user credentials and returns a token if authentication is successful.
     *
     * @param authRequest The AuthRequest object containing the user's username and password
     * @return A string representation of the authentication token
     * @throws RuntimeException if authentication fails
     */
    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequestDTO authRequest) {
        String userName = AuthenticationUtils.prependServiceNameToUserName(authRequest);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            return authService.generateToken(authRequest.getUserName());
        } else {
            throw new RuntimeException("invalid access");
        }
    }

    /**
     * This method handles GET requests to /auth/validate and validates the specified
     * authentication token. It uses the AuthService to check the validity of the token
     * and returns a message indicating whether the token is valid or not.
     *
     * @param token The authentication token to be validated
     * @return A message indicating whether the token is valid or not
     */
    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }
}
