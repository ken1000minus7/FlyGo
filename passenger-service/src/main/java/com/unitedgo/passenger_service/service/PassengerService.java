package com.unitedgo.passenger_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.unitedgo.passenger_service.dto.PassengerDTO;
import com.unitedgo.passenger_service.entity.Passenger;
import com.unitedgo.passenger_service.repository.PassengerRepository;
import com.unitedgo.passenger_service.util.URSException;

import reactor.core.publisher.Mono;

@Service
public class PassengerService {

	@Autowired
	private PassengerRepository passengerRepository;
	
	@Autowired
	private WebClient.Builder webClient;
	
	@Transactional
	public Passenger addPassenger(PassengerDTO passengerDTO) throws URSException {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		webClient.build()
			.get()
			.uri("http://flights-service/urs/flights/info/" + passengerDTO.getFlightId())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new URSException("The given flight does not exist", HttpStatus.NOT_FOUND)))
			.onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new URSException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE)))
			.toBodilessEntity()
			.block();
		
		Integer currentPassengers = passengerRepository.findPassengerCount(passengerDTO.getFlightId(), username);
		if(currentPassengers > 3) {
			throw new URSException("Can't add more passengers", HttpStatus.BAD_REQUEST);
		}
		Passenger passenger = Passenger.builder()
					.name(passengerDTO.getName())
					.age(passengerDTO.getAge())
					.gender(passengerDTO.getGender())
					.flightId(passengerDTO.getFlightId())
					.userId(username)
					.mealPreferance(passengerDTO.getMealPreference())
					.seatPreference(passengerDTO.getSeatPreference())
					.pnr(null)
					.build();
		return passengerRepository.save(passenger);
	}
	
	public List<Passenger> getPassengersByFlight(String flightId) {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return passengerRepository.findByFlightId(flightId, username);
	}
	
	public List<Passenger> getPassengersByBooking(String pnr) {
		return passengerRepository.findByPnr(pnr);
	}
	
	@Transactional
	public List<Passenger> bookPassengers(String flightId, String pnr) throws URSException {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Passenger> passengers = getPassengersByFlight(flightId);
		if(passengers.isEmpty()) {
			throw new URSException("No passengers available to book for this flight", HttpStatus.NOT_FOUND);
		}
		passengerRepository.updatePassengerPnr(pnr, flightId, username);
		return passengerRepository.findByPnr(pnr);
	}
}
