package com.unitedgo.flights_service.model;

import java.util.List;

import com.unitedgo.flights_service.entity.Flight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightsResponse {
	private List<Flight> flights;
}
