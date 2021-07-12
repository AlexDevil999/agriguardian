package com.agriguardian.dto;

import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.UserInfo;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
public class AddUserFollowerDto {
    @NotBlank(message = "field 'username' is mandatory")
    private String username;
    @NotNull(message = "field 'password' may not be null")
    private String password;
    @NotBlank(message = "field 'name' is mandatory")
    private String name;




    public AppUser buildUser() {
        return AppUser.builder()
                .username(username)
                .password(password)
                .build();
    }

    public UserInfo buildUserInfo() {
        return UserInfo.builder()
                .name(name)
                .build();
    }
}
