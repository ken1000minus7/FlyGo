package com.unitedgo.booking_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Flight {
	
	private String flightId;
	private String departure;
	private String arrival;
	private String departureTime;
	private String arrivalTime;
	private String airlines;
	private Integer duration;
	private Double price;
}
