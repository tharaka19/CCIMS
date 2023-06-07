package com.cims.auth.service.service;

import com.cims.auth.service.config.CustomUserDetails;
import com.cims.auth.service.constants.enums.ServiceName;
import com.cims.auth.service.dto.UserAccountDTO;
import com.cims.auth.service.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * The CustomUserDetailsService class implements the UserDetailsService interface to provide
 * a custom user details service for Spring Security. It uses a RestTemplate to make REST API
 * calls to other services to retrieve the user details.
 */
@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Loads the user details for the specified username.
     *
     * @param username the name of the user to load
     * @return the user details for the specified username
     * @throws UsernameNotFoundException if the user cannot be found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccountDTO userAccount;
        Optional<UserAccountDTO> userAccount1;
        if (AuthenticationUtils.getServiceName(username).equals(ServiceName.ADMIN.toString())){
            userAccount = restTemplate.getForObject("http://localhost:8764/admin/userAccount/getByUserName/" + AuthenticationUtils.getUserName(username), UserAccountDTO.class);
            userAccount1 = Optional.ofNullable(userAccount);
            return userAccount1.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
        }
        userAccount = restTemplate.getForObject("http://localhost:8764/user/userAccount/getByUserName/" + AuthenticationUtils.getUserName(username), UserAccountDTO.class);
        userAccount1 = Optional.ofNullable(userAccount);
        return userAccount1.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("user not found with name :" + username));
    }
}
