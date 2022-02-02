package com.agriguardian.service.security;

import com.agriguardian.domain.AppUserAuthDetails;
import com.agriguardian.dto.auth.AuthResponseDto;
import com.agriguardian.entity.AppUser;
import com.agriguardian.exception.BadTokenException;
import com.agriguardian.service.AesEncryptor;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.token.secret}")
    private String jwtSecret;
    @Value("${jwt.token.ttl_ms.access}")
    private long accessValidity;
    @Value("${jwt.token.ttl_ms.refresh}")
    private long refreshValidity;

    private final AesEncryptor aesEncryptor;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }


    public AppUserAuthDetails readTokenInfo(String token) {
        Claims claims = this.extractClaim(token);
        String username = claims.getSubject();
        log.debug("[readTokenInfo] username from token is: " + username);
        Object o = claims.get("role");
        if (o instanceof String) {
            return AppUserAuthDetails.build(username, String.class.cast(o));
        } else {
            throw new BadTokenException("Invalid token data");
        }
    }

    public AuthResponseDto token(AppUser appUser) throws NoSuchPaddingException, NoSuchAlgorithmException {
        long accessExpiresAt = System.currentTimeMillis() + accessValidity;
        long refreshExpiresAt = System.currentTimeMillis() + refreshValidity;

        String access = generateAccess(appUser, accessExpiresAt, "access");
        String refresh = generateRefresh(appUser, refreshExpiresAt, "refresh");
        refresh = aesEncryptor.encode(refresh);
        return new AuthResponseDto(access, refresh, accessExpiresAt, refreshExpiresAt);
    }


    private Claims extractClaim(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }

    public String getOwner(String token) {
        Claims claims = this.extractClaim(token);
        String username = claims.getSubject();
        return username;
    }

    private String generateAccess(AppUser user, long expiresAt, String type) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        claims.put("id", user.getId());
        claims.put("role", user.getUserRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expiresAt))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    private String generateRefresh(AppUser user, long expiresAt, String type)
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        claims.put("id", user.getId());
        claims.put("role", user.getUserRole());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expiresAt))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean isTokenAccess(String token) {
        String type = extractClaim(token).get("type", String.class);
        switch (type) {
            case "access": {
                return true;
            }
            case "refresh": {
                throw new BadTokenException("inappropriate token type: 'refresh'");
            }
            default: {
                throw new BadTokenException("unknown token type: " + token);
            }
        }
    }

    public boolean validateSign(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            throw new BadTokenException("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            throw new BadTokenException("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            throw new BadTokenException("Malformed jwt");
        } catch (SignatureException sEx) {
            throw new BadTokenException("Invalid signature");
        } catch (Exception e) {
            throw new BadTokenException("Invalid token");
        }
    }
}
