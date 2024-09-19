package com.unitedgo.payment_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(a -> {
				a.requestMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.anyRequest().authenticated();
			})
			.sessionManagement(s -> { s.sessionCreationPolicy(SessionCreationPolicy.STATELESS); })
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
