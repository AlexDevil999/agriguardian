package com.agriguardian.controller;

import com.agriguardian.dto.appUser.AddUserFollowerDto;
import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.BadTokenException;
import com.agriguardian.service.AppUserService;
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
import java.util.Set;
import java.util.stream.Collectors;

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

        //todo move to service; split admin's and child's groups
        AppUser admin = appUserService.findByUsername(principal.getName()).orElseThrow(() -> new BadTokenException("token is invalid; rsn: user does not exist: " + principal.getName()));

        Set<Long> tGroups = admin.getAppUserTeamGroups().stream().map(utg -> utg.getTeamGroup().getId()).collect(Collectors.toSet());

        dto.getTeamGroups().forEach(tg -> {
            if (!tGroups.contains(tg)) {
                throw new AccessDeniedException(String.format("user %d does not have permissions for the resource: teamGroupId %d", admin.getId(), tg));
            }
        });

        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserFollowerIfNotExist(appUser, Status.ACTIVATED, dto.getTeamGroups());

        return ResponseUserDto.of(saved);
    }
}
