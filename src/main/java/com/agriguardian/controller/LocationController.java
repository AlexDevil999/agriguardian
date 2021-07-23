package com.agriguardian.controller;

import com.agriguardian.dto.GeoMonitoringDto;
import com.agriguardian.dto.ViolationDto;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.exception.BadRequestException;
import com.agriguardian.service.AppUserService;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {
    private final AppUserService userService;
    private final UserMonitor userMonitoringService;


    @PostMapping("/report")
    public ViolationDto notifyLocation(@Valid @RequestBody GeoMonitoringDto geo, Errors errors, Principal principal) {
        //todo add storing of history
        ValidationDto.handleErrors(errors);
        if (geo.getLocations().isEmpty()) {
            throw new BadRequestException("field 'locations' may not be empty");
        }

        AppUser user = userService.findByUsernameOrThrowNotFound(principal.getName());

        Collections.sort(geo.getLocations());
        List<AlertGeoZone> violatedZones = userMonitoringService.monitor(user, geo.getLocations().get(geo.getLocations().size() - 1).getPoint());

        List<Long> zoneIds = violatedZones.stream().map(AlertGeoZone::getId).collect(Collectors.toList());
        return ViolationDto.builder()
                .userId(user.getId())
                .violatedZones(zoneIds)
                .build();
    }
}
