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

//    @Transactional
    public void delete(AlertBluetoothZone zone) {
        try {
            zone.getAppUserBluetoothZones().forEach(userZoneRepository::delete);

            System.out.println("????????????");
            alertBluetoothZoneRepository.deleteById(zone.getId());
        } catch (Exception e) {
            System.out.println("1");
            System.out.println("2");
            System.out.println("3");
            System.out.println("4");
            System.out.println("55555555555555555");
            System.out.println(e.getMessage());
        }

        System.out.println("111111111111111111");
        System.out.println(zone.getId());
        System.out.println(zone.getTeamGroup().getId());
        System.out.println(zone.getAssociatedUser().getId());
//        zone.getAppUserBluetoothZones().forEach(userZoneRepository::delete);
//        alertBluetoothZoneRepository.delete(zone);
    }
}
