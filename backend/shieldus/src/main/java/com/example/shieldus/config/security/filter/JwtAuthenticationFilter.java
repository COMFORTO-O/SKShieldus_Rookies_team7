package com.example.shieldus.config.security.filter;


import com.example.shieldus.config.jwt.JwtTokenProvider;
import com.example.shieldus.config.security.dto.LoginRequestDto;
import com.example.shieldus.config.security.utils.RSAUtil;
import com.example.shieldus.controller.dto.ResponseDto;
import com.example.shieldus.exception.ErrorCode;
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
//            String password = rsaUtil.decryptRsaBase64(loginRequest.getPassword());
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


        ResponseCookie jwtCookie = ResponseCookie.from("Authorization", jwt)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(60 * 60 * 24)
                .build();

        response.addHeader("Set-Cookie", jwtCookie.toString());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 성공시 data에도 jwt 추가
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("jwt", jwt);
        ResponseDto<Map<String, String>> responseDto = ResponseDto.success(responseBody);

        new ObjectMapper().writeValue(response.getWriter(), responseDto);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseDto<Map<String, String>> responseDto = ResponseDto.error(ErrorCode.AUTHENTICATION_FAILED);
        new ObjectMapper().writeValue(response.getWriter(), responseDto);
    }


}