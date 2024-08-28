package com.unitedgo.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
public class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;
	
	@BeforeEach
	public void initialize() {
		ReflectionTestUtils.setField(jwtService, "secretKey", "life");
	}
	
	@Test
	public void testGenerateAndValidateToken() {
		String token = jwtService.generateToken("username");
		assertTrue(jwtService.validateToken(token, "username"));
	}
	
	@Test
	public void testExtractClaims() {
		String token = jwtService.generateToken("username");
		String username = jwtService.extractUsername(token);
		boolean expired = jwtService.isTokenExpired(token);
		assertEquals("username", username);
		assertFalse(expired);
	}
}
