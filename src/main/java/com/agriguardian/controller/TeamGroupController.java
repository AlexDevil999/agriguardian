package com.agriguardian.controller;

import com.agriguardian.dto.ResponseTeamGroupDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.dto.teamGroup.JoinTeamGroupDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AppUserTeamGroupRepository;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
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
    private final AppUserTeamGroupRepository autgRepository;


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/join")
    public ResponseUserDto joinGroup(@Valid @RequestBody JoinTeamGroupDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findByInvitationCode(dto.getInvitationCode())
                .orElseThrow(() -> new NotFoundException("group not found; resource: code " + dto.getInvitationCode()));
        GroupRole groupRole = defineInvCodeOrThrowBadRequest(teamGroup, dto.getInvitationCode());

        AppUserTeamGroup autg = user.addTeamGroup(teamGroup, groupRole);
        autgRepository.save(autg);

        return ResponseUserDto.of(user);
    }

    @GetMapping("/{id}")
    public ResponseTeamGroupDto findById(@PathVariable Long id, Principal principal) {

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
