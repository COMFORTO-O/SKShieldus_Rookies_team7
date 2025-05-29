package com.example.shieldus.config.security.filter;


import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.dto.LoginRequestDto;
import com.example.shieldus.config.security.utils.RSAUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RSAUtil rsaUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, RSAUtil rsaUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.rsaUtil = rsaUtil;
        // 로그인 URL 설정
        setFilterProcessesUrl("/api/account/login");
    }

    // 사용자 토큰 생성
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            // 로그인 object 추출
            LoginRequestDto loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);
            // password 디코딩
            //String password = rsaUtil.decryptRsaBase64(loginRequest.getPassword());
            String password = loginRequest.getPassword();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), password);

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 로그인 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String jwt = jwtTokenProvider.createToken(authResult);

        Map<String, String> responseBody = new HashMap<>();

        // TODO : cookie 에 추가
        ResponseCookie jwtCookie = ResponseCookie.from("Authorization", jwt)
                .httpOnly(false) // JavaScript 접근 불가 (XSS 공격 방어)
                .secure(false)   // 개발 환경 (HTTP)에서는 false로 설정!
                // ⭐ 중요: 프로덕션 (HTTPS) 환경에서는 true로 변경해야 합니다.
                .path("/")       // 모든 경로에서 쿠키 사용 가능
                .sameSite("Lax") // ⭐ 중요: SameSite 속성 명시.
                // 크로스 오리진 요청 시 쿠키 전송 규칙을 정의합니다.
                // 개발 중에는 "Lax"를 먼저 시도하고, 그래도 안되면 HTTPS로 전환 후 "None"을 시도합니다.
                .maxAge(60 * 60 * 24) // 쿠키 유효 기간 (초 단위, 예: 1일)
                .build();


        response.addHeader("Set-Cookie", jwtCookie.toString());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), responseBody);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Authentication Failed");
        errorResponse.put("message", failed.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), errorResponse);
    }


}