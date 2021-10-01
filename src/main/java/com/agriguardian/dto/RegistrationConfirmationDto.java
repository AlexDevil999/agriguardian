package com.agriguardian.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationConfirmationDto {
    @NotBlank(message = "field 'username' is mandatory")
    private String username;

    @NotBlank(message = "field 'confirmationCode' is mandatory")
    private String confirmationCode;
}
