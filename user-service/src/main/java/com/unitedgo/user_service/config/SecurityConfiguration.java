package com.unitedgo.user_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.unitedgo.user_service.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	private JwtFilter jwtFilter;
	
	@Autowired
	public SecurityConfiguration(JwtFilter jwtFilter) {
		super();
		this.jwtFilter = jwtFilter;
	}

	@Bean
	AuthenticationManager authenticationManager(
		HttpSecurity http, 
		PasswordEncoder passwordEncoder, 
		UserService userService
	) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class)
				.userDetailsService(userService)
				.passwordEncoder(passwordEncoder)
				.and().build();
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(a -> {
				a.requestMatchers("/urs/user/validate").authenticated()
					.anyRequest().permitAll();
			})
			.sessionManagement(s -> { s.sessionCreationPolicy(SessionCreationPolicy.STATELESS); })
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
