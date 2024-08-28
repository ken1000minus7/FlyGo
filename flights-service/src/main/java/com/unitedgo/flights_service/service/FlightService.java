package com.unitedgo.flights_service.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.unitedgo.flights_service.entity.Flight;
import com.unitedgo.flights_service.model.flightapi.Journey;
import com.unitedgo.flights_service.repository.FlightRepository;
import com.unitedgo.flights_service.util.URSException;

@Service
public class FlightService {
	
	@Autowired
	private FlightRepository flightRepository;
	
	public List<Flight> processSearchResults(List<Journey> journeys, String origin, String destination) {
		List<Flight> flights = journeys.stream().map( journey -> {
			String departureTime = journey.getFlights()
						.stream()
						.filter(info -> info.getDepartureAirport().getId().equals(origin))
						.findAny()
						.get()
						.getDepartureAirport()
						.getTime();
			String arrivalTime = journey.getFlights()
						.stream()
						.filter(info -> info.getArrivalAirport().getId().equals(destination))
						.findAny()
						.get()
						.getArrivalAirport()
						.getTime();
			Set<String> airlines = journey.getFlights()
					.stream()
					.map(info -> info.getAirline())
					.collect(Collectors.toSet());
			
			return Flight.builder()
					.departure(origin)
					.arrival(destination)
					.departureTime(departureTime)
					.arrivalTime(arrivalTime)
					.airlines(String.join(",", airlines))
					.flightId(origin + " " + destination + " " + departureTime)
					.duration(journey.getTotalDuration())
					.price(journey.getPrice())
					.build();
		})
		.collect(Collectors.toList());
		return flightRepository.saveAll(flights);
	}
	
	public Flight getFlightById(String flightId) throws URSException {
		return flightRepository.findById(flightId).orElseThrow(() -> new URSException("Flight with given id does not exist", HttpStatus.NOT_FOUND));
	}
}
