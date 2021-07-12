package com.agriguardian.entity;

import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    @Column(name = "user_name")
    private String username;
    private String password;
    private String otp;
    @Column (name = "refresh_token")
    private String refreshToken;

    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserInfo userInfo;
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Subscription subscription;
    @OneToOne(mappedBy = "appUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CreditCard card;
    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private TeamGroup teamGroup;

    @Column(name = "created_on")
    private long createdOnMs;
    @Column(name = "updated_on")
    private long updatedOnMs;
    @Column(name = "otp_created_on")
    private long otpCreatedOnMs;
    @Column(name = "rt_created_on")
    private long rtCreatedOnMs;

    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "appUser")
    private Set<AppUserTeamGroup> appUserTeamGroups;


    public void addUserInfo(UserInfo ui) {
        this.setUserInfo(ui);
        ui.setAppUser(this);
    }

    public void addSubscription(Subscription subscription) {
        this.setSubscription(subscription);
        subscription.setAppUser(this);
    }

    public void  addCreditCard(CreditCard cc) {
        if (cc == null) return;

        this.setCard(cc);
        cc.setAppUser(this);
    }

    public AppUserTeamGroup buildTeamGroupLink(TeamGroup teamGroup, GroupRole role) {
        return AppUserTeamGroup.builder()
                .teamGroup(teamGroup)
                .appUser(this)
                .groupRole(role)
                .build();
    }

//    public TeamGroup createTeamGroup() {
//        if (teamGroup != null) {
//            throw new BadRequestException("user " + username + "already have group (id " + teamGroup.getId() + ")");
//        }
//        TeamGroup tg = TeamGroup.builder()
//                .vulnerableInvitationCode(RandomCodeGenerator.generateInvitationCode())
//                .guardianInvitationCode(RandomCodeGenerator.generateInvitationCode())
//                .name(getUserInfo().getName() + "'s group")
//                .owner(this)
//                .appUserTeamGroups(new HashSet())
//                .build();
//
//        return addTeamGroup(tg, GroupRole.GUARDIAN);
//    }

    public AppUserTeamGroup addTeamGroup(TeamGroup tg, GroupRole role) {
        AppUserTeamGroup teamGroup = AppUserTeamGroup.builder()
                .teamGroup(tg)
                .appUser(this)
                .groupRole(role)
                .build();

        if (appUserTeamGroups == null) {
            appUserTeamGroups = new HashSet();
        }

        if (tg.getAppUserTeamGroups() == null) {
            tg.setAppUserTeamGroups(new HashSet());
        }
        appUserTeamGroups.add(teamGroup);
        tg.getAppUserTeamGroups().add(teamGroup);

        return teamGroup;
    }

    public Map<Long, GroupRole> defineTeamGroups() {
        return appUserTeamGroups == null ? new HashMap<>() :
                appUserTeamGroups.stream()
                .collect(Collectors.toMap(
                value -> value.getTeamGroup().getId(),
                        AppUserTeamGroup::getGroupRole));
    }

    public Long getOwnGroup() {
        return teamGroup == null ? null : teamGroup.getId();
    }
}
