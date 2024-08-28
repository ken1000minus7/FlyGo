package com.unitedgo.user_service.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.unitedgo.user_service.dto.UserDTO;
import com.unitedgo.user_service.util.URSException;

public interface UserService extends UserDetailsService {
	public UserDTO registerUser(UserDTO userDTO) throws URSException;
	public UserDTO getUser(String username) throws URSException;
}
