package com.agriguardian.service;

import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.exception.InternalErrorException;
import com.agriguardian.exception.NotFoundException;
import com.agriguardian.repository.AlertBluetoothZoneRepository;
import com.agriguardian.repository.AppUserBluetoothZoneRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AlertBluetoothZoneService {
    private final AlertBluetoothZoneRepository alertBluetoothZoneRepository;
    private final AppUserBluetoothZoneRepository userZoneRepository;

    @Transactional
    public AlertBluetoothZone createNew(AppUser anchor, TeamGroup group, ZoneRule rule, Set<AppUser> vulnerables, String name) {

        AlertBluetoothZone zone = AlertBluetoothZone.builder()
                .rule(rule)
                .name(name)
                .build();
        zone.addAnchorUser(anchor);
        zone.addTeamGroup(group);
        try{
            AlertBluetoothZone savedZone = alertBluetoothZoneRepository.save(zone);
            vulnerables.forEach(v -> {
                AppUserBluetoothZone userZone = savedZone.addVulnerable(v);
                userZoneRepository.save(userZone);
            });

            return savedZone;
        }
        catch (Exception e){
            log.error("[createNew] failed to create new bluetoothZone {}; rsn: {}", zone, e.getMessage());
            throw new InternalErrorException("failed to create new bluetoothZone; rsn: " + e.getMessage());
        }
    }

    @Transactional
    public AlertBluetoothZone editExisting
            (Long id,AppUser anchor, TeamGroup group, ZoneRule rule, Set<AppUser> vulnerables, String name) {


            AlertBluetoothZone currentZone = alertBluetoothZoneRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("zone with id: " + id + " does not exists"));

            currentZone.setRule(rule);
            Optional.ofNullable(name).ifPresent(currentZone::setName);
            currentZone.addAnchorUser(anchor);
            currentZone.addTeamGroup(group);

            currentZone.emptySet();

        try {
            AlertBluetoothZone savedZone = alertBluetoothZoneRepository.save(currentZone);

            vulnerables.forEach(v -> {
                AppUserBluetoothZone userZone = savedZone.addVulnerable(v);
                userZoneRepository.save(userZone);
            });

            return savedZone;
        }
        catch (Exception e){
            log.error("[editExisting] failed to edit bluetoothZone {}; rsn: {}", currentZone, e.getMessage());
            throw new InternalErrorException("failed to edit bluetoothZone; rsn: " + e.getMessage());
        }
    }

    public void delete(AlertBluetoothZone zone) {
        zone.getAssociatedUser().setAlertBluetoothZone(null);
        zone.getTeamGroup().getAlertBluetoothZones().remove(zone);
        try {
            alertBluetoothZoneRepository.delete(zone);
        }
        catch (Exception e){
            log.error("[delete] failed to delete bluetoothZone {}; rsn: {}", zone, e.getMessage());
            throw new InternalErrorException("failed to delete bluetoothZone; rsn: " + e.getMessage());
        }
    }

    public Optional<AlertBluetoothZone> findById(Long id) {
        try {
            return alertBluetoothZoneRepository.findById(id);
        }
        catch (Exception e){
            log.error("[findById] failed to retrieve a bluetoothZone; rsn: {}", e.getMessage());
            throw new InternalErrorException("failed to retrieve a bluetoothZone. rsn: " + e.getMessage());
        }
    }
}
