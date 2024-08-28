package com.unitedgo.passenger_service.dto;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerDTO {
	
	@NotNull(message = "${passenger.name}")
	@Pattern(regexp = "^[a-zA-z\\d\\s-]+$", message = "${passenger.name}")
	private String name;
	
	@NotNull(message = "${passenger.gender}")
	@Pattern(regexp = "^Male|Female|Other$", message = "${passenger.gender}")
	private String gender;
	
	@NotNull(message = "${passenger.age}")
	@Range(min = 0, max = 80, message = "${passenger.age}")
	private Integer age;
	
	@NotNull(message = "${passenger.flight}")
	private String flightId;
	
	private String mealPreference;
	private String seatPreference;
}
