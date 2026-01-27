package com.dormex.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allowed-headers}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse and trim comma-separated origins from environment variable
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        boolean hasWildcard = origins.contains("*");
        
        // CORS spec forbids Access-Control-Allow-Origin: * with credentials
        // Browsers will BLOCK such requests completely
        if (hasWildcard && allowCredentials) {
            log.warn("CORS misconfiguration detected: Cannot use wildcard '*' origins with credentials enabled. " +
                    "Switching to allowedOriginPatterns to allow all origins with credentials.");
            // Use allowedOriginPatterns("*") which works with credentials
            // This allows any origin but still sends the actual origin in the response header
            configuration.setAllowedOriginPatterns(List.of("*"));
        } else if (hasWildcard) {
            // Wildcard without credentials - this is valid
            configuration.setAllowedOrigins(List.of("*"));
        } else {
            // Specific origins - use patterns to support wildcards like https://*.example.com
            // Also handles exact origins correctly
            configuration.setAllowedOriginPatterns(origins);
        }
        
        // Parse and trim comma-separated HTTP methods
        List<String> methods = Arrays.stream(allowedMethods.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        configuration.setAllowedMethods(methods);
        
        // Allow all headers or parse specific ones
        if ("*".equals(allowedHeaders.trim())) {
            configuration.setAllowedHeaders(List.of("*"));
        } else {
            List<String> headers = Arrays.stream(allowedHeaders.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            configuration.setAllowedHeaders(headers);
        }
        
        // Allow credentials (cookies, authorization headers) - now configurable
        configuration.setAllowCredentials(allowCredentials);
        
        // Expose Authorization header so frontend can read JWT token
        configuration.setExposedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Total-Pages"
        ));
        
        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("CORS configured with origins: {}, credentials: {}", 
                hasWildcard ? "[all origins via patterns]" : origins, allowCredentials);
        
        return source;
    }
}
