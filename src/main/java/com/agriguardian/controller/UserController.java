package com.agriguardian.controller;

import com.agriguardian.config.Props;
import com.agriguardian.dto.*;
import com.agriguardian.dto.appUser.*;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.EventType;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import com.agriguardian.exception.AccessDeniedException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.TeamGroupRepository;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        log.debug("[registerUserMaster] user: " + dto.toString());

        AppUser appUser = dto.buildUser();
        appUser.addUserInfo(dto.buildUserInfo());
        appUser.addCreditCard(dto.buildCreditCard());

        AppUser saved = appUserService.saveUserMasterIfNotExist(appUser, Status.REGISTRATION, dto.getWithTeamGroup());

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PutMapping("/master/edit")
    public ResponseUserDto editUserMaster(@Valid @RequestBody EditUserMasterDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[editUserMaster] editor: " + principal.getName());
        log.debug("[editUserMaster] user: " + dto.toString());
        AppUser appUserToEdit = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        AppUser editedUser = dto.buildUser(principal.getName());
        editedUser.addUserInfo(dto.buildUserInfo());
        editedUser.addCreditCard(dto.buildCreditCard());

        AppUser edited = editUserAndNotifyHisGroupsMembers(appUserToEdit, editedUser);

        return ResponseUserDto.of(edited);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PutMapping("/master/password/edit")
    public ResponseUserDto editUserMasterPassword(@Valid @RequestBody EditUserPasswordDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[editUserMasterPassword] user: " + dto.toString());
        AppUser appUserToEdit = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        AppUser edited = appUserService.setNewPasswordForUser(appUserToEdit, dto.getOldPassword(), dto.getNewPassword());

        return ResponseUserDto.of(edited);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PutMapping("/follower/edit")
    public ResponseUserDto editUserFollower
            (@Valid @RequestBody EditUserFollowerDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[editUserFollower] user: " + principal.getName() + "follower: "+ dto.getId());

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        AppUser appUserToEdit = appUserService.findById(dto.getId()).orElseThrow(() -> new NotFoundException("user with id: " + dto.getId() + " was not found"));

        if(appUserToEdit.getUserRole()!=UserRole.USER_FOLLOWER){
            throw new ConflictException("user " + appUserToEdit.getUsername() + "is not a follower");
        }

        if(!appUserService.masterCanEditVulnerable(admin, appUserToEdit)){
            throw new AccessDeniedException("master " + admin.getUsername() + "may not edit follower" + appUserToEdit.getUsername());
        }

        AppUser editedUser = dto.buildUser();
        editedUser.addUserInfo(dto.buildUserInfo());

        AppUser edited = editUserAndNotifyHisGroupsMembers(appUserToEdit, editedUser);

        return ResponseUserDto.of(edited);
    }

    @PutMapping("/follower/editSelf")
    public ResponseUserDto editUserFollowerSelf
            (@Valid @RequestBody EditUserFollowerSelfDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[editUserFollowerSelf] user: " + principal.getName());

        AppUser appUserToEdit = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        if(appUserToEdit.getUserRole()!=UserRole.USER_FOLLOWER){
            throw new ConflictException("user " + appUserToEdit.getUsername() + "is not a follower");
        }

        AppUser editedUser = dto.buildUser(appUserToEdit.getPassword(), appUserToEdit.getUsername());
        editedUser.addUserInfo(dto.buildUserInfo());

        AppUser edited = editUserAndNotifyHisGroupsMembers(appUserToEdit, editedUser);

        return ResponseUserDto.of(edited);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/follower")
    public ResponseUserDto addUserFollower
            (@Valid @RequestBody AddUserFollowerDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[editUserFollower] user: " + principal.getName() + "follower: "+ dto.getUsername());

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserFollowerIfNotExist(vulnerable, Status.ACTIVATED, teamGroups, admin);

        notifyAllUsersFromTeamGroups(teamGroups);

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/device")
    public ResponseUserDto addUserDevice(@Valid @RequestBody AddUserDeviceDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[addUserDevice] user: " + principal.getName() + "device: "+ dto.getMacAddress());

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.setPassword(props.getDevicePass());
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserDeviceIfNotExist(vulnerable, Status.ACTIVATED, teamGroups, admin);

        notifyAllUsersFromTeamGroups(teamGroups);

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping("/beacon")
    public ResponseUserDto addUserBeacon(@Valid @RequestBody AddUserDeviceDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        log.debug("[addUserDevice] user: " + principal.getName() + "beacon: "+ dto.getMacAddress());

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Set<TeamGroup> teamGroups = extractAndCheckTeamGroups(dto.getTeamGroups(), admin);

        AppUser vulnerable = dto.buildUser();
        vulnerable.setPassword(props.getDevicePass());
        vulnerable.addUserInfo(dto.buildUserInfo());

        AppUser saved = appUserService.saveUserBeaconIfNotExist(vulnerable, Status.ACTIVATED, teamGroups, admin);

        notifyAllUsersFromTeamGroups(teamGroups);

        return ResponseUserDto.of(saved);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/follower/delete/{id}")
    public ResponseUserDto deleteFollowerFromUser
            (@PathVariable(name="id")Long id, Principal principal) {
        log.debug("[deleteFollowerFromUser] user: " + principal.getName() + " id: "+ id);

        AppUser admin = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        appUserService.deleteFollowerFromUser(id,admin);


        AppUser adminAfterDeletion = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        return ResponseUserDto.of(adminAfterDeletion);
    }

    @DeleteMapping("/master")
    public ResponseEntity deleteUser(Principal principal) {
        log.debug("[deleteUser] : " + principal.getName());
        appUserService.deleteUser(principal.getName());
        return ResponseEntity.ok("deleted");
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

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @GetMapping(value = "/getSubAccounts")
    public List<SubAccountDto> getSubAccounts(Principal principal) {
        log.debug("[getSubAccounts] for: " + principal.getName());
        AppUser current = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        if(current.getUserRole().equals(UserRole.USER_FOLLOWER)) {
            throw new AccessDeniedException("followers have no sub accounts");
        }

        Map<AppUser, String> relatedUserIdsWithRelationType = appUserService.getAllRelatedWithRelationType(current);

        return new ResponseSubAccountsDto(relatedUserIdsWithRelationType).getSubAccounts();
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

    private void notifyAllUsersFromTeamGroups(Set<TeamGroup> teamGroups){
        teamGroups.forEach(tg -> {
            notificator.notifyUsers(
                    tg.extractUsers(),
                    MessageDto.builder()
                            .event(EventType.TEAM_GROUP_UPDATED)
                            .groupId(tg.getId())
                            .build()
            );
        });
    }

    private AppUser editUserAndNotifyHisGroupsMembers(AppUser appUserToEdit, AppUser editedUser) {
        AppUser edited = appUserService.editUser(editedUser, appUserToEdit);

        if(edited.getAppUserTeamGroups()!=null) {
            Set<TeamGroup> shouldGetNotification = edited.getAppUserTeamGroups().stream().map(appUserTeamGroup -> appUserTeamGroup.getTeamGroup()).collect(Collectors.toSet());
            notifyAllUsersFromTeamGroups(shouldGetNotification);
        }

        return edited;
    }
}
