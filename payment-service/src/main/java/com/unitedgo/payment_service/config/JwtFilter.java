package com.unitedgo.payment_service.config;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.unitedgo.payment_service.model.Credentials;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    
    private WebClient.Builder webClient;
    
    @Autowired
    public JwtFilter(Builder webClient) {
		super();
		this.webClient = webClient;
	}


	@Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION);
        if(!StringUtils.startsWithIgnoreCase(authHeader, BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
            	Credentials credentials = webClient.build()
						            		.get()
						            		.uri("http://user-service/urs/user/validate")
						            		.header(AUTHORIZATION, authHeader)
						            		.retrieve()
						            		.bodyToMono(Credentials.class)
						            		.block();
            	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    credentials.getUsername(),
                                    credentials.getPassword(),
                                    List.of(new SimpleGrantedAuthority("USER"))
                            );
            	usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            	SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        } catch (Exception ignored) { /* Ignoring exception */ }
        finally {
            filterChain.doFilter(request,response);
        }
    }

}
