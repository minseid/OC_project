package com.where.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // JWT 삭제는 CustomLogoutFilter에서 처리하므로 여기서는 응답만 처리

        // API 요청인 경우 JSON 응답
        if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": true, \"message\": \"로그아웃 성공\"}");
        }
        // 웹 요청인 경우 리다이렉트
        else {
            // 기본 리다이렉트 URL
            String redirectUrl = "/login?logout=true";

            // 소셜 로그인 여부에 따라 다른 리다이렉트 처리
            if (authentication != null && authentication.getDetails() != null) {
                try {
                    // Map인 경우에만 provider 정보 추출 시도
                    if (authentication.getDetails() instanceof Map) {
                        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                        String provider = (String) details.get("provider");

                        if (provider != null) {
                            switch (provider) {
                                case "naver":
                                    redirectUrl = "/naver-logout-success";
                                    break;
                                case "kakao":
                                    redirectUrl = "/kakao-logout-success";
                                    break;
                                case "apple":
                                    redirectUrl = "/apple-logout-success";
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // 예외 발생 시 기본 리다이렉트 유지
                    // 로그 추가
                    // log.error("소셜 로그아웃 처리 중 오류 발생", e);
                }
            }

            response.sendRedirect(redirectUrl);
        }
    }
}