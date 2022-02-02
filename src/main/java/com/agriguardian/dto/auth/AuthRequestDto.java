package com.agriguardian.dto.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthRequestDto {

    @NotBlank(message = "username/email is mandatory")
    private String username;
    @NotNull(message = "password is mandatory")
    private String password;
}