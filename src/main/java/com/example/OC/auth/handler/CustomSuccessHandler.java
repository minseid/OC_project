package com.example.side.auth.handler;

import com.example.side.auth.CustomOAuth2User;
import com.example.side.auth.jwt.JWTUtil;
import com.example.side.auth.jwt.RefreshToken;
import com.example.side.auth.jwt.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import static com.example.side.common.MoaolioConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    /**
     * 로그인 성공 시 동작하는 메소드
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // CustomOAuth2User
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        /**
         * username, role 값 가져오기
         */
        String username = customOAuth2User.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

//        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", "social", username, role, REFRESH_TOKEN_EXPIRED_MS);
        log.info("Generated Refresh Token: {}", refresh);
        addRefreshEntity(username, refresh, REFRESH_TOKEN_EXPIRED_MS);


        /**
         * Refresh 토큰만 우선적으로 쿠키에 담아 프론트로 보낸 뒤
         * 프론트의 특정 페이지에서 axios를 통해 쿠키(Refresh 토큰)를 가지고
         * 서버측으로 가서 헤더 방식으로 Access 토큰을 가져오면 된다.
         */
        response.addCookie(createCookie(refresh));
//        response.sendRedirect("http://localhost:3000/");
        response.sendRedirect("http://localhost:3000");

    }

    /**
     * JWT 전달을 쿠키로 진행
     */
    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("RefreshAuth", value);

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
