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
	
	
	private UserRepository userRepository;
	private ModelMapper modelMapper;
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
	}



	@Override
	public UserDTO registerUser(UserDTO userDTO) throws URSException {
		if(userRepository.existsByUsernameOrPhoneNumber(userDTO.getUsername(), userDTO.getPhoneNumber())) {
			throw new URSException("Username or phone number already exists", HttpStatus.CONFLICT);
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
