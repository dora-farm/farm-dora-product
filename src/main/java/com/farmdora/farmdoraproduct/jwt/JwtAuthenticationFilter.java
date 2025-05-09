package com.farmdora.farmdoraproduct.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // 1️⃣ Authorization 헤더에서 꺼내기
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.replace("Bearer ", "").trim();
        }

        // 2️⃣ Authorization 헤더가 없으면 쿠키에서 꺼내기
        if (token == null && request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && jwtUtil.validateToken(token)) {
            int userId = jwtUtil.getUserId(token);
            log.debug("인증된 사용자: {}", userId);

//            // 블랙리스트 체크
//            if (Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token))) {
//                log.warn("블랙리스트 토큰 접근 차단");
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                return;
//            }

            // SecurityContext 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, jwtUtil.getAuthorities(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

}