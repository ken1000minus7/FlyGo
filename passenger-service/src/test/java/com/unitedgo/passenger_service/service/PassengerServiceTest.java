package com.unitedgo.passenger_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.unitedgo.passenger_service.dto.PassengerDTO;
import com.unitedgo.passenger_service.entity.Passenger;
import com.unitedgo.passenger_service.repository.PassengerRepository;
import com.unitedgo.passenger_service.util.URSException;

import reactor.core.publisher.Mono;

@SpringBootTest
public class PassengerServiceTest {
	
	@Mock
	private PassengerRepository passengerRepository;
	
	@Mock
	private WebClient.Builder webClient;
	
	@InjectMocks
	private PassengerService passengerService;
	
	@BeforeEach
	public void initialize() {
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(authentication.getPrincipal()).thenReturn("user");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}
	
	@Test
	public void testAddPassenger() throws URSException {
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		when(uriSpecMock.uri(anyString())).thenReturn(headersSpecMock);
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);
		when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(mock(ResponseEntity.class)));
		
		when(passengerRepository.findPassengerCount("id", "user")).thenReturn(2);
		PassengerDTO passengerDTO = PassengerDTO.builder()
					.age(10)
					.gender("Male")
					.flightId("id")
					.name("name")
					.build();
		Passenger passenger = Passenger.builder()
				.name(passengerDTO.getName())
				.age(passengerDTO.getAge())
				.gender(passengerDTO.getGender())
				.flightId(passengerDTO.getFlightId())
				.userId("user")
				.mealPreferance(passengerDTO.getMealPreference())
				.seatPreference(passengerDTO.getSeatPreference())
				.pnr(null)
				.build();
		when(passengerRepository.save(any())).thenReturn(passenger);
		Passenger p = passengerService.addPassenger(passengerDTO);
		assertEquals(passenger, p);
	}
	
	@Test
	public void testAddPassengerPassengersFull() throws URSException {
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		when(uriSpecMock.uri(anyString())).thenReturn(headersSpecMock);
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);
		when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(mock(ResponseEntity.class)));
		
		when(passengerRepository.findPassengerCount("id", "user")).thenReturn(4);
		
		URSException exception = assertThrows(URSException.class, () -> passengerService.addPassenger(PassengerDTO.builder().flightId("id").build()));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
		assertEquals("Can't add more passengers", exception.getMessage());
	}
	
	@Test
	public void testGetPassengersByFlight() {
		Passenger passenger = Passenger.builder()
					.age(10)
					.flightId("id")
					.gender("Male")
					.name("name")
					.userId("user")
					.build();
		List<Passenger> passengers = List.of(passenger);
		
		when(passengerRepository.findByFlightId("id", "user")).thenReturn(passengers);
		
		List<Passenger> output = passengerService.getPassengersByFlight("id");
		assertEquals(passengers, output);
	}
	
	@Test
	public void testGetPassengersByBooking() {
		Passenger passenger = Passenger.builder()
				.age(10)
				.flightId("id")
				.gender("Male")
				.name("name")
				.userId("user")
				.pnr("pnr")
				.build();
		when(passengerRepository.findByPnr("pnr")).thenReturn(List.of(passenger));
		List<Passenger> passengers = passengerService.getPassengersByBooking("pnr");
		assertEquals(passenger, passengers.get(0));
	}
	
	@Test
	public void testBookPassengers() throws URSException {
		Passenger passenger = Passenger.builder()
				.age(10)
				.flightId("id")
				.gender("Male")
				.name("name")
				.userId("user")
				.build();
		List<Passenger> passengers = List.of(passenger);
	
		when(passengerRepository.findByFlightId("id", "user")).thenReturn(passengers);
		when(passengerRepository.findByPnr("pnr")).thenReturn(List.of(passenger));
		List<Passenger> output = passengerService.bookPassengers("id", "pnr");
		assertEquals(passengers, output);
		
	}
	
	@Test
	public void testBookPassengersNoPassengers() throws URSException {
	
		when(passengerRepository.findByFlightId("id", "user")).thenReturn(List.of());
		URSException exception = assertThrows(URSException.class, () -> passengerService.bookPassengers("id", "pnr"));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("No passengers available to book for this flight", exception.getMessage());
		
	}
}
