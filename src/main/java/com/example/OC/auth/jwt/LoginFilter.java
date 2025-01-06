package com.example.side.auth.jwt;

import com.example.side.auth.CustomUserDetails;
import com.example.side.auth.dto.LoginDto;
import com.example.side.user.entity.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static com.example.side.common.MoaolioConstants.REFRESH_TOKEN_EXPIRED_MS;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("-------------------in attemptAuthentication");

        LoginDto loginDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("--------create ObjectMapper");
            ServletInputStream inputStream = request.getInputStream();
            System.out.println("request.getInputStream------------------");
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            System.out.println("getMessageBody-----------------");
            loginDto = objectMapper.readValue(messageBody, LoginDto.class);
            System.out.println("-----------------readValue-------------------");
        } catch (IOException e) {
            System.out.println("---------------------------runtimeException12389712938--------");
            throw new RuntimeException(e);
        }


        String username = loginDto.getUid();
        String password = loginDto.getPassword();

        System.out.println("---------login Dto----------");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        System.out.println("---------auth Token----------");
        System.out.println(authToken);
        System.out.println("-----------------------------");


        return authenticationManager.authenticate(authToken);
    }

    /**
     * 로그인 성공시
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();


        String username = customUserDetails.getUsername();
        System.out.println("------username--------");
        System.out.println(username);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        System.out.println(role+auth+username);


        System.out.println("----------username + auth + role-----------");
        System.out.println(username + auth + role);
        String refresh = jwtUtil.createJwt("refresh", "basic", username, role, REFRESH_TOKEN_EXPIRED_MS);
        String access = jwtUtil.createJwt("access", "basic", username, role, REFRESH_TOKEN_EXPIRED_MS);
        System.out.println(access);
        System.out.println("---------create refresh token----------");
        System.out.println(refresh);

        addRefreshEntity(username, refresh, REFRESH_TOKEN_EXPIRED_MS);

        /**
         * Refresh 토큰만 우선적으로 쿠키에 담아 프론트로 보낸 뒤
         * 프론트의 특정 페이지에서 axios를 통해 쿠키(Refresh 토큰)를 가지고
         * 서버측으로 가서 헤더 방식으로 Access 토큰을 가져오면 된다.
         */
        response.addCookie(createCookie("RefreshAuth", refresh));
//        response.sendRedirect("http://localhost:3000");
//        response.sendRedirect("http://localhost:8081/test");
        System.out.println("=----------------end successful---------------");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        System.out.println("fail");
    }


    /**
     * JWT 전달을 쿠키로 진행
     */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value.trim());

        cookie.setMaxAge(24 * 60 * 60);
//        cookie.setSecure(true); // https 통신을 진행할 경우
        cookie.setPath("/"); // 쿠키가 적용될 범위
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = new RefreshToken(username, refresh, date.toString());
        refreshRepository.save(refreshToken);
    }
}