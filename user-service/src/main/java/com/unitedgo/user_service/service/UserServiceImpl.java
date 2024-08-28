package com.unitedgo.user_service.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.unitedgo.user_service.dto.UserDTO;
import com.unitedgo.user_service.entity.User;
import com.unitedgo.user_service.repository.UserRepository;
import com.unitedgo.user_service.util.URSException;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDTO registerUser(UserDTO userDTO) throws URSException {
		if(userRepository.existsById(userDTO.getUsername())) {
			throw new URSException("Username already exists", HttpStatus.CONFLICT);
		}
		userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		User user = modelMapper.map(userDTO, User.class);
		User savedUser = userRepository.save(user);
		return modelMapper.map(savedUser, UserDTO.class);
	}
	
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findById(username).orElseThrow(() -> {
			return new UsernameNotFoundException("User not found");
		});
	}



	@Override
	public UserDTO getUser(String username) throws URSException {
		User user = userRepository.findById(username).orElseThrow(() -> {
			return new UsernameNotFoundException("User not found");
		});
		return modelMapper.map(user, UserDTO.class);
	}

}
