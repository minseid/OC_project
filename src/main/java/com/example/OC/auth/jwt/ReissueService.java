package com.example.side.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.example.side.common.MoaolioConstants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("find refresh------------------------");

        /**
         * 쿠키에서 Refresh 토큰 찾음
         */
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("RefreshAuth")) {

                refresh = cookie.getValue();
            }
        }
        //토큰 존재여부
        if (refresh == null) {

            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        /**
         * 만료 확인
         */
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }


        /**
         * 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
         */
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("is not refresh token", HttpStatus.BAD_REQUEST);
        }

        /**
         * Refresh Token이 DB에 저장되어 있는지 확인
         */
        if (!refreshRepository.existsByRefresh(refresh)) {

            return new ResponseEntity<>("not found refresh token", HttpStatus.BAD_REQUEST);
        }

        /**
         * 새로운 액세스 토큰
         */
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        String loginType = jwtUtil.getLoginType(refresh);

        String newAccess = "";
        String newRefresh = "";
        //make new JWT
        if (loginType.equals("social")) {
            newAccess = jwtUtil.createJwt("access", "social", username, role, ACCESS_TOKEN_EXPIRED_MS);
            newRefresh = jwtUtil.createJwt("refresh", "social", username, role, REFRESH_TOKEN_EXPIRED_MS);
        }
        else if (loginType.equals("basic")) {
            newAccess = jwtUtil.createJwt("access", "basic", username, role, ACCESS_TOKEN_EXPIRED_MS);
            newRefresh = jwtUtil.createJwt("refresh", "basic", username, role, REFRESH_TOKEN_EXPIRED_MS);
        }
        //엑세스토큰 출력
        System.out.println("new access token: " + newAccess);

        /**
         * 기존 refresh token 삭제 및 새 refresh token 저장
         */
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, REFRESH_TOKEN_EXPIRED_MS);

        //response
        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("RefreshAuth", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * JWT 전달을 쿠키로 진행
     */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

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