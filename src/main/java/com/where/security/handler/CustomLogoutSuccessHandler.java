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
                    if (authentication.getDetails() instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();

                        Object providerObj = details.get("provider");
                        if (providerObj instanceof String) {
                            String provider = (String) providerObj;

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
                    }
                } catch (Exception e) {
                    // 로그 추가
                }
            }

            response.sendRedirect(redirectUrl);
        }
    }
}