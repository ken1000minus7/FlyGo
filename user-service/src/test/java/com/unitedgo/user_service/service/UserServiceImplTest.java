package com.unitedgo.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unitedgo.user_service.dto.UserDTO;
import com.unitedgo.user_service.entity.User;
import com.unitedgo.user_service.repository.UserRepository;
import com.unitedgo.user_service.util.URSException;

@SpringBootTest
public class UserServiceImplTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ModelMapper modelMapper;
	
	@Mock 
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	@Test
	public void testRegisterUser() throws URSException {
		UserDTO userDTO = getUserDTO();
		UserDTO savedUserDTO = getUserDTO();
		savedUserDTO.setPassword("encoded");
		User user = getUser();
		User savedUser = getUser();
		user.setPassword("encoded");
		savedUser.setPassword("encoded");
		
		when(userRepository.existsById(userDTO.getUsername())).thenReturn(false);
		when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encoded");
		when(modelMapper.map(Mockito.any(), eq(User.class))).thenReturn(user);
		when(userRepository.save(user)).thenReturn(savedUser);
		when(modelMapper.map(Mockito.any(), eq(UserDTO.class))).thenReturn(savedUserDTO);
		UserDTO outputDto = userService.registerUser(userDTO);
		assertEquals(savedUserDTO, outputDto);
	}
	
	@Test
	public void testRegisterUserUsernameExists() throws URSException {
		UserDTO userDTO = getUserDTO();
		when(userRepository.existsById(userDTO.getUsername())).thenReturn(true);
		URSException exception = assertThrows(URSException.class, () -> userService.registerUser(userDTO));
		assertEquals(HttpStatus.CONFLICT, exception.getCode());
		assertEquals("Username already exists", exception.getMessage());
	}
	
	@Test
	public void testGetUser() throws URSException {
		User user = getUser();
		UserDTO userDTO = getUserDTO();
		when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
		when(modelMapper.map(eq(user), eq(UserDTO.class))).thenReturn(userDTO);
		UserDTO output = userService.getUser(user.getUsername());
		assertEquals(userDTO, output);
	}
	
	@Test 
	public void testGetUserNotFound() throws URSException {
		User user = getUser();
		when(userRepository.findById(user.getUsername())).thenReturn(Optional.empty());
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.getUser(user.getUsername()));
		assertEquals("User not found", exception.getMessage());
	}
	
	@Test
	public void testLoadByUsername() throws URSException {
		User user = getUser();
		when(userRepository.findById(user.getUsername())).thenReturn(Optional.of(user));
		assertEquals(user, userService.loadUserByUsername(user.getUsername()));
	}
	
	@Test 
	public void testLoadByUsernameNotFound() throws URSException {
		User user = getUser();
		when(userRepository.findById(user.getUsername())).thenReturn(Optional.empty());
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(user.getUsername()));
		assertEquals("User not found", exception.getMessage());
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
	
	private User getUser() {
		UserDTO userDTO = getUserDTO();
		return User.builder()
				.city(userDTO.getCity())
				.email(userDTO.getEmail())
				.name(userDTO.getName())
				.password(userDTO.getPassword())
				.phoneNumber(userDTO.getPhoneNumber())
				.username(userDTO.getUsername())
				.build();
	}
}
