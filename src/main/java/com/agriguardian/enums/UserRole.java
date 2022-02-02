package com.agriguardian.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ADMIN,
    USER_MASTER,   //parent
    USER_FOLLOWER; //child

    @Override
    public String getAuthority() {
        return name();
    }

}
