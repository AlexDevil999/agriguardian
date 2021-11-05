package com.agriguardian.dto.appUser;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class ResponseSubAccountsDto {
    private Map<Long, String> subAccountsIdsAndRelations;

    public ResponseSubAccountsDto(Map<Long, String> subAccountsIdsAndRelations) {
        this.subAccountsIdsAndRelations = subAccountsIdsAndRelations;
    }
}
