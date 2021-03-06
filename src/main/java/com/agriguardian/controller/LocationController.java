package com.agriguardian.controller;

import com.agriguardian.dto.GeoMonitoringDto;
import com.agriguardian.dto.MessageDto;
import com.agriguardian.dto.ViolationDto;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.EventType;
import com.agriguardian.enums.Restrictions;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.service.AppUserService;
import com.agriguardian.service.interfaces.Notificator;
import com.agriguardian.service.interfaces.UserMonitor;
import com.agriguardian.util.ValidationDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    
    private final AppUserService userService;
    private final UserMonitor userMonitoringService;
    private final Notificator notificator;


    @PostMapping("/report")
    public ViolationDto notifyLocation(@Valid @RequestBody GeoMonitoringDto geo, Errors errors, Principal principal) {
        //todo add storing of history
        ValidationDto.handleErrors(errors);
        log.error("amount of coordinates: " + geo.getLocations().size());
        AppUser user = userService.findByUsernameOrThrowNotFound(principal.getName());

        if(Restrictions.cannotSendGpsData.equals(user.getRestrictions())){
            log.debug("beacon: "+ principal.getName() + "may not send GPS data");

            return ViolationDto.builder()
                    .userId(user.getId())
                    .violatedZones(Arrays.asList())
                    .build();
        }

        if (geo.getLocations().isEmpty()) {
            throw new BadRequestException("field 'locations' may not be empty");
        }

        log.debug("[notifyLocation] {} notifies location {}", user.getUsername(), geo);

        List<AlertGeoZone> violatedZones = userMonitoringService.monitor(user, geo.findLastLocation().getPoint());
        userService.setUserLocationData(user,geo.findLastLocation());

        log.trace("violatedZones for user {} has been extracted", principal.getName());

        //todo add notificaton and storing of user when he/she changes state
        violatedZones.forEach(zone -> {
            notificator.notifyUsers(
                    zone.getTeamGroup().extractAdmins(),
                    MessageDto.builder()
                            .userId(String.valueOf(user.getId()))
                            .userFullName(user.getUserInfo().getName())
                            .event(EventType.USER_VIOLATION.name())
                            .violatedZoneId(String.valueOf(zone.getId()))
                            .violatedZoneName(zone.getName())
                            .time(String.valueOf(geo.findLastLocation().getTime()))
                            .lat(String.valueOf(geo.findLastLocation().getPoint().getLat()))
                            .lon(String.valueOf(geo.findLastLocation().getPoint().getLon()))
                            .violatedZoneRule(zone.getRule().name())
                            .build()
            );
        });

        List<Long> zoneIds = violatedZones.stream().map(AlertGeoZone::getId).collect(Collectors.toList());
        return ViolationDto.builder()
                .userId(user.getId())
                .violatedZones(zoneIds)
                .build();
    }
}
