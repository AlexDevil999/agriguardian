package com.agriguardian.controller;

import com.agriguardian.config.Props;
import com.agriguardian.dto.DeleteDevicesDto;
import com.agriguardian.dto.FcmCredentialsDto;
import com.agriguardian.dto.MessageDto;
import com.agriguardian.dto.RegistrationConfirmationDto;
import com.agriguardian.dto.appUser.AddUserDeviceDto;
import com.agriguardian.dto.appUser.AddUserFollowerDto;
import com.agriguardian.dto.appUser.AddUserMasterDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.EventType;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.exception.AccessDeniedException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.EmailSenderService;
import com.agriguardian.service.interfaces.EmailSender;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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
    private final Notificator notificator;
    private final Props props;

    @PostMapping("/master")
    public ResponseUserDto registerUserMaster(@Valid @RequestBody AddUserMasterDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);

        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());
        appUser.addCreditCard(dto.buildCreditCard());

        AppUser saved = appUserService.saveUserMasterIfNotExist(appUser, Status.REGISTRATION, dto.getWithTeamGroup());

        return ResponseUserDto.of(saved);
    }

    @PostMapping("/confirmation")
    public ResponseUserDto confirmUserMaster
            (@RequestBody RegistrationConfirmationDto dto, Errors errors) {
        ValidationDto.handleErrors(errors);
        AppUser currentUser =appUserService.findByUsernameOrThrowNotFound(dto.getUsername());
        AppUser saved = appUserService.activateUser(currentUser,dto.getConfirmationCode());
        return ResponseUserDto.of(saved);
    }

    @PostMapping("/resend")
    public ResponseUserDto resendConfirmation
            (@RequestBody String username, Errors errors) {
        ValidationDto.handleErrors(errors);
        AppUser currentUser =appUserService.findByUsernameOrThrowNotFound(username);
        appUserService.sendEmailConfirmationForUser(currentUser);
        return ResponseUserDto.of(currentUser);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/follower")
    public ResponseUserDto addUserFollower
            (@Valid @RequestBody AddUserFollowerDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserFollowerIfNotExist(vulnerable, Status.ACTIVATED, teamGroups);

        teamGroups.forEach(tg -> {
            notificator.notifyUsers(
                    tg.extractUsers(),
                    MessageDto.builder()
                            .event(EventType.TEAM_GROUP_UPDATED)
                            .groupId(tg.getId())
                            .build()
            );
        });

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/device")
    public ResponseUserDto addUserFollower(@Valid @RequestBody AddUserDeviceDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.setPassword(props.getDevicePass());
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserDeviceIfNotExist(vulnerable, Status.ACTIVATED, teamGroups);
        saved.setUsername("device_" + saved.getId());
        saved = appUserService.save(vulnerable);

        teamGroups.forEach(tg -> {
            notificator.notifyUsers(
                    tg.extractUsers(),
                    MessageDto.builder()
                            .event(EventType.TEAM_GROUP_UPDATED)
                            .groupId(tg.getId())
                            .build()
            );
        });

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/device")
    public ResponseEntity deleteDevicesFromUser
            (@RequestBody @Valid DeleteDevicesDto dto, Errors errors, Principal principal) {

        ValidationDto.handleErrors(errors);

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        appUserService.deleteDevice(dto.getUsername(),dto.getMacAddresses());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/add-fcm-token")
    public ResponseUserDto addFcmCredentials(@Valid @RequestBody FcmCredentialsDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        user.setFcmToken(dto.getToken());

        return ResponseUserDto.of(appUserService.save(user));
    }

    @GetMapping(value = "/current")
    public ResponseUserDto getCurrentUser(@RequestHeader("Authorization") String header,
                                          Principal principal) {
        log.debug("[getCurrentUser] for: " + principal.getName());
        AppUser user = appUserService.findByUsername(principal.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        return ResponseUserDto.of(user);
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
