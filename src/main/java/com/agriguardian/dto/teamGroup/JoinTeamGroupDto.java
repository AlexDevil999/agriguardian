package com.agriguardian.dto.teamGroup;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Getter
public class JoinTeamGroupDto {
    @NotBlank(message = "field 'invitationCode' is mandatory")
    private String invitationCode;

    private Set<Long> followerIds;
}
