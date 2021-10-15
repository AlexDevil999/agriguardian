package com.agriguardian.controller;

import com.agriguardian.dto.AddTeamGroupRuleDto;
import com.agriguardian.dto.MessageDto;
import com.agriguardian.dto.ResponseAlertBluetoothZoneDto;
import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.EventType;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.UserRole;
import com.agriguardian.enums.ZoneType;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AlertBluetoothZoneService;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/alert-bluetooth-zones")
public class AlertBluetoothZoneController {
    private final AppUserService appUserService;
    private final TeamGroupService teamGroupService;
    private final AlertBluetoothZoneService bluetoothZoneService;
    private final Notificator notificator;


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping
    public ResponseAlertBluetoothZoneDto addAlertBluetoothZone(@Valid @RequestBody AddTeamGroupRuleDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        log.trace("user {} is trying to create bluetoothZone", principal.getName());

        if(!dto.getType().equals(ZoneType.BLUETOOTH))
            throw new ConflictException("mismatch of zone type . Was: " +dto.getType());

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findById(dto.getTeamGroupId())
                .orElseThrow(() -> new NotFoundException("group not found; resource: id " + dto.getTeamGroupId()));

        if (!teamGroup.extractAdmins().contains(user)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroup.getId());
        }
        if (user.getAlertBluetoothZone() != null) {
            throw new ConflictException("user already has bluetooth rule; delete existing rule first; resource: rule id " + user.getAlertBluetoothZone().getId());
        }
        Set<AppUser> vulnerables = validateAndExtractVulnerablesForTeamGroup(dto.getVulnerables(), teamGroup);

        AlertBluetoothZone zone = bluetoothZoneService.createNew(
                user,
                teamGroup,
                dto.getRule(),
                vulnerables,
                dto.getName() != null ? dto.getName() : user.getUserInfo().getName() + "'s"
        );

        notificator.notifyUsers(
                teamGroup.extractUsers(),
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(teamGroup.getId())
                        .build()
                );

        return ResponseAlertBluetoothZoneDto.of(zone);
    }

    @GetMapping("/{id}")
    public ResponseAlertBluetoothZoneDto findById(@PathVariable Long id, Principal principal) {

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AlertBluetoothZone bz = bluetoothZoneService.findById(id)
                .orElseThrow(() -> new NotFoundException("zone not found; resource: id " + id));

        if (bz.getTeamGroup().extractUsers().stream().noneMatch(u -> u.equals(user))) {
            throw new AccessDeniedException("user does not have permissions for zone; resource: zone id" + id);
        }

        return ResponseAlertBluetoothZoneDto.of(bz);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @GetMapping("/my-bluetooth-zone")
    public ResponseAlertBluetoothZoneDto findById(Principal principal) {
        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AlertBluetoothZone bz = user.getAlertBluetoothZone();

        return bz == null ? null : ResponseAlertBluetoothZoneDto.of(bz);
    }

    //todo add permisions and verifications
    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping
    public ResponseEntity deleteAlertBluetoothZone(Principal principal) {

        log.trace("user {} is trying to delete bluetooth zone",principal.getName());

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AlertBluetoothZone zone = user.getAlertBluetoothZone();

        long id = zone.getId();
        Set<AppUser> userList = zone.getTeamGroup().extractUsers();
        long tgId = zone.getTeamGroup().getId();
        bluetoothZoneService.delete(zone);

        notificator.notifyUsers(
                userList,
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(tgId)
                        .build()
        );

        return ResponseEntity.ok(id);
    }


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PutMapping("/edit-my-bluetooth-zone")
    public ResponseAlertBluetoothZoneDto editBluetoothZone
            (@Valid @RequestBody AddTeamGroupRuleDto dto, Errors errors, Principal principal){
        ValidationDto.handleErrors(errors);

        log.trace("user {} is trying to edit bluetoothZone", principal.getName());

        if(!dto.getType().equals(ZoneType.BLUETOOTH))
            throw new ConflictException("mismatch of zone type . Was: " +dto.getType());

        AppUser thisUser = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        if(thisUser.getAlertBluetoothZone()==null)
            throw new NotFoundException("error finding bluetoothZone for user: "+ principal.getName());

        AlertBluetoothZone currentUsersBluetoothZone = thisUser.getAlertBluetoothZone();

        TeamGroup teamGroupForCurrentBluetoothZone= currentUsersBluetoothZone.getTeamGroup();

        if (!teamGroupForCurrentBluetoothZone.extractAdmins().contains(thisUser)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroupForCurrentBluetoothZone.getId());
        }

        Set<AppUser> vulnerables = validateAndExtractVulnerablesForTeamGroup(dto.getVulnerables(),teamGroupForCurrentBluetoothZone);

        AlertBluetoothZone editedBluetoothZone = bluetoothZoneService
                .editExisting(currentUsersBluetoothZone.getId(), thisUser,teamGroupForCurrentBluetoothZone,dto.getRule(),vulnerables,dto.getName());

        notificator.notifyUsers(
                editedBluetoothZone.getTeamGroup().extractUsers(),
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(editedBluetoothZone.getTeamGroup().getId())
                        .build()
        );

        return ResponseAlertBluetoothZoneDto.of(editedBluetoothZone);
    }

    private Set<AppUser> validateAndExtractVulnerablesForTeamGroup(Set<Long> potentialVulnerables, TeamGroup teamGroup){
        Set<AppUser> vulnerables = new HashSet<>();
        for (Long vulnerable : potentialVulnerables) {
            AppUser currVulnerable = appUserService.findById(vulnerable)
                    .orElseThrow(() -> new NotFoundException("error finding user with id : " + vulnerable));

            if(!teamGroup.containsUser(currVulnerable))
                throw new ConflictException
                        ("user with id "+ currVulnerable.getId()+ "is not in team group "+ teamGroup.getId() + " group");

            for (AppUserTeamGroup appUserTeamGroup : teamGroup.getAppUserTeamGroups()) {
                if (appUserTeamGroup.getAppUser().equals(currVulnerable)) {
                    if (appUserTeamGroup.getGroupRole().equals(GroupRole.GUARDIAN))
                        throw new ConflictException("unable to add guardian: " +appUserTeamGroup.getId() + " to bluetoothZone");
                }
            }

            vulnerables.add(currVulnerable);
        }
        return vulnerables;
    }


}
