package com.rayala.oauthimpl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private String name;
    private String email;
    private String phoneNumber; // remains null for now
    private String pictureUrl;  // NEW FIELD
    private Role role = Role.USER;


    public static UserInfo getDummyUser() {
        return builder()
                .name("Sunny")
                .email("sanathsunnyno.1@gmail.com")
                .phoneNumber("910058xxxx")
                .pictureUrl("")
                .role(Role.USER)
                .build();
    }
}
