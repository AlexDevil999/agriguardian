package com.agriguardian.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "location_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
    @Id
    @SequenceGenerator(name = "locationDataSequence", sequenceName = "location_data_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locationDataSequence")
    private long id;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    @Column(name = "lon")
    private Double lon;
    @Column(name = "lat")
    private Double lat;

    @Column(name = "last_online")
    private Long lastOnline;
}
