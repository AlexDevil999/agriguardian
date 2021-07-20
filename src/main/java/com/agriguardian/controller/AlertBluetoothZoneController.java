package com.agriguardian.controller;

import com.agriguardian.dto.AddTeamGroupRuleDto;
import com.agriguardian.dto.ResponseAlertBluetoothZoneDto;
import com.agriguardian.dto.appUser.ResponseUserDto;
import com.agriguardian.dto.teamGroup.JoinTeamGroupDto;
import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AlertBluetoothZoneService;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.naming.ConfigurationException;
import javax.validation.Valid;
import java.security.Principal;
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



    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping
    public ResponseAlertBluetoothZoneDto addAlertBluetoothZone(@Valid @RequestBody AddTeamGroupRuleDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findById(dto.getTeamGroupId())
                .orElseThrow(() -> new NotFoundException("group not found; resource: id " + dto.getTeamGroupId()));
        if (!teamGroup.extractAdmins().contains(user)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroup.getId());
        }
        if (user.getAlertBluetoothZone() != null) {
            throw new ConflictException("user already has bluetooth rule; delete existing rule first; resource: rule id " + user.getAlertBluetoothZone().getId());
        }
        Set<AppUser> vulnerables = teamGroup.extractVulnerables().stream().filter(u -> dto.getVulnerables().contains(u.getId())).collect(Collectors.toSet());

        if (vulnerables.size() != dto.getVulnerables().size()) {
            dto.getVulnerables().removeAll(vulnerables.stream().map(AppUser::getId).collect(Collectors.toSet()));
            throw new BadRequestException("the resource does not belong to the group; user id " + dto.getVulnerables());
        }

        return ResponseAlertBluetoothZoneDto.of(
                bluetoothZoneService.createNew(
                        user,
                        teamGroup,
                        dto.getRule(),
                        vulnerables
                )
        );
    }

    //todo add permisions and verifications
    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping
    public ResponseEntity deleteAlertBluetoothZone(Principal principal) {

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AlertBluetoothZone zone = user.getAlertBluetoothZone();

        long id = zone.getId();
        bluetoothZoneService.delete(zone);


        return ResponseEntity.ok(id);
    }

}
