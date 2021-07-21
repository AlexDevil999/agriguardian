package com.agriguardian.controller;

import com.agriguardian.dto.AddTeamGroupRuleDto;
import com.agriguardian.dto.ResponseAlertGeoZoneDto;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AlertGeoZoneService;
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

import javax.validation.Valid;
import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/alert-geo-zones")
public class AlertGeoZoneController {
    private final AppUserService appUserService;
    private final TeamGroupService teamGroupService;
    private final AlertGeoZoneService zoneService;


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping
    public ResponseAlertGeoZoneDto addAlertBluetoothZone(@Valid @RequestBody AddTeamGroupRuleDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        if (dto.getFigureType() == null) throw new BadRequestException("field 'figureType' may not be null");
        if (dto.getBorders() == null && dto.getBorders().isEmpty())  throw new BadRequestException("field 'borders' should contains at least 3 points");

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findById(dto.getTeamGroupId())
                .orElseThrow(() -> new NotFoundException("group not found; resource: id " + dto.getTeamGroupId()));
        if (!teamGroup.extractAdmins().contains(user)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroup.getId());
        }

        Set<AppUser> vulnerables = teamGroup.extractVulnerables().stream().filter(u -> dto.getVulnerables().contains(u.getId())).collect(Collectors.toSet());

        if (vulnerables.size() != dto.getVulnerables().size()) {
            dto.getVulnerables().removeAll(vulnerables.stream().map(AppUser::getId).collect(Collectors.toSet()));
            throw new BadRequestException("the resource does not belong to the group; user id " + dto.getVulnerables());
        }

        return ResponseAlertGeoZoneDto.of(
                zoneService.createNew(
                        dto.getRule(),
                        dto.getCenterLat(),
                        dto.getCenterLon(),
                        dto.getFigureType(),
                        dto.getRadius(),
                        teamGroup,
                        vulnerables,
                        dto.getBorders()
                )
        );
    }

    //todo add permisions and verifications
    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAlertBluetoothZone(@PathVariable Long id,
                                                   Principal principal) {
        AlertGeoZone zone = zoneService.findById(id).orElseThrow(() -> new NotFoundException("zone not found; resource " + id));

        if (!zone.getTeamGroup().extractAdmins().stream().anyMatch(u -> u.getUsername().equals(principal.getName()))) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + zone.getTeamGroup().getId());
        }

        long zoneId = zone.getId();
        zoneService.delete(zone);

        return ResponseEntity.ok(zoneId);
    }
}