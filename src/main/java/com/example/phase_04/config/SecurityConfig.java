package com.example.phase_04.config;

import com.example.phase_04.service.impl.PersonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PersonServiceImpl personService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/manager/**").hasRole("MANAGER")
                        .requestMatchers("/customer/captcha").permitAll()
                        .requestMatchers("/customer/onlinePayment").permitAll()
                        .requestMatchers("/customer/usernameToHtml").permitAll()
                        .requestMatchers("/customer/**").hasAnyRole("CUSTOMER")
                        .requestMatchers("/technician/**").hasAnyRole("TECHNICIAN")
                        .requestMatchers("/person/changePassword").hasAnyRole("TECHNICIAN", "MANAGER", "CUSTOMER")
                        .anyRequest().permitAll())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Autowired
    @Transactional
    public void getUser(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(personService::findByUsername)
                .passwordEncoder(passwordEncoder);
    }
}
