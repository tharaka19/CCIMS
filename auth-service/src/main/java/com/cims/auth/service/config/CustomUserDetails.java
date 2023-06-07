package com.cims.auth.service.config;

import com.cims.auth.service.dto.UserAccountDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * CustomUserDetails is a class that implements the UserDetails interface to provide
 * custom user authentication details for a Spring Security application. It takes a
 * UserAccountDTO object as input and extracts the username and password information
 * from it to create a new UserDetails object.
 */
public class CustomUserDetails implements UserDetails {

    private String userName;
    private String password;

    /**
     * Constructor for CustomUserDetails that takes a UserAccountDTO object as input
     * and extracts the username and password information from it to create a new
     * UserDetails object.
     *
     * @param userAccountDTO The UserAccountDTO object containing the user's account details.
     */
    public CustomUserDetails(UserAccountDTO userAccountDTO) {
        this.userName = userAccountDTO.getUserName();
        this.password = userAccountDTO.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
