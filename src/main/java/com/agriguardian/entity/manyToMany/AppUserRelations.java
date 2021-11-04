package com.agriguardian.entity.manyToMany;

import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Relation;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "app_users_relations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AppUserRelations {
    @Id
    @SequenceGenerator(name = "appUsersRelationsSequence", sequenceName = "app_users_relations_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appUsersRelationsSequence")
    private long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="controller_id")
    private AppUser controller;

    @ManyToOne(optional = false)
    @JoinColumn(name="follower_id")
    private AppUser userFollower;

    @Enumerated(EnumType.STRING)
    private Relation relation;
}
