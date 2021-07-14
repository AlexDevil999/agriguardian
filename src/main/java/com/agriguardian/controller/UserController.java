package com.agriguardian.controller;

import com.agriguardian.dto.appUser.AddUserFollowerDto;
import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.AccessDeniedException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final AppUserService appUserService;

    @PostMapping("/master")
    public ResponseUserDto addUserMaster(@Valid @RequestBody AddUserMasterDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);

        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());
        appUser.addCreditCard(dto.buildCreditCard());

        AppUser saved = appUserService.saveUserMasterIfNotExist(appUser, Status.ACTIVATED, dto.getWithTeamGroup());

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/follower")
    public ResponseUserDto addUserFollower(@Valid @RequestBody AddUserFollowerDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserFollowerIfNotExist(vulnerable, Status.ACTIVATED, teamGroups);

        return ResponseUserDto.of(saved);
    }


    private Set<TeamGroup> extractAndCheckTeamGroups(Set<Long> targetTeamGroupIds, AppUser user) {
        Set<TeamGroup> teamGroups = new HashSet<>();
        user.getAppUserTeamGroups().forEach(utg -> {
            long tgId = utg.getTeamGroup().getId();
            if (targetTeamGroupIds.remove(tgId)) {
                if (utg.getGroupRole() == GroupRole.VULNERABLE) {
                    throw new AccessDeniedException(String.format(
                            "weak permission; resource: teamGroupId %d; groupRole: %s", tgId, GroupRole.VULNERABLE.name()));
                }
                teamGroups.add(utg.getTeamGroup());
            }
        });

        if (!targetTeamGroupIds.isEmpty()) {
            throw new AccessDeniedException(String.format("permissions are absent; resource: teamGroupId %s",
                    targetTeamGroupIds.stream().map(String::valueOf).collect(joining(","))));
        }
        return teamGroups;
    }
}
