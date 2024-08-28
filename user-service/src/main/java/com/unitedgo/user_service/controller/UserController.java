package com.unitedgo.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.user_service.dto.UserDTO;
import com.unitedgo.user_service.model.Credentials;
import com.unitedgo.user_service.model.AuthenticationResponse;
import com.unitedgo.user_service.service.JwtService;
import com.unitedgo.user_service.service.UserService;
import com.unitedgo.user_service.util.URSException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/urs/user")
@Validated
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody @Valid UserDTO userDTO) throws URSException {
		UserDTO savedUser = userService.registerUser(userDTO);
		String jwtToken = jwtService.generateToken(savedUser.getUsername());
		AuthenticationResponse response = new AuthenticationResponse(jwtToken, savedUser, "Registered successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PostMapping("/login") 
	@CircuitBreaker(name = "userService", fallbackMethod = "loginUserFallback")
	public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody @Valid Credentials credentials) throws URSException {
		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
			authenticationManager.authenticate(token);
		} catch (BadCredentialsException e) {
			throw new URSException("Incorrect username or password", HttpStatus.UNAUTHORIZED);
		}
		UserDTO userDTO = userService.getUser(credentials.getUsername());
		String jwtToken = jwtService.generateToken(credentials.getUsername());
		AuthenticationResponse response = new AuthenticationResponse(jwtToken, userDTO, "Logged in successfully");
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/validate")
	public ResponseEntity<Credentials> validateToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Credentials credentials = new Credentials((String) authentication.getPrincipal(), null);
		return ResponseEntity.ok(credentials);
	}
	
	public void loginUserFallback(Credentials credentials) throws URSException {
		throw new URSException("Login unavailable", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
