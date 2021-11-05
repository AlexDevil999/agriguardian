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
@IdClass(RelationId.class)
public class AppUserRelations {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name="controller_id")
    private AppUser controller;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name="follower_id")
    private AppUser userFollower;

    @Enumerated(EnumType.STRING)
    private Relation relation;
}
