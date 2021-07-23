package com.agriguardian.service;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.AlertGeoZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.repository.AlertGeoZoneRepository;
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


    @Transactional
    public AlertGeoZone createNew(
            ZoneRule rule,
            Double centerLat,
            Double centerLon,
            Figure figure,
            Integer radius,
            TeamGroup group,
            Set<AppUser> vulnerables,
            List<Point> borders) {

        AlertGeoZone zone = AlertGeoZone.builder()
                .rule(rule)
                .centerLat(centerLat)
                .centerLon(centerLon)
                .figureType(figure)
                .radius(radius)
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

    public void delete(AlertGeoZone zone) {
        zone.getTeamGroup().getAlertBluetoothZones().remove(zone);
        zoneRepository.delete(zone);
    }

    public Optional<AlertGeoZone> findById(Long id) {
        return zoneRepository.findById(id);
    }
}
