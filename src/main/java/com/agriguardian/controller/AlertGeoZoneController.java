package com.agriguardian.controller;

import com.agriguardian.dto.*;
import com.agriguardian.entity.*;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.ZoneType;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.service.AlertGeoZoneService;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.TeamGroupService;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/alert-geo-zones")
public class AlertGeoZoneController {
    private final AppUserService appUserService;
    private final TeamGroupService teamGroupService;
    private final AlertGeoZoneService geoZoneServie;
    private final Notificator notificator;
    private static final int MIN_NUMBER_OF_VERTICES = 3;


    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PostMapping
    public ResponseAlertGeoZoneDto addAlertGeoZone(@Valid @RequestBody AddGeoZoneDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);
        Optional.ofNullable(dto.getBorders()).ifPresent(points ->log.debug("[bordersAmount]: " + points.size()));

        log.trace("user {} is trying to create geoZone", principal.getName());

        if (dto.getFigureType() == null) throw new BadRequestException("field 'figureType' may not be null");

        if (dto.getFigureType().equals(Figure.POLYGON) &&
                (dto.getBorders() == null || dto.getBorders().size() < MIN_NUMBER_OF_VERTICES))
            throw new BadRequestException("field 'borders' should contains at least 3 points");

        if(dto.getFigureType().equals(Figure.CIRCLE) &&
                (dto.getCenterLon()==null||dto.getCenterLat()==null||dto.getRadius()==null))
            throw new BadRequestException("for circle long, lat and radius should be present");

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        TeamGroup teamGroup = teamGroupService.findById(dto.getTeamGroupId())
                .orElseThrow(() -> new NotFoundException("group not found; resource: id " + dto.getTeamGroupId()));
        if (!teamGroup.extractAdmins().contains(user)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroup.getId());
        }

        Set<AppUser> vulnerables = validateAndExtractVulnerablesForTeamGroup(dto.getVulnerables(), teamGroup);

        AlertGeoZone zone = geoZoneServie.createNew(
                dto.getRule(),
                dto.getCenterLat(),
                dto.getCenterLon(),
                dto.getFigureType(),
                dto.getRadius(),
                teamGroup,
                vulnerables,
                dto.getBorders(),
                dto.getName() != null ? dto.getName() : user.getUserInfo().getName() + "'s",
                dto.createZoneSchedulingRules()
        );

        notificator.notifyUsers(
                teamGroup.extractUsers(),
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(teamGroup.getId())
                        .build()
        );

        return ResponseAlertGeoZoneDto.of(zone);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @PutMapping("/edit-my-geo-zone")
    public ResponseAlertGeoZoneDto editGeoZone
            (@Valid @RequestBody EditGeoZoneDto dto, Errors errors, Principal principal){
        ValidationDto.handleErrors(errors);
        Optional.ofNullable(dto.getBorders()).ifPresent(points -> log.error("[bordersAmount]: " + points.size()));

        log.trace("user {} is trying to edit geoZone", principal.getName());

        if(!dto.getType().equals(ZoneType.GEO))
            throw new ConflictException("mismatch of zone type . Was: " +dto.getType());

        AppUser thisUser = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        AlertGeoZone currentUsersGeoZone = geoZoneServie.findById(dto.getGeoZoneId())
                .orElseThrow(() -> new NotFoundException("geo zone with id: "+dto.getGeoZoneId() + "was not found"));

        TeamGroup teamGroupForCurrentGeoZone = currentUsersGeoZone.getTeamGroup();

        if (!teamGroupForCurrentGeoZone.extractAdmins().contains(thisUser)) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + teamGroupForCurrentGeoZone.getId());
        }

        Set<AppUser> vulnerables = validateAndExtractVulnerablesForTeamGroup(dto.getVulnerables(),teamGroupForCurrentGeoZone);

        AlertGeoZone editedGeoZone = geoZoneServie
                .editExisting(
                        dto.getGeoZoneId(),dto.getCenterLat(),
                        dto.getCenterLon(),dto.getFigureType(),dto.getRadius(),
                        teamGroupForCurrentGeoZone,dto.getBorders(),
                        dto.getRule(),vulnerables,dto.getName(),
                        dto.createZoneSchedulingRules()
                );

        notificator.notifyUsers(
                editedGeoZone.getTeamGroup().extractUsers(),
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(editedGeoZone.getTeamGroup().getId())
                        .build()
        );

        return ResponseAlertGeoZoneDto.of(editedGeoZone);
    }

    @GetMapping("/{id}")
    public ResponseAlertGeoZoneDto findById(@PathVariable Long id, Principal principal) {

        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());
        AlertGeoZone gz = geoZoneServie.findById(id)
                .orElseThrow(() -> new NotFoundException("zone not found; resource: id " + id));

        if (gz.getTeamGroup().extractUsers().stream().noneMatch(u -> u.equals(user))) {
            throw new AccessDeniedException("user does not have permissions for zone; resource: zone id" + id);
        }

        return ResponseAlertGeoZoneDto.of(gz);
    }

    @PreAuthorize("hasAuthority('USER_MASTER')")
    @GetMapping("/my-geo-zones")
    public  Map<Long, Set<ResponseAlertGeoZoneDto>> findZonesUnderProtection(Principal principal) {
        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        Map<Long, Set<AlertGeoZone>> zonesUnderProtection = user.getAppUserTeamGroups().stream()
                .filter(utg -> GroupRole.GUARDIAN == utg.getGroupRole())
                .collect(Collectors.toMap(utg -> utg.getTeamGroup().getId(), utg -> utg.getTeamGroup().getAlertGeoZones()));


        Map<Long, Set<ResponseAlertGeoZoneDto>> response =
                zonesUnderProtection.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().map(ResponseAlertGeoZoneDto::of).collect(Collectors.toSet())
                        ));

        return response;
    }

    @GetMapping("/group-geo-zones/{id}")
    public  Set<ResponseAlertGeoZoneDto> findZonesForGroup(@PathVariable Long id, Principal principal) {
        AppUser user = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        TeamGroup thisTeamGroup = teamGroupService.findById(id).orElseThrow((()-> new NotFoundException("no teamGroup with id " + id)));

        if(user.getAppUserTeamGroups().stream().noneMatch(appUserTeamGroup -> appUserTeamGroup.getTeamGroup().equals(thisTeamGroup)))
            throw new NotFoundException("user: " + user.getId() + "not in teamGroup: " + id);

        Set<ResponseAlertGeoZoneDto> response =
                thisTeamGroup.getAlertGeoZones().stream().map(ResponseAlertGeoZoneDto::of).collect(Collectors.toSet());

        return response;
    }

    //todo add permisions and verifications
    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAlertGeoZone(@PathVariable Long id, Principal principal) {

        log.trace("user {} is trying to delete geoZone", principal.getName());

        AlertGeoZone zone = geoZoneServie.findById(id).orElseThrow(() -> new NotFoundException("zone not found; resource " + id));

        if (zone.getTeamGroup().extractAdmins().stream().noneMatch(u -> u.getUsername().equals(principal.getName()))) {
            throw new AccessDeniedException("user does not have rights on recourse: teamGroup " + zone.getTeamGroup().getId());
        }

        long zoneId = zone.getId();
        Set<AppUser> userList = zone.getTeamGroup().extractUsers();
        long tgId = zone.getTeamGroup().getId();
        geoZoneServie.delete(zone);

        notificator.notifyUsers(
                userList,
                MessageDto.builder()
                        .event(EventType.TEAM_GROUP_UPDATED)
                        .groupId(tgId)
                        .build()
        );

        return ResponseEntity.ok(zoneId);
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
                        throw new ConflictException("unable to add guardian: " +appUserTeamGroup.getId() + " to geoZone");
                }
            }

            vulnerables.add(currVulnerable);
        }
        return vulnerables;
    }
}
