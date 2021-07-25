package com.agriguardian.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmCredentialsDto {
    @NotBlank(message = "token is mandatory")
    private String token;
}
