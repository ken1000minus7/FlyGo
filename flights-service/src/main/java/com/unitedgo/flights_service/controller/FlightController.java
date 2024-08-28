package com.unitedgo.flights_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.unitedgo.flights_service.entity.Flight;
import com.unitedgo.flights_service.model.FlightsResponse;
import com.unitedgo.flights_service.model.flightapi.SearchResponse;
import com.unitedgo.flights_service.service.FlightService;
import com.unitedgo.flights_service.util.URSException;
import com.unitedgo.flights_service.validator.ValidDateConstraint;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.constraints.Size;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping("/urs/flights")
public class FlightController {
	
	@Value("${flight.baseUrl}")
	private String flightApiUrl;
	
	@Value("${flight.apiKey}")
	private String flightApiKey;
	
	@Autowired
	private FlightService flightService;
	
	@GetMapping("/search")
	@CircuitBreaker(name = "flightsService", fallbackMethod = "searchFlightsFallback")
	public ResponseEntity<?> searchFlights(
		@RequestParam @Size(max = 3, min = 3) String origin,
		@RequestParam @Size(max = 3, min = 3) String destination,
		@RequestParam @ValidDateConstraint String date
	) {
		SearchResponse response = WebClient.builder()
					.baseUrl(flightApiUrl)
					.build()
					.get()
					.uri(uriBuilder -> 
						uriBuilder.queryParam("engine", "google_flights")
							.queryParam("gl", "in")
							.queryParam("currency", "INR")
							.queryParam("type", 2)
							.queryParam("api_key", flightApiKey)
							.queryParam("departure_id", origin)
							.queryParam("arrival_id", destination)
							.queryParam("outbound_date", date)
							.build()
					)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, t -> {
						System.out.println(t.request().getURI().toString());
						return Mono.error(new URSException("Origin and destinations ids must be valid and date should be in correct format", HttpStatus.BAD_REQUEST));
					})
					.onStatus(HttpStatusCode::is5xxServerError, t -> 
						Mono.error(new URSException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE))
					)
					.bodyToMono(SearchResponse.class)
					.block();
		List<Flight> flights = flightService.processSearchResults(response.getFlights(), origin, destination);		
		return ResponseEntity.ok(new FlightsResponse(flights));
	}
	
	@GetMapping("/info/{id}")
	public ResponseEntity<Flight> getFlightById(@PathVariable String id) throws URSException {
		Flight flight = flightService.getFlightById(id);
		return ResponseEntity.ok(flight);
	}
	
	public void searchFlightsFallback() throws URSException {
		throw new URSException("Cannot search flights right now", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
