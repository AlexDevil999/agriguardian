package com.agriguardian.entity;

import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.enums.ZoneRule;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

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
    private AppUser appUser;
    @Enumerated(EnumType.STRING)
    private ZoneRule rule;

    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroup teamGroup;

    @OneToMany(mappedBy = "alertBluetoothZone")
    private Set<AppUserBluetoothZone> appUserBluetoothZones;


    public void addAnchorUser(AppUser user) {
        user.addAlertBluetoothZone(this);
    }

    public void addTeamGroup(TeamGroup teamGroup) {
        teamGroup.addAlertBluetoothZone(this);
    }
}
