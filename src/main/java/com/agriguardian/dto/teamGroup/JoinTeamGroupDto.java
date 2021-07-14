package com.agriguardian.dto.teamGroup;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class JoinTeamGroupDto {
    @NotBlank(message = "field 'ownerUsername' is mandatory")
    private String ownerUsername;
    @NotBlank(message = "field 'invitationCode' is mandatory")
    private String invitationCode;
}
