package com.agriguardian.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "team_groups")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class TeamGroup {
    @Id
    @SequenceGenerator(name = "teamGroupsSequence", sequenceName = "team_groups_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teamGroupsSequence")
    private long id;
    private String name;

    private String guardianInvitationCode;
    private String vulnerableInvitationCode;
    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "teamGroup")
    private Set<UserTeamGroup> userTeamGroups;
}
