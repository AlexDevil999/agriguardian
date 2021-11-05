package com.agriguardian.entity.manyToMany;

import com.agriguardian.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelationId implements Serializable {
    private long controller;

    private long userFollower;
}
