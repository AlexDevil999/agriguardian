package com.agriguardian.entity;

import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.enums.ZoneRule;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "bluetooth_zones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AlertBluetoothZone {
    @Id
    @SequenceGenerator(name = "alertBluetoothZoneSequence", sequenceName = "bluetooth_zones_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alertBluetoothZoneSequence")
    private long id;
    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser associatedUser;
    @Enumerated(EnumType.STRING)
    private ZoneRule rule;
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroup teamGroup;

    @OneToMany(mappedBy = "alertBluetoothZone",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<AppUserBluetoothZone> appUserBluetoothZones;


    public void addAnchorUser(AppUser user) {
        user.createNewAlertBluetoothZone(this);
    }

    public void addTeamGroup(TeamGroup teamGroup) {
        teamGroup.addAlertBluetoothZone(this);
    }

    public AppUserBluetoothZone addVulnerable(AppUser user) {
        if (appUserBluetoothZones == null) appUserBluetoothZones = new HashSet<>();

        final AppUserBluetoothZone userBluetoothZone =
                AppUserBluetoothZone.builder()
                        .appUser(user)
                        .alertBluetoothZone(this)
                        .build();
        Optional<AppUserBluetoothZone> fromSet = appUserBluetoothZones.stream()
                .filter(userZone -> userZone.equals(userBluetoothZone))
                .findAny();

        if (fromSet.isPresent()) {
            return fromSet.get();
        } else {
            appUserBluetoothZones.add(userBluetoothZone);
            user.getAppUserBluetoothZones().add(userBluetoothZone); //todo check this
            return userBluetoothZone;
        }
    }

    public void emptySet(){
        appUserBluetoothZones.removeAll(appUserBluetoothZones);
    }

    public Set<AppUser> extractVulnerables() {
        if (appUserBluetoothZones == null) return new HashSet<>();

        return appUserBluetoothZones.stream().map(AppUserBluetoothZone::getAppUser).collect(Collectors.toSet());
    }

    public Set<Long> extractIdsOfVulnerables() {
        return extractVulnerables().stream().map(AppUser::getId).collect(Collectors.toSet());
    }
}
