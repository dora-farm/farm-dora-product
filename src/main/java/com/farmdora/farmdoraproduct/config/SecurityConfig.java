package com.farmdora.farmdoraproduct.config;

import com.farmdora.farmdoraproduct.jwt.JwtAuthenticationFilter;
import com.farmdora.farmdoraproduct.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                    // 공개 API
//                    .requestMatchers("/video/main/**", "/my/seller/item/video/**").permitAll()
//                    // 특정 권한이 필요한 API
//                    .requestMatchers("/my/seller/item/register").hasRole("SELLER")
//                    .requestMatchers("/my/seller/item/update").hasRole("SELLER")
//                    .requestMatchers("/video/register").hasRole("SELLER")
//                    .requestMatchers("/video/seller/**").hasRole("SELLER")
//                    .requestMatchers("/video/admin/**").hasRole("ADMIN")
//                    // 여러 권한에 접근 가능한 API
//                    .requestMatchers("/my/seller/item/delete").hasAnyRole("ADMIN", "SELLER")
//                    .requestMatchers("/my/seller/item/detail").hasAnyRole("ADMIN", "SELLER")
//                    .requestMatchers("/my/seller/item/updateStatus").hasAnyRole("ADMIN", "SELLER")
//                    .requestMatchers("/video/delete").hasAnyRole("ADMIN", "SELLER")
//                    .requestMatchers("/video/updateStatus").hasAnyRole("ADMIN", "SELLER")
//                    // 그 외 요청은 인증만 필요
//                    .anyRequest().authenticated())
//                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
//                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement((session)->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // CORS 설정 유지
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Collections.singletonList(("http://192.168.0.14:3000")));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://farmdora.kro.kr"
                // 추가 필요한 도메인들...
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
