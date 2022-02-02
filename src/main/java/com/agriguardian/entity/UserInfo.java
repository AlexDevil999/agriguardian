package com.agriguardian.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class UserInfo {
    @Id
    @SequenceGenerator(name = "userInfoSequence", sequenceName = "user_info_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userInfoSequence")
    private long id;
    private String name;
    private String country;
    private String county;
    private String city;
    private String street;
    @Column(name = "zip_area_code")
    private String zipAreaCode;
    @Column(name = "phone_code")
    private String phoneCode;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "avatar")
    private Integer userAvatar;

    @OneToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;


    public void addAppUser(AppUser user) {
        user.addUserInfo(this);
    }

    public void editUserInfo(UserInfo editedInfo){
        this.name=editedInfo.getName();
        this.userAvatar=editedInfo.getUserAvatar();
        this.country=editedInfo.getCountry();
        this.county=editedInfo.getCounty();
        this.city= editedInfo.getCity();
        this.street=editedInfo.getStreet();
        this.zipAreaCode=editedInfo.getZipAreaCode();
        this.phoneCode= editedInfo.getPhoneCode();
        this.phoneNumber= editedInfo.phoneNumber;
    }

}
