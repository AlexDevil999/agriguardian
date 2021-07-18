package com.agriguardian.entity.manyToMany;

import com.agriguardian.entity.AlertBluetoothZone;
import com.agriguardian.entity.AppUser;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "app_user_bluetooth_zones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AppUserBluetoothZone {
    @Id
    @SequenceGenerator(name = "AppUserBluetoothZoneSequence", sequenceName = "app_user_bluetooth_zones_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AppUserBluetoothZoneSequence")
    private long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;
    @ManyToOne
    @JoinColumn(name = "alert_bluetooth_zone_id")
    private AlertBluetoothZone alertBluetoothZone;


    public boolean storesBind(AppUser appUser, AlertBluetoothZone zone) {
        return appUser.equals(this.appUser) && zone.equals(this.alertBluetoothZone);
    }
}
