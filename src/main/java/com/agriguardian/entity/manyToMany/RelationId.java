package com.agriguardian.entity.manyToMany;

import com.agriguardian.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelationId implements Serializable {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationId that = (RelationId) o;
        return controller == that.controller && userFollower == that.userFollower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(controller, userFollower);
    }

    private long controller;

    private long userFollower;
}
