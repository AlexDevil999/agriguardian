package com.agriguardian.entity.manyToMany;


import com.agriguardian.entity.AppUser;
import com.agriguardian.entity.TeamGroup;
import com.agriguardian.enums.GroupRole;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "app_user_team_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AppUserTeamGroup {
    @Id
    @SequenceGenerator(name = "appUserTeamGroupsSequence", sequenceName = "app_user_team_groups_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appUserTeamGroupsSequence")
    private long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    @ManyToOne
    @JoinColumn(name = "team_group_id")
    private TeamGroup teamGroup;
    @Enumerated(EnumType.STRING)
    private GroupRole groupRole;


    public boolean storesBind(AppUser appUser, TeamGroup teamGroup) {
        return appUser.equals(this.appUser) && teamGroup.equals(this.teamGroup);
    }
}
