package com.agriguardian.entity;

import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "team_groups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TeamGroup {
    @Id
    @SequenceGenerator(name = "teamGroupsSequence", sequenceName = "team_groups_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teamGroupsSequence")
    private long id;
    private String name;

    @Column(name = "guardian_code")
    private String guardianInvitationCode;
    @Column(name = "vulnerable_code")
    private String vulnerableInvitationCode;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    @OneToMany(mappedBy = "teamGroup")
    private Set<AppUserTeamGroup> appUserTeamGroups;

    @OneToMany(mappedBy = "teamGroup")
    private Set<AlertBluetoothZone> alertBluetoothZones;

    @OneToMany(mappedBy = "teamGroup")
    private Set<AlertGeoZone> alertGeoZones;


    public void addAlertBluetoothZone(AlertBluetoothZone zone) {
        alertBluetoothZones.add(zone);
        zone.setTeamGroup(this);
    }

    public void addAlertGeoZone(AlertGeoZone zone) {
        alertGeoZones.add(zone);
        zone.setTeamGroup(this);
    }
}
