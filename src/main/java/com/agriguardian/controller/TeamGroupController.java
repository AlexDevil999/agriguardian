package com.agriguardian.controller;

import com.agriguardian.dto.MessageDto;
import com.agriguardian.dto.ResponseTeamGroupDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.dto.teamGroup.JoinTeamGroupDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.EventType;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/team-groups")
public class TeamGroupController {
    private final AppUserService appUserService;
    private final TeamGroupService teamGroupService;
    private final Notificator notificator;


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/join")
    public ResponseUserDto joinGroup(@Valid @RequestBody JoinTeamGroupDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findByInvitationCode(dto.getInvitationCode())
                .orElseThrow(() -> new NotFoundException("group not found; resource: code " + dto.getInvitationCode()));
        GroupRole groupRole = defineInvCodeOrThrowBadRequest(teamGroup, dto.getInvitationCode());

        if(user.getUserRole().equals(UserRole.USER_FOLLOWER)){
            if(groupRole==GroupRole.GUARDIAN)
                throw new ConflictException("follower may not be invited as a guardian to group");

            if(dto.getFollowerIds()!=null){
                throw new ConflictException("follower may not add devices to group");
            }
        }

        teamGroupService.addUserToTeamGroup(user,teamGroup, groupRole);

        if(dto.getFollowerIds()!=null){
            for (Long id:dto.getFollowerIds()) {
                AppUser userToAddToGroup = appUserService.findById(id).orElseThrow(() -> new NotFoundException("user with id: "+id+"was not found"));
                teamGroupService.addUserToTeamGroup(userToAddToGroup,teamGroup,GroupRole.VULNERABLE);
            }
        }

        notificator.notifyUsers(
                teamGroup.extractUsers(),
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(teamGroup.getId())
                        .build()
        );

        return ResponseUserDto.of(user);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/{tgId}/{userId}")
    public ResponseTeamGroupDto deleteUserFromTeamGroup(Principal principal,
                                                  @PathVariable("userId") Long userId,
                                                  @PathVariable("tgId") Long tgId) {
        AppUser deleter = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup updatedTg = teamGroupService.deleteAppUserFromTeamGroupByTeamGroupAdmin(deleter,tgId,userId);
        return ResponseTeamGroupDto.of(updatedTg);

    }

    @GetMapping("/{id}")
    public ResponseTeamGroupDto findById(@PathVariable Long id, Principal principal) {
        log.debug("[findById] user: {}; team group id: {}.", principal.getName(), id);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findById(id)
                .orElseThrow(() -> new NotFoundException("group not found; resource: id " + id));

        if (!teamGroup.containsUser(user)) {
            throw new AccessDeniedException("user does not have permissions for team group; resource: group id" + id);
        }

        return ResponseTeamGroupDto.of(teamGroup);
    }


    private GroupRole defineInvCodeOrThrowBadRequest(TeamGroup tg, String code) {
        if (tg.getGuardianInvitationCode().equals(code)) return GroupRole.GUARDIAN;
        else if (tg.getVulnerableInvitationCode().equals(code)) return GroupRole.VULNERABLE;
        else throw new BadRequestException("unknown code: " + code);
    }
}
