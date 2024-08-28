package com.unitedgo.passenger_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.passenger_service.dto.PassengerDTO;
import com.unitedgo.passenger_service.entity.Passenger;
import com.unitedgo.passenger_service.model.PassengersResponse;
import com.unitedgo.passenger_service.service.PassengerService;
import com.unitedgo.passenger_service.util.URSException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/urs/passenger")
public class PassengerController {
	
	@Autowired
	private PassengerService passengerService;
	
	@PostMapping
	@CircuitBreaker(name = "passengerService", fallbackMethod = "addPassengerFallback")
	public ResponseEntity<Passenger> addPassenger(@RequestBody @Valid PassengerDTO passengerDTO) throws URSException {
		Passenger passenger = passengerService.addPassenger(passengerDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(passenger);
	}
	
	@GetMapping("/flight")
	public ResponseEntity<PassengersResponse> getPassengersByFlight(@RequestParam String flightId) {
		List<Passenger> passengers = passengerService.getPassengersByFlight(flightId);
		return ResponseEntity.ok(new PassengersResponse(passengers));
	}
	
	@GetMapping("/booking")
	public ResponseEntity<PassengersResponse> getPassengersByBooking(@RequestParam String pnr) {
		List<Passenger> passengers = passengerService.getPassengersByBooking(pnr);
		return ResponseEntity.ok(new PassengersResponse(passengers));
	}
	
	@PutMapping("/book")
	public ResponseEntity<PassengersResponse> bookPassengers(@RequestParam String pnr, @RequestParam String flightId) throws URSException {
		List<Passenger> passengers = passengerService.bookPassengers(flightId, pnr);
		return ResponseEntity.ok(new PassengersResponse(passengers));
	}
	
	public void addPassengerFallback() throws URSException {
		throw new URSException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
