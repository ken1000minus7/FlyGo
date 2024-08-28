package com.unitedgo.flights_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Flight {
	
	@Id
	private String flightId;
	private String departure;
	private String arrival;
	private String departureTime;
	private String arrivalTime;
	private String airlines;
	private Integer duration;
	private Double price;
}
