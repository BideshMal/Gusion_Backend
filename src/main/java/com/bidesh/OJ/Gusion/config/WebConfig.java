package com.bidesh.OJ.Gusion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "https://conceptual-datha-personal00abhi-6944df08.koyeb.app", // Your Live App
                    "https://gus-front.vercel.app", 
                    "http://localhost:8081", // Local Vite
                    "http://localhost:5173"  // Default Vite
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true); // Crucial for Auth tokens!
    }
}
// package com.bidesh.OJ.Gusion.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**") // Allow all endpoints
//                 .allowedOrigins("*") // Allow requests from ANYWHERE (Lovable, Postman, etc.)
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                 .allowedHeaders("*");
//     }
// }
