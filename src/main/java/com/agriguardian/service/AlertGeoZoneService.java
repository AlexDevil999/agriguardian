package com.agriguardian.service;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AlertGeoZoneRepository;
import com.agriguardian.repository.AppUserGeoZoneRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AlertGeoZoneService {
    private final AlertGeoZoneRepository zoneRepository;
    private final AppUserGeoZoneRepository appUserGeoZoneRepository;


    @Transactional
    public AlertGeoZone createNew(
            ZoneRule rule,
            Double centerLat,
            Double centerLon,
            Figure figure,
            Integer radius,
            TeamGroup group,
            Set<AppUser> vulnerables,
            List<Point> borders,
            String name) {

        AlertGeoZone zone = AlertGeoZone.builder()
                .rule(rule)
                .centerLat(centerLat)
                .centerLon(centerLon)
                .figureType(figure)
                .radius(radius)
                .name(name)
                .build();


        zone.addTeamGroup(group);
//        AlertBluetoothZone savedZone = alertBluetoothZoneRepository.save(zone);
//        zone.addVulnerable()

        vulnerables.forEach(v -> {
            AppUserGeoZone userZone = zone.addVulnerable(v);
//            userZoneRepository.save(userZone);
        });

        zone.bordersByPoints(borders);

        return zoneRepository.save(zone);
    }

    @Transactional
    public AlertGeoZone editExisting
            (Long id,Double centerLat,
             Double centerLon,Figure figure,
             Integer radius, TeamGroup group,
             List<Point> borders, ZoneRule rule,
             Set<AppUser> vulnerables, String name) {

        AlertGeoZone currentZone = zoneRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("zone with id: "+id+" does not exists"));

        currentZone.setRule(rule);
        Optional.ofNullable(name).ifPresent(currentZone::setName);


        if(figure.equals(Figure.CIRCLE)){
            currentZone.setCenterLat(centerLat);
            currentZone.setCenterLon(centerLon);
            currentZone.setFigureType(figure);
            currentZone.setRadius(radius);
        }
        else if(figure.equals(Figure.POLYGON)){
            currentZone.bordersByPoints(borders);
            currentZone.setFigureType(figure);
        }

        currentZone.emptyVulnerables();

        AlertGeoZone savedZone = zoneRepository.save(currentZone);


        vulnerables.forEach(v -> {
            AppUserGeoZone userZone = savedZone.addVulnerable(v);
            appUserGeoZoneRepository.save(userZone);
        });

        return savedZone;
    }


    public void delete(AlertGeoZone zone) {
        zone.getTeamGroup().getAlertBluetoothZones().remove(zone);
        zoneRepository.delete(zone);
    }

    public Optional<AlertGeoZone> findById(Long id) {
        return zoneRepository.findById(id);
    }
}
