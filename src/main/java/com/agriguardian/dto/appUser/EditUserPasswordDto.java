package com.agriguardian.dto.appUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EditUserPasswordDto {
    private String newPassword;
    private String oldPassword;
}
