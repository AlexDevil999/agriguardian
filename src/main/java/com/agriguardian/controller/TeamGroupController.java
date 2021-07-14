package com.agriguardian.controller;

import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.dto.teamGroup.JoinTeamGroupDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/team-groups")
public class TeamGroupController {
    private final AppUserService appUserService;
    private final TeamGroupService teamGroupService;

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/join")
    public ResponseUserDto joinGroup(@Valid @RequestBody JoinTeamGroupDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AppUser groupOwner = appUserService.findByUsernameOrThrowNotFound(dto.getOwnerUsername());
        GroupRole gr = defineInvitationCode(groupOwner, dto.getInvitationCode());

        user.addTeamGroup(groupOwner.getTeamGroup(), gr);
        appUserService.save(user);

//        todo  add saving of TG???
        return ResponseUserDto.of(user);
    }

    private GroupRole defineInvitationCode(AppUser groupOwner, String code) {
        TeamGroup tg = groupOwner.getTeamGroup();
        if (tg == null) {
            throw new BadRequestException("user "  + groupOwner.getUsername() + " does not own a team group");
        }
        if (tg.getGuardianInvitationCode().equals(code)) return GroupRole.GUARDIAN;
        else if (tg.getVulnerableInvitationCode().equals(code)) return GroupRole.VULNERABLE;
        else throw new AccessDeniedException("code mismatch");
    }

    public boolean checkOwnership(AppUser appUser, TeamGroup teamGroup) {

        return true;
    }
}
