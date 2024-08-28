package com.unitedgo.flights_service.model.flightapi;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SearchResponse {
	private List<Journey> bestFlights;
	private List<Journey> otherFlights;
	
	public List<Journey> getFlights() {
		ArrayList<Journey> flights = new ArrayList<>();
		if(this.bestFlights != null) flights.addAll(this.bestFlights);
		if(this.otherFlights != null) flights.addAll(this.otherFlights);
		return flights;
	}
}
