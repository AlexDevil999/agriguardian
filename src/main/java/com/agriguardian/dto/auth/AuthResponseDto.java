package com.agriguardian.dto.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private long accessExpiredAt;
    private long refreshExpiredAt;
}