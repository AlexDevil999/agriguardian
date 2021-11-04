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
    private String username;

    private Set<String> macAddresses;
}
