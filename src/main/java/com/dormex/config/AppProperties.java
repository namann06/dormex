package com.dormex.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
@Getter
@Setter
public class AppProperties {

    
    private final Jwt jwt = new Jwt();

   
    private final Cors cors = new Cors();

   
    private final Admin admin = new Admin();

  
    @Getter
    @Setter
    public static class Jwt {
       
        @NotBlank(message = "JWT secret is required")
        private String secret;

     
        @Positive(message = "JWT expiration must be positive")
        private long expiration = 86400000L;

        @Positive(message = "Refresh token expiration must be positive")
        private long refreshExpiration = 604800000L;
    }

   
    @Getter
    @Setter
    public static class Cors {
      
        private String allowedOrigins = "http://localhost:3000";

      
        private String allowedMethods = "GET,POST,PUT,DELETE,PATCH,OPTIONS";

        private String allowedHeaders = "*";
    }

  
    @Getter
    @Setter
    public static class Admin {
      
        private String email = "admin@dormex.com";

      
        private String password;

      
        private String name = "System Admin";
    }
}
