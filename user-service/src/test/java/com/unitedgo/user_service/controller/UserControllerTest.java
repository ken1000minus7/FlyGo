package com.unitedgo.user_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unitedgo.user_service.dto.UserDTO;
import com.unitedgo.user_service.model.AuthenticationResponse;
import com.unitedgo.user_service.model.Credentials;
import com.unitedgo.user_service.service.JwtService;
import com.unitedgo.user_service.service.UserService;
import com.unitedgo.user_service.util.URSException;

@SpringBootTest
class UserControllerTest {

	@Mock
	private UserService userService;
	
	@Mock
	private JwtService jwtService;	
	
	@Mock
	private AuthenticationManager authenticationManager;
	
	@InjectMocks
	private UserController userController;
	
	@Test
	void testRegisterUser() throws URSException {
		UserDTO userDTO = getUserDTO();
		when(userService.registerUser(userDTO)).thenReturn(userDTO);
		when(jwtService.generateToken(userDTO.getUsername())).thenReturn("token");
		ResponseEntity<AuthenticationResponse> response = userController.registerUser(userDTO);
		assertEquals(HttpStatus.CREATED.value(), response.getStatusCode().value());
		AuthenticationResponse authResponse = response.getBody();
		assertEquals("token", authResponse.getJwtToken());
		assertEquals(userDTO, authResponse.getUser());
		assertEquals("Registered successfully", authResponse.getMessage());
	}
	
	@Test
	void testLoginUser() throws URSException {
		UserDTO userDTO = getUserDTO();
		Credentials credentials = new Credentials(userDTO.getUsername(), userDTO.getPassword());
		when(userService.getUser(userDTO.getUsername())).thenReturn(userDTO);
		when(jwtService.generateToken(userDTO.getUsername())).thenReturn("token");
		when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);
		ResponseEntity<AuthenticationResponse> response = userController.loginUser(credentials);
		assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		AuthenticationResponse authResponse = response.getBody();
		assertEquals("token", authResponse.getJwtToken());
		assertEquals(userDTO, authResponse.getUser());
		assertEquals("Logged in successfully", authResponse.getMessage());
	}
	
	@Test
	void testLoginUserBadCredentials() throws URSException {
		UserDTO userDTO = getUserDTO();
		Credentials credentials = new Credentials(userDTO.getUsername(), userDTO.getPassword());
		when(authenticationManager.authenticate(Mockito.any())).thenThrow(new BadCredentialsException(""));
		URSException exception = assertThrows(URSException.class, () -> userController.loginUser(credentials));
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getCode());
		assertEquals("Incorrect username or password", exception.getMessage());
	}
	
	@Test
	void validateToken() {
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn("user");
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		SecurityContextHolder.setContext(securityContext);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		ResponseEntity<Credentials> response = userController.validateToken();
		assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
		assertEquals("user", response.getBody().getUsername());
		assertNull(response.getBody().getPassword());
	}
	
	@Test
	void testLoginUserFallback() {
		URSException exception = assertThrows(URSException.class, () -> userController.loginUserFallback(null, new Exception()));
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getCode());
		assertEquals("Login unavailable", exception.getMessage());
	}
	
	private UserDTO getUserDTO() {
		return UserDTO.builder()
				.city("c")
				.email("emaik")
				.name("name")
				.password("passs")
				.phoneNumber("1111111")
				.username("user")
				.build();
	}
}
