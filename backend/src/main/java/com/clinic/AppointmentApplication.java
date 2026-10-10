package com.clinic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication
public class AppointmentApplication {
    public static void main(String[] a) { SpringApplication.run(AppointmentApplication.class, a); }
    @Bean BCryptPasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
    @Bean WebMvcConfigurer cors() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry r) {
                r.addMapping("/api/**").allowedOrigins("http://localhost:5173").allowedMethods("*");
            }
        };
    }
}
