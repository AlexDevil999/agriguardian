package com.agriguardian.dto.appUser;


import com.agriguardian.entity.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
public class ResponseSubAccountsDto {

   private List<SubAccountDto> subAccounts;

   public ResponseSubAccountsDto(Map<AppUser, String> subAccountsAndRelations){
       List<SubAccountDto> responseSubAccountsDtos = new LinkedList<>();
       subAccountsAndRelations.forEach((appUser, relation) -> responseSubAccountsDtos.add(SubAccountDto.of(appUser,relation)));
       subAccounts=responseSubAccountsDtos;
   }


}
