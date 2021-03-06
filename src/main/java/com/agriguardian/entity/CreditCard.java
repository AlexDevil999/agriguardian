package com.agriguardian.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @SequenceGenerator(name = "creditCardsSequence", sequenceName = "credit_cards_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "creditCardsSequence")
    private long id;
    private String number;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;


    public void addAppUser(AppUser u) {
        u.addCreditCard(this);
    }
}
