package com.agriguardian.dto;


import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class RefreshCodesDto {
    @NotNull(message = "field 'guardianCode' may not be null")
    private Boolean guardianCode;

    @NotNull(message = "field 'vulnerableCode' may not be null")
    private Boolean vulnerableCode;

}
