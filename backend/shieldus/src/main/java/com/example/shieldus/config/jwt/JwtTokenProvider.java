package com.example.shieldus.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime; // 밀리초 단위

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC512(secretKey);
        this.verifier = JWT.require(algorithm).build();
    }

    // JWT 토큰 생성
    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        try {
            return JWT.create()
                    .withSubject(authentication.getName()) // 사용자 ID (Subject)
                    .withClaim("auth", authorities) // 권한 정보 (Custom Claim)
                    .withIssuedAt(now) // 발행 시간
                    .withExpiresAt(validity) // 만료 시간
                    .sign(algorithm); // 서명
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new RuntimeException("Error creating JWT token", exception);
        }
    }

    // JWT 토큰으로부터 인증 정보 추출
    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = verifier.verify(token); // 토큰 검증 및 디코딩

        String username = decodedJWT.getSubject();
        String authoritiesClaim = decodedJWT.getClaim("auth").asString();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(authoritiesClaim.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(username, "", authorities); // 비밀번호는 토큰에 없으므로 빈 문자열
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // JWT 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            verifier.verify(token); // 토큰 검증
            return true;
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            System.out.println("JWT Validation Error: " + exception.getMessage());
            return false;
        }
    }
}