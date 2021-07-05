package com.agriguardian.entity;


import com.agriguardian.enums.GroupRole;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_team_groups")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class UserTeamGroup {
    @Id
    @SequenceGenerator(name = "userTeamGroupsSequence", sequenceName = "user_team_groups_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userTeamGroupsSequence")
    private long id;
    private String number;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroup teamGroup;

    private GroupRole groupRole;
}
