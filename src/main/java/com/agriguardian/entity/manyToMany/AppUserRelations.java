package com.agriguardian.entity.manyToMany;

import com.agriguardian.entity.AppUser;
import com.agriguardian.enums.Relation;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "app_users_relations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(RelationId.class)
public class AppUserRelations {
    @Id
    @ManyToOne(optional = false ,fetch = FetchType.EAGER)
    @JoinColumn(name="controller_id")
    private AppUser controller;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name="follower_id")
    private AppUser userFollower;

    @Enumerated(EnumType.STRING)
    private Relation relation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserRelations that = (AppUserRelations) o;
        return controller.getId()==that.getController().getId() && userFollower.getId()==(that.userFollower.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(controller.getId(), userFollower.getId());
    }
}
