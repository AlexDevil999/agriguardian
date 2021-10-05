package com.agriguardian.entity;

import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

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


    @OneToMany(mappedBy = "teamGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AppUserTeamGroup> appUserTeamGroups;

    @OneToMany(mappedBy = "teamGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AlertBluetoothZone> alertBluetoothZones;

    @OneToMany(mappedBy = "teamGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<AlertGeoZone> alertGeoZones;


    public void addAlertBluetoothZone(AlertBluetoothZone zone) {
        alertBluetoothZones.add(zone);
        zone.setTeamGroup(this);
    }

    public void addAlertGeoZone(AlertGeoZone zone) {
        alertGeoZones.add(zone);
        zone.setTeamGroup(this);
    }

    public Set<AppUser> extractUsers() {
        return appUserTeamGroups.stream().map(AppUserTeamGroup::getAppUser).collect(Collectors.toSet());
    }

    public Set<AppUser> extractAdmins() {
        return appUserTeamGroups.stream().filter(userGroup -> GroupRole.GUARDIAN == userGroup.getGroupRole())
                .map(AppUserTeamGroup::getAppUser).collect(Collectors.toSet());
    }

    public Set<AppUser> extractVulnerables() {
        return appUserTeamGroups.stream().filter(userGroup -> GroupRole.VULNERABLE == userGroup.getGroupRole())
                .map(AppUserTeamGroup::getAppUser).collect(Collectors.toSet());
    }

    public boolean containsUser(AppUser user) {
        return extractUsers().stream().anyMatch(groupUser -> groupUser.equals(user));
    }
}
