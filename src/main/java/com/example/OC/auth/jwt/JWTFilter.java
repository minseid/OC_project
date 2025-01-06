package com.example.side.auth.jwt;

import com.example.side.auth.CustomOAuth2User;
import com.example.side.auth.CustomUserDetails;
import com.example.side.user.dto.request.UserDto;
import com.example.side.user.dto.request.UserOAuth2Dto;
import com.example.side.user.entity.User;
import com.example.side.user.entity.UserRole;
import com.example.side.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /**
         * 헤더에서 Authorization에 담긴 액세스 토큰을 꺼냄
         */
        String accessToken = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();
        System.out.println("access-> " + accessToken);
        System.out.println("requestURI-> " + requestURI);

        /**
         * 없다면 다음 필터
         */
        if (accessToken == null || requestURI.contains("/api/user")) {

            filterChain.doFilter(request, response);

            return;
        }

        /**
         * 만료확인, 만료 시 다음 필터로 넘기지 않음
         */
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        /**
         * 토큰이 Access인지 확인(발급 시 페이로드에 명시)
         */
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            /**
             * 프론트와 얘기 후 특정 상태코드를 응답해야함
             */
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        /**
         * 토큰에서 username, role 획득 후 값 매핑
         */
        String username = jwtUtil.getUsername(accessToken);
        String userRole = jwtUtil.getRole(accessToken);
        String loginType = jwtUtil.getLoginType(accessToken);

        if (loginType.equals("social")) {

            UserOAuth2Dto userOAuth2Dto = new UserOAuth2Dto();
            userOAuth2Dto.setUid(username);
            userOAuth2Dto.setRole(userRole);

            /**
             * OAuth2UserDetails에 회원 정보 객체 담기
             */
            CustomOAuth2User customOAuth2User = new CustomOAuth2User(userOAuth2Dto);

            /**
             * 스프링 시큐리티 인증 토큰 생성
             */
            Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

            /**
             * 세션에 사용자 등록
             */
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        }
        else if (loginType.equals("basic")) {
            Optional<User> findUser = userRepository.findByUid(username);
            User user = findUser.orElseThrow(() -> new UsernameNotFoundException("username not found"));

            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            /**
             * 스프링 시큐리티 인증 토큰 생성
             */
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(), customUserDetails.getAuthorities());

            /**
             * 세션에 사용자 등록
             */
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
        }
    }
}
