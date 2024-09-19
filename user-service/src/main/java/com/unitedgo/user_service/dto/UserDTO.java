package com.unitedgo.user_service.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
	
	@Pattern(regexp = "^[a-z0-9@$%*?&!]+$", message = "${user.username}")
	@NotNull(message = "{user.username}")
	private String username;

	@Length(min = 8, message = "${user.password}")
	@NotNull(message = "${user.password}")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*?])[a-zA-z0-9!@#$%^&*?]+$", message = "${user.password}")
	private String password;
	
	@Pattern(regexp = "^([A-Z][a-z]*)(\\s[A-Z][a-z]*)*$", message = "${user.name}")
	@NotNull(message = "${user.name}")
	private String name;
	
	@Email(message = "${user.email}")
	@NotNull(message = "${user.email}")
	private String email;
	
	@Pattern(regexp = "^[A-Za-z]+$", message = "${user.city}")
	@NotNull(message = "${user.city}")
	private String city;
	
	@Pattern(regexp = "^[6789][0-9]{9}$", message = "${user.phone}")
	@NotNull(message = "${user.phone}")
	private String phoneNumber;
}
