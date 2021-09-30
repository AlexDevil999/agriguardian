package com.agriguardian.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registration_code")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RegistrationCode {
    @Id
    @SequenceGenerator(name = "regstrationCodeSequence", sequenceName = "app_users_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "registration_code")
    private String registrationCode;

    @Column(name = "valid_till")
    private Long validTill;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser owner;

    public boolean codeValid(Long time){
        return time<validTill;
    }
}
