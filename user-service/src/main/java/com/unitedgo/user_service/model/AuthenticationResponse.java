package com.unitedgo.user_service.model;

import com.unitedgo.user_service.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
	private String jwtToken;
	private UserDTO user;
	private String message;
}
