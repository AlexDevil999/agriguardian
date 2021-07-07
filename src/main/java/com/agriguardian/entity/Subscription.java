package com.agriguardian.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@Builder
public class Subscription {
    @Id
    @SequenceGenerator(name = "subscriptionsSequence", sequenceName = "subscriptions_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscriptionsSequence")
    private long id;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
}
