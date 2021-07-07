package com.agriguardian.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddUserFollowerDto {
    @NotBlank(message = "field 'username' may not be empty")
    private String username;
    @NotNull(message = "field 'password' may not be null or empty")
    private String password;
    @NotBlank(message = "field 'name' may not be empty")
    private String name;
}
