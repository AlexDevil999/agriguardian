package com.agriguardian.dto;

import com.agriguardian.entity.AppUser;
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
    @NotNull(message = "field 'groupId' may not be null")
    private Long groupId;



    public AppUser buildUser() {
        return AppUser.builder()
                .username(username)
                .password(password)
                .name(name)
                .build();
    }
}
