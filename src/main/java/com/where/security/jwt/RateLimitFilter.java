package com.where.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Simple rate limiter to prevent brute force attacks
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // Configure rate limit: 10 requests per minute
    private static final int CAPACITY = 10;
    private static final int REFILL_RATE = 10;
    private static final TimeUnit REFILL_UNIT = TimeUnit.MINUTES;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only apply rate limiting to authentication endpoints
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/api/user/login") || requestURI.contains("/api/token/refresh")) {
            String ipAddress = getClientIP(request);

            TokenBucket tokenBucket = buckets.computeIfAbsent(ipAddress,
                    k -> new TokenBucket(CAPACITY, REFILL_RATE, REFILL_UNIT));

            if (!tokenBucket.tryConsume()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Rate limit exceeded. Please try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Token bucket implementation for rate limiting
     */
    private static class TokenBucket {
        private final int capacity;
        private final float refillTokensPerPeriod;
        private final long refillPeriodNanos;

        private float availableTokens;
        private long lastRefillTimestamp;

        public TokenBucket(int capacity, int refillRate, TimeUnit refillUnit) {
            this.capacity = capacity;
            this.refillTokensPerPeriod = refillRate;
            this.refillPeriodNanos = refillUnit.toNanos(1);
            this.availableTokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();

            if (availableTokens < 1) {
                return false;
            }

            availableTokens -= 1;
            return true;
        }

        private void refill() {
            long currentTime = System.nanoTime();
            long elapsedTime = currentTime - lastRefillTimestamp;

            if (elapsedTime <= 0) {
                return;
            }

            float tokensToAdd = (elapsedTime / (float) refillPeriodNanos) * refillTokensPerPeriod;
            if (tokensToAdd > 0) {
                availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
                lastRefillTimestamp = currentTime;
            }
        }
    }
}