package com.buberdinner.dinnerservice.infrastructure.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tout appel vers /images/** servira des fichiers depuis uploads/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");
    }
}

