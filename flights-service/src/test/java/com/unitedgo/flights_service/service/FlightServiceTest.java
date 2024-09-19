package com.unitedgo.flights_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.unitedgo.flights_service.entity.Flight;
import com.unitedgo.flights_service.model.flightapi.Airport;
import com.unitedgo.flights_service.model.flightapi.FlightInfo;
import com.unitedgo.flights_service.model.flightapi.Journey;
import com.unitedgo.flights_service.repository.FlightRepository;
import com.unitedgo.flights_service.util.URSException;

@SpringBootTest
public class FlightServiceTest {

	@Mock
	private FlightRepository flightRepository;
	
	@InjectMocks
	private FlightService flightService;
	
	@Test
	public void testProcessSearchResults() {
		Airport departureAirport1 = new Airport("chicago", "ORD", "2024-12-12 00:00");
		Airport departureAirport2 = new Airport("chicago", "ORD", "2024-12-12 01:00");
		Airport arrivalAirport = new Airport("delhi", "DEL", "2024-12-12 10:00");
		FlightInfo flightInfo1 = FlightInfo.builder()
					.airline("united")
					.departureAirport(departureAirport1)
					.arrivalAirport(arrivalAirport)
					.build();
		FlightInfo flightInfo2 = FlightInfo.builder()
				.airline("united")
				.departureAirport(departureAirport2)
				.arrivalAirport(arrivalAirport)
				.build();
		List<Journey> journeys = List.of(
			Journey.builder()
				.price(10.0)
				.flights(List.of(flightInfo1))
				.totalDuration(10)
				.build(),
			Journey.builder()
				.price(10.0)
				.flights(List.of(flightInfo2))
				.totalDuration(10)
				.build()
		);
		Flight f1 = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.departureTime(departureAirport1.getTime())
				.arrivalTime(arrivalAirport.getTime())
				.build();
		Flight f2 = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.departureTime(departureAirport2.getTime())
				.arrivalTime(arrivalAirport.getTime())
				.build();
		when(flightRepository.saveAll(anyCollection())).thenReturn(List.of(f1, f2));
		List<Flight> flights = flightService.processSearchResults(journeys, "ORD", "DEL");
		Flight flight1 = flights.get(0);
		Flight flight2 = flights.get(1);
		
		assertEquals("ORD", flight1.getDeparture());
		assertEquals("DEL", flight1.getArrival());
		assertEquals("united", flight1.getAirlines());
		assertEquals(departureAirport1.getTime(), flight1.getDepartureTime());
		assertEquals(arrivalAirport.getTime(), flight1.getArrivalTime());
		assertEquals(10.0, flight1.getPrice());
		assertEquals(10, flight1.getDuration());
		
		assertEquals("ORD", flight2.getDeparture());
		assertEquals("DEL", flight2.getArrival());
		assertEquals("united", flight2.getAirlines());
		assertEquals(departureAirport2.getTime(), flight2.getDepartureTime());
		assertEquals(arrivalAirport.getTime(), flight2.getArrivalTime());
		assertEquals(10.0, flight2.getPrice());
		assertEquals(10, flight2.getDuration());
	}
	
	@Test
	public void testGetFlightById() throws URSException {
		String flightId = "id";
		Flight f1 = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.departureTime("2024-12-12 00:00")
				.arrivalTime("2024-12-12 10:00")
				.flightId(flightId)
				.build();
		when(flightRepository.findById(flightId)).thenReturn(Optional.of(f1));
		Flight flight = flightService.getFlightById(flightId);
		assertEquals(f1, flight);
	}
	
	@Test
	public void testGetFlightByIdNotFound() {
		when(flightRepository.findById("id")).thenReturn(Optional.empty());
		URSException exception = assertThrows(URSException.class, () -> flightService.getFlightById("id"));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("Flight with given id does not exist", exception.getMessage());
	}
}
