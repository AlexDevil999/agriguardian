package com.agriguardian.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteDevicesDto {
    @NotBlank(message = "username is mandatory")
    private String username;

    @NotNull(message = "set of mac addresses is mandatory")
    private Set<String> macAddresses;
}
