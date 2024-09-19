package com.unitedgo.flights_service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.unitedgo.flights_service.entity.Flight;
import com.unitedgo.flights_service.model.FlightsResponse;
import com.unitedgo.flights_service.model.flightapi.Airport;
import com.unitedgo.flights_service.model.flightapi.FlightInfo;
import com.unitedgo.flights_service.model.flightapi.Journey;
import com.unitedgo.flights_service.model.flightapi.SearchResponse;
import com.unitedgo.flights_service.service.FlightService;
import com.unitedgo.flights_service.util.URSException;

import reactor.core.publisher.Mono;

@SpringBootTest
public class FlightControllerTest {

	@Mock
	private FlightService flightService;
	
	@InjectMocks
	private FlightController flightController;
	
	@Test
	public void testSearchFlights() {
		WebClient.Builder webClient = mock(WebClient.Builder.class);
		ReflectionTestUtils.setField(flightController, "webClient", webClient);
		when(webClient.baseUrl(any())).thenReturn(webClient);
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		when(uriSpecMock.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(headersSpecMock);
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);
		
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
		List<Journey> journeys1 = List.of(
			Journey.builder()
				.price(10.0)
				.flights(List.of(flightInfo1))
				.totalDuration(10)
				.build()
		);
		List<Journey> journeys2 = List.of(
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
		SearchResponse searchResponse = new SearchResponse(journeys1, journeys2);
		when(responseSpec.bodyToMono(SearchResponse.class)).thenReturn(Mono.just(searchResponse));
		when(flightService.processSearchResults(anyList(), anyString(), anyString())).thenReturn(List.of(f1, f2));
		ResponseEntity<FlightsResponse> responseEntity = flightController.searchFlights("ORD", "DEL", "2024-12-12");
		assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
		List<Flight> flights = responseEntity.getBody().getFlights();
		
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
		when(flightService.getFlightById(flightId)).thenReturn(f1);
		ResponseEntity<Flight> responseEntity = flightController.getFlightById(flightId);
		assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
		assertEquals(f1, responseEntity.getBody());
	}
	
	@Test
	public void testSearchFlightsFallback() {
		URSException ursException = new URSException("m", HttpStatus.NOT_FOUND);
		URSException exception = assertThrows(URSException.class, () -> flightController.searchFlightsFallback(null, null, null, ursException));
		assertEquals(ursException, exception);
	}
	
	@Test
	public void testSearchFlightsFallback500URSException() {
		URSException ursException = new URSException("m", HttpStatus.INTERNAL_SERVER_ERROR);
		URSException exception = assertThrows(URSException.class, () -> flightController.searchFlightsFallback(null, null, null, ursException));
		assertEquals("Cannot search flights right now", exception.getMessage());
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getCode());
	}
	
	@Test
	public void testSearchFlightsFallback500AnyException() {
		Exception e = new Exception("m");
		URSException exception = assertThrows(URSException.class, () -> flightController.searchFlightsFallback(null, null, null, e));
		assertEquals("Cannot search flights right now", exception.getMessage());
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getCode());
	}
}
