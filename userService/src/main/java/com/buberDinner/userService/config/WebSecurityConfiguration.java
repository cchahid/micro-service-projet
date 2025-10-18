package com.buberDinner.userService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.buberDinner.userService.model.Role;
import com.buberDinner.userService.service.UserService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

   private final UserService userService;
   private final JwtAuthentificatedFilter jwtAuthentificatedFilter;

   @Autowired
   public WebSecurityConfiguration(UserService userService, JwtAuthentificatedFilter jwtAuthentificatedFilter) {
       this.userService = userService;
       this.jwtAuthentificatedFilter = jwtAuthentificatedFilter;
   }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**").permitAll()
       .requestMatchers("/api/users/*/exists", "/api/users/*/isHost").permitAll()
       .requestMatchers("/api/chambres/**","/api/residents/**","/api/paiments/**","/api/requetes-maintenance/**","/api/techniciens/**","/api/statistiques/**").hasAnyAuthority(Role.HOST.name())
       .requestMatchers("/api/chambres/**","/api/paiments/**","/api/requetes-maintenance/**").hasAnyAuthority(Role.GUEST.name())
       .anyRequest().authenticated()).sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
       .authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthentificatedFilter, UsernamePasswordAuthenticationFilter.class);
         return http.build();
    }
    @Bean
    public AuthenticationManager authentificationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    } 
    @Bean
    public AuthenticationProvider authenticationProvider() {
           DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
              provider.setUserDetailsService(userService.userDetailsService());
              provider.setPasswordEncoder(new BCryptPasswordEncoder());
                return provider;
    }



}
