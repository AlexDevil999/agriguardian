package com.agriguardian.domain;

import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.UserRole;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Log4j2
@ToString
public class AppUserAuthDetails extends AppUser implements UserDetails {
    private Collection<GrantedAuthority> authorities;

    public static AppUserAuthDetails build(String username, String role) {
        AppUserAuthDetails details = new AppUserAuthDetails();
        details.setUsername(username);
        details.authorities = defineAuthorities(role);
        return details;
    }

    private static List<GrantedAuthority> defineAuthorities(String role) {
        return Arrays.asList(UserRole.valueOf(role));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    //todo maybe true instead?
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    //todo maybe true instead?
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    //todo maybe true instead?
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    //todo maybe true instead?
    @Override
    public boolean isEnabled() {
        return true;
    }
}