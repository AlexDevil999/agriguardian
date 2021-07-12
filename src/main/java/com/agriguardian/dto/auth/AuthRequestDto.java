package com.agriguardian.dto.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthRequestDto {
    @NotBlank(message = "username/email is mandatory")
    private String username;
    @NotBlank(message = "password is mandatory")
    private String password;
}