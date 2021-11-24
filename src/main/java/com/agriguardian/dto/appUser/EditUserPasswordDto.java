package com.agriguardian.dto.appUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditUserPasswordDto {
    @NotNull(message = "field 'newPassword' is mandatory")
    private String newPassword;
    @NotNull(message = "field 'oldPassword' is mandatory")
    private String oldPassword;
}
