package com.unitedgo.user_service.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unitedgo.user_service.entity.User;
import com.unitedgo.user_service.service.JwtService;
import com.unitedgo.user_service.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
class JwtFilterTest {

	@Mock
	private JwtService jwtService;
	
	@Mock 
	private UserService userService;
	
	@InjectMocks
	private JwtFilter jwtFilter;
	
	@Test
	void testDoFilterInternal() throws ServletException, IOException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		FilterChain filterChain = mock(FilterChain.class);
		
		when(request.getHeader(anyString())).thenReturn("Bearer token");
		when(jwtService.extractUsername("token")).thenReturn("user");
		
		User user = User.builder()
				.username("user")
				.password("pass")
				.build();
		when(userService.loadUserByUsername("user")).thenReturn(user);
		when(jwtService.validateToken("token", "user")).thenReturn(true);
		
		jwtFilter.doFilterInternal(request, response, filterChain);
		assertEquals("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}
}
