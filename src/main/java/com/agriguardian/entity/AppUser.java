package com.agriguardian.entity;

import com.agriguardian.enums.GroupRole;
import com.agriguardian.enums.Status;
import com.agriguardian.enums.UserRole;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@Builder
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

    private String name;
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

    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "appUser")
    private Set<AppUserTeamGroup> appUserTeamGroups = new HashSet<>();


    public void addUserInfo(UserInfo ui) {
        this.setUserInfo(ui);
        ui.setAppUser(this);
    }

    public void addSubscription(Subscription subscription) {
        this.setSubscription(subscription);
        subscription.setAppUser(this);
    }

    public void  addCreditCard(CreditCard cc) {
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
}
