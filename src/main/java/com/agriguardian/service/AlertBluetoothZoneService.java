package com.agriguardian.service;

import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.enums.ZoneRule;
import com.agriguardian.repository.AlertBluetoothZoneRepository;
import com.agriguardian.repository.AppUserBluetoothZoneRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AlertBluetoothZoneService {
    private final AlertBluetoothZoneRepository alertBluetoothZoneRepository;
    private final AppUserBluetoothZoneRepository userZoneRepository;


    @Transactional
    public AlertBluetoothZone createNew(AppUser anchor, TeamGroup group, ZoneRule rule, Set<AppUser> vulnerables) {
        AlertBluetoothZone zone = AlertBluetoothZone.builder()
                .rule(rule)
                .build();
        zone.addAnchorUser(anchor);
        zone.addTeamGroup(group);
        AlertBluetoothZone savedZone = alertBluetoothZoneRepository.save(zone);

        vulnerables.forEach(v -> {
            AppUserBluetoothZone userZone = savedZone.addVulnerable(v);
            userZoneRepository.save(userZone);
        });

        return savedZone;
    }

    public void delete(AlertBluetoothZone zone) {
            zone.getAssociatedUser().setAlertBluetoothZone(null);
            zone.getTeamGroup().getAlertBluetoothZones().remove(zone);
            alertBluetoothZoneRepository.delete(zone);
    }
}
