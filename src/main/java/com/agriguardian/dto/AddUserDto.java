package com.agriguardian.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AddUserDto {
    @NotBlank(message = "field 'username' may not be blank")
    private String username;
    @NotNull(message = "field 'password' may not be empty")
    private String password;

}
