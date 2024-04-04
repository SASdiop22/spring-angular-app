package edu.miage.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }


    @Bean
    WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers("/", "/index.html", "*.ico", "*.css", "*.js");
    }
}
