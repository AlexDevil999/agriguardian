package com.agriguardian.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResendConfirmationDto {
    @NotBlank(message = "field 'username' is mandatory")
    private String username;

}
