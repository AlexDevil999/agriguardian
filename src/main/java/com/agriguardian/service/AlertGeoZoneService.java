package com.agriguardian.service;

import com.agriguardian.domain.Point;
import com.agriguardian.entity.*;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.enums.Figure;
import com.agriguardian.enums.SchedulePeriod;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.exception.ConflictException;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AlertGeoZoneRepository;
import com.agriguardian.repository.AppUserGeoZoneRepository;
import com.agriguardian.repository.ZoneSchedulingRuleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AlertGeoZoneService {
    private final AlertGeoZoneRepository zoneRepository;
    private final AppUserGeoZoneRepository appUserGeoZoneRepository;
    private final ZoneSchedulingRuleRepository zoneSchedulingRuleRepository;

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
            String name,
            List<ZoneSchedulingRule> zoneSchedulingRules) {

        AlertGeoZone zone = AlertGeoZone.builder()
                .rule(rule)
                .centerLat(centerLat)
                .centerLon(centerLon)
                .figureType(figure)
                .radius(radius)
                .name(name)
                .build();

        if(!Optional.ofNullable(zoneSchedulingRules).isPresent()) {
            zoneSchedulingRules = new ArrayList<>();
            ZoneSchedulingRule zoneSchedulingRule = new ZoneSchedulingRule();
            zoneSchedulingRule.setSchedulePeriod(SchedulePeriod.CONSTANT);
            zoneSchedulingRules.add(zoneSchedulingRule);
        }

        zone.addTeamGroup(group);

        vulnerables.forEach(v -> {
            AppUserGeoZone userZone = zone.addVulnerable(v);
        });

        if(figure.equals(Figure.POLYGON))
            zone.bordersByPoints(borders);

        try {
            AlertGeoZone savedZone = zoneRepository.save(zone);
            zoneSchedulingRules.forEach(zoneSchedulingRule -> {
                zone.addSchedulingRule(zoneSchedulingRule);
                zoneSchedulingRule.setAlertGeoZone(savedZone);
                zoneSchedulingRuleRepository.save(zoneSchedulingRule);
            });
            return savedZone;
        }
        catch (Exception e){
            log.error("[createNew] failed to create new geoZone {}; rsn: {}", zone, e.getMessage());
            throw new InternalErrorException("failed to create new geoZone; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AlertGeoZone editExisting
            (Long id,Double centerLat,
             Double centerLon,Figure figure,
             Integer radius, TeamGroup group,
             List<Point> borders, ZoneRule rule,
             Set<AppUser> vulnerables, String name,
             List<ZoneSchedulingRule> zoneSchedulingRules) {

            AlertGeoZone currentZone = zoneRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("zone with id: " + id + " does not exists"));

            currentZone.setRule(rule);
            Optional.ofNullable(name).ifPresent(currentZone::setName);

            currentZone.emptyRules();
            zoneSchedulingRuleRepository.deleteByAlertGeoZoneId(currentZone.getId());

        if(!Optional.ofNullable(zoneSchedulingRules).isPresent()) {
            zoneSchedulingRules = new ArrayList<>();
            ZoneSchedulingRule zoneSchedulingRule = new ZoneSchedulingRule();
            zoneSchedulingRule.setSchedulePeriod(SchedulePeriod.CONSTANT);
            zoneSchedulingRules.add(zoneSchedulingRule);
        }

            zoneSchedulingRules.forEach(zoneSchedulingRule ->
                {currentZone.addSchedulingRule(zoneSchedulingRule);
                zoneSchedulingRule.setAlertGeoZone(currentZone);});
            zoneSchedulingRules.forEach(zoneSchedulingRule -> zoneSchedulingRule.setAlertGeoZone(currentZone));


            currentZone.setFigureType(figure);
            currentZone.emptyBorders();
            if (figure.equals(Figure.CIRCLE)) {
                currentZone.setCenterLat(centerLat);
                currentZone.setCenterLon(centerLon);
                currentZone.setRadius(radius);
            } else if (figure.equals(Figure.POLYGON)) {
                currentZone.bordersByPoints(borders);
            }

            currentZone.emptyVulnerables();
            appUserGeoZoneRepository.deleteAppUserGeoZoneByAlertGeoZoneId(currentZone.getId());

        try {
            AlertGeoZone savedZone = zoneRepository.save(currentZone);

            vulnerables.forEach(v -> {
                AppUserGeoZone userZone = savedZone.addVulnerable(v);
                appUserGeoZoneRepository.save(userZone);
            });


            return savedZone;
        }
        catch (Exception e){
            log.error("[editExisting] failed to edit geoZone {}; rsn: {}", currentZone, e.getMessage());
            throw new InternalErrorException("failed to edit geoZone; rsn: " + e.getMessage());
        }
    }


    public void delete(AlertGeoZone zone) {
        try {
            zone.getTeamGroup().getAlertGeoZones().remove(zone);
            zoneRepository.delete(zone);
        }
        catch (Exception e){
            log.error("[delete] failed to delete geoZone {}; rsn: {}", zone, e.getMessage());
            throw new InternalErrorException("failed to delete geoZone; rsn: " + e.getMessage());
        }
    }

    public Optional<AlertGeoZone> findById(Long id) {
        try {
            return zoneRepository.findById(id);
        }
        catch (Exception e){
            log.error("[findById] failed to retrieve a geoZone; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a geoZone. rsn: " + e.getMessage());
        }
    }

}
