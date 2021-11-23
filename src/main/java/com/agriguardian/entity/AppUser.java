package com.agriguardian.entity;

import com.agriguardian.entity.manyToMany.AppUserBluetoothZone;
import com.agriguardian.entity.manyToMany.AppUserGeoZone;
import com.agriguardian.entity.manyToMany.AppUserRelations;
import com.agriguardian.entity.manyToMany.AppUserTeamGroup;
import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Restrictions;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppUser {
    @Id
    @SequenceGenerator(name = "appUsersSequence", sequenceName = "app_users_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appUsersSequence")
    private long id;
    @Column(name = "user_name",unique = true)
    private String username;
    private String password;
    private String otp;
    @Column(name = "refresh_token",unique = true)
    private String refreshToken;
    @Column(name = "fcm_token")
    private String fcmToken;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserInfo userInfo;
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CreditCard card;
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private TeamGroup teamGroup;

    @Column(name = "created_on")
    private long createdOnMs;
    @Column(name = "updated_on")
    private long updatedOnMs;
    @Column(name = "otp_created_on")
    private long otpCreatedOnMs;
    @Column(name = "rt_created_on")
    private long rtCreatedOnMs;
    @Column(name = "mac_address")
    private String macAddress;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    private Restrictions restrictions;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private Set<AppUserTeamGroup> appUserTeamGroups;

    @OneToOne(mappedBy = "associatedUser", cascade = CascadeType.ALL)
    private AlertBluetoothZone alertBluetoothZone;

    @OneToMany(mappedBy = "appUser",cascade = CascadeType.ALL)
    private Set<AppUserBluetoothZone> appUserBluetoothZones;
    @OneToMany(mappedBy = "appUser",cascade = CascadeType.ALL)
    private Set<AppUserGeoZone> appUserGeoZones;
    @OneToMany(mappedBy = "controller",cascade = CascadeType.ALL)
    private Set<AppUserRelations> relationToUsers;
    @OneToMany(mappedBy = "userFollower",cascade = CascadeType.ALL)
    private Set<AppUserRelations> usersRelationToMe;


    public void addUserInfo(UserInfo ui) {
        this.setUserInfo(ui);
        ui.setAppUser(this);
    }

    public void addSubscription(Subscription subscription) {
        this.setSubscription(subscription);
        subscription.setAppUser(this);
    }

    public void addCreditCard(CreditCard cc) {
        if (cc == null) return;

        this.setCard(cc);
        cc.setAppUser(this);
    }

    public void createNewAlertBluetoothZone(AlertBluetoothZone zone) {
        this.setAlertBluetoothZone(zone);
        zone.setAssociatedUser(this);
    }

    public AppUserBluetoothZone bindToAlertBluetoothZone(AlertBluetoothZone zone) {
        return zone.addVulnerable(this);
    }


    public AppUserTeamGroup addTeamGroup(TeamGroup tg, GroupRole role) {
        if (appUserTeamGroups == null) {
            appUserTeamGroups = new HashSet();
        }

        Optional<AppUserTeamGroup> savedBind = appUserTeamGroups.stream()
                .filter(appUserTeamGroup -> appUserTeamGroup.storesBind(this, tg)).findAny();

        AppUserTeamGroup userTeamGroupBind;
        if (savedBind.isPresent()) {
            userTeamGroupBind = savedBind.get();
            userTeamGroupBind.setGroupRole(role);
        } else {
            userTeamGroupBind = AppUserTeamGroup.builder()
                    .teamGroup(tg)
                    .appUser(this)
                    .groupRole(role)
                    .build();

            if (tg.getAppUserTeamGroups() == null) {
                tg.setAppUserTeamGroups(new HashSet());
            }
            appUserTeamGroups.add(userTeamGroupBind);
            tg.getAppUserTeamGroups().add(userTeamGroupBind);
        }

        return userTeamGroupBind;
    }

    public Long getOwnGroup() {
        return teamGroup == null ? null : teamGroup.getId();
    }

    public List<AlertGeoZone> extractAlertGeoZones() {
        if (appUserGeoZones == null) {
            return Collections.emptyList();
        } else {
            return appUserGeoZones.stream().map(AppUserGeoZone::getAlertGeoZone).collect(Collectors.toList());
        }
    }

    public void editUser(AppUser editedUser){
        Optional.ofNullable(editedUser.getCard()).ifPresent(creditCard -> this.setCard(creditCard));
        this.setUsername(editedUser.getUsername());
        this.userInfo.editUserInfo(editedUser.getUserInfo());
    }
}
