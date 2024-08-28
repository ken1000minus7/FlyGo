package com.unitedgo.booking_service.model;

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
public class Passenger {
	private Integer passengerId;
	private String name;
	private Integer age;
	private String gender;
	private String mealPreferance;
	private String seatPreference;
	private String flightId;
	private String userId;
	private String pnr;
}
