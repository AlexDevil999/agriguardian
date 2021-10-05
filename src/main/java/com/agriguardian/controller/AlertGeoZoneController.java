package com.agriguardian.controller;

import com.agriguardian.dto.*;
import com.agriguardian.entity.*;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public ResponseAlertGeoZoneDto addAlertGeoZone(@Valid @RequestBody AddTeamGroupRuleDto dto, Errors errors, Principal principal) {
        ValidationDto.handleErrors(errors);

        if (dto.getFigureType() == null) throw new BadRequestException("field 'figureType' may not be null");
        if (dto.getBorders() == null || dto.getBorders().size() < MIN_NUMBER_OF_VERTICES)
            throw new BadRequestException("field 'borders' should contains at least 3 points");

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

        AlertGeoZone zone = geoZoneServie.createNew(
                dto.getRule(),
                dto.getCenterLat(),
                dto.getCenterLon(),
                dto.getFigureType(),
                dto.getRadius(),
                teamGroup,
                vulnerables,
                dto.getBorders(),
                dto.getName() != null ? dto.getName() : user.getUserInfo().getName() + "'s"
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
    public ResponseAlertGeoZoneDto editBluetoothZone
            (@Valid @RequestBody EditGeoZoneDto dto, Errors errors, Principal principal){
        ValidationDto.handleErrors(errors);

        if(!dto.getType().equals(ZoneType.GEO))
            throw new ConflictException("mismatch of zone type . Was: " +dto.getType());

        AppUser thisUser = appUserService.findByUsernameOrThrowNotFound(principal.getName());

        if(thisUser.getAlertBluetoothZone()==null)
            throw new NotFoundException("error finding bluetoothZone for user: "+ principal.getName());

        AlertGeoZone currentUsersGeoZone = geoZoneServie.findById(dto.getGeoZoneId())
                .orElseThrow(() -> new NotFoundException("feo zone with id: "+dto.getGeoZoneId() + "was not found"));

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
                        dto.getRule(),vulnerables,dto.getName()
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

//        Set<AlertGeoZone> zonesUnderProtection = user.getAppUserTeamGroups().stream()
        Map<Long, Set<AlertGeoZone>> zonesUnderProtection = user.getAppUserTeamGroups().stream()
                .filter(utg -> GroupRole.GUARDIAN == utg.getGroupRole())
//                .flatMap(utg -> utg.getTeamGroup().getAlertGeoZones().stream())
                .collect(Collectors.toMap(utg -> utg.getTeamGroup().getId(), utg -> utg.getTeamGroup().getAlertGeoZones()));

//        return zonesUnderProtection.stream()
//                .map(ResponseAlertGeoZoneDto::of)
//                .collect(Collectors.toList());


        Map<Long, Set<ResponseAlertGeoZoneDto>> response =
                zonesUnderProtection.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream().map(ResponseAlertGeoZoneDto::of).collect(Collectors.toSet())
                        ));

        return response;
    }

    //todo add permisions and verifications
    @PreAuthorize("hasAuthority('USER_MASTER')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAlertGeoZone(@PathVariable Long id, Principal principal) {
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
