package com.unitedgo.booking_service.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.UriBuilder;

import com.unitedgo.booking_service.dto.BookingDTO;
import com.unitedgo.booking_service.entity.Booking;
import com.unitedgo.booking_service.model.Flight;
import com.unitedgo.booking_service.model.Passenger;
import com.unitedgo.booking_service.model.PassengersResponse;
import com.unitedgo.booking_service.repository.BookingRepository;
import com.unitedgo.booking_service.util.URSException;

import reactor.core.publisher.Mono;

@SpringBootTest
class BookingServiceTest {
	
	@Mock
	private BookingRepository bookingRepository;
	
	@Mock 
	private WebClient.Builder webClient;
	
	@InjectMocks
	private BookingService bookingService;
	
	@BeforeEach
	void initialize() {
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(authentication.getPrincipal()).thenReturn("user");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}
	
	@Test
	void testCreateBooking() throws URSException {
		Passenger passenger = Passenger.builder()
				.age(10)
				.gender("Male")
				.flightId("id")
				.name("name")
				.build();
		Flight flight = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.flightId("id")
				.departureTime("2032-12-12 00:00")
				.arrivalTime("2032-12-12 10:00")
				.build();
		Booking booking = Booking.builder()
				.flightId("id")
				.passengerCount(1)
				.ticketPrice(flight.getPrice() * BookingService.GST)
				.status(Booking.Status.PENDING)
				.userId("user")
				.build();
		setupWebClientResponse(flight, List.of(passenger));
		when(bookingRepository.save(any())).thenReturn(booking);
		BookingDTO bookingDTO = bookingService.createBooking("id", "");
		
		assertEquals(booking.getFlightId(), bookingDTO.getFlight().getFlightId());
		assertEquals(booking.getTicketPrice() * booking.getPassengerCount(), bookingDTO.getTotalAmount());
		assertEquals(booking.getPassengerCount(), bookingDTO.getPassengers().size());
		assertEquals(booking.getStatus(), bookingDTO.getStatus());
		bookingDTO.getPassengers().forEach(p -> assertEquals(bookingDTO.getPnr(), p.getPnr()));
	}
	
	@Test
	void testCreateBookingNoPassengers() {
		setupWebClientResponse(mock(Flight.class), List.of());
		URSException exception = assertThrows(URSException.class, () -> bookingService.createBooking("id", ""));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
		assertEquals("No passengers have been added", exception.getMessage());
	}
	
	@Test
	void testCreateBookingFlightFull() {
		Passenger passenger = Passenger.builder()
				.age(10)
				.gender("Male")
				.flightId("id")
				.name("name")
				.build();
		Flight flight = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.flightId("id")
				.departureTime("2032-12-12 00:00")
				.arrivalTime("2032-12-12 10:00")
				.build();
		setupWebClientResponse(flight, List.of(passenger));
		when(bookingRepository.getPassengerCount("id")).thenReturn(Optional.of(200));
		URSException exception = assertThrows(URSException.class, () -> bookingService.createBooking("id", ""));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
		assertEquals("Not enough seats available", exception.getMessage());
	}
	
	@Test
	void testCreateBookingFlightNotAvailable() {
		Passenger passenger = Passenger.builder()
				.age(10)
				.gender("Male")
				.flightId("id")
				.name("name")
				.build();
		Flight flight = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.flightId("id")
				.departureTime("2023-12-12 00:00")
				.arrivalTime("2023-12-12 10:00")
				.build();
		setupWebClientResponse(flight, List.of(passenger));
		URSException exception = assertThrows(URSException.class, () -> bookingService.createBooking("id", ""));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
		assertEquals("This flight is no longer available", exception.getMessage());
	}
	
	@Test
	void testGetBookingDetails() throws URSException {
		Passenger passenger = Passenger.builder()
				.age(10)
				.gender("Male")
				.flightId("id")
				.name("name")
				.pnr("pnr")
				.build();
		Flight flight = Flight.builder()
				.airlines("united")
				.departure("ORD")
				.arrival("DEL")
				.price(10.0)
				.duration(10)
				.flightId("id")
				.departureTime("2032-12-12 00:00")
				.arrivalTime("2032-12-12 10:00")
				.build();
		Booking booking = Booking.builder()
				.flightId("id")
				.passengerCount(1)
				.ticketPrice(flight.getPrice() * BookingService.GST)
				.status(Booking.Status.PENDING)
				.userId("user")
				.pnr("pnr")
				.build();
		setupWebClientResponse(flight, List.of(passenger));
		when(bookingRepository.findByPnrAndUserId("pnr", "user")).thenReturn(Optional.of(booking));
		BookingDTO bookingDTO = bookingService.getBookingDetails("pnr", "");
		
		assertEquals(booking.getFlightId(), bookingDTO.getFlight().getFlightId());
		assertEquals(booking.getTicketPrice() * booking.getPassengerCount(), bookingDTO.getTotalAmount());
		assertEquals(booking.getPassengerCount(), bookingDTO.getPassengers().size());
		assertEquals(booking.getStatus(), bookingDTO.getStatus());
		bookingDTO.getPassengers().forEach(p -> assertEquals(bookingDTO.getPnr(), p.getPnr()));
	}
	
	@Test
	void getBookingDetailsBookingDoesNotExist() {
		when(bookingRepository.findByPnrAndUserId("pnr", "user")).thenReturn(Optional.empty());
		URSException exception = assertThrows(URSException.class, () -> bookingService.getBookingDetails("pnr", ""));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("This PNR does not exist for this account", exception.getMessage());
	}
	
	@Test
	void testConfirmBooking() {
		when(bookingRepository.existsByPnrAndUserId("pnr", "user")).thenReturn(true);
		assertDoesNotThrow(() -> bookingService.confirmBooking("pnr"));
	}
	
	@Test
	void testConfirmBookingBookingDoesNotExist() {
		when(bookingRepository.existsByPnrAndUserId("pnr", "user")).thenReturn(false);
		URSException exception = assertThrows(URSException.class, () -> bookingService.confirmBooking("pnr"));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("The booking does not exist for this account", exception.getMessage());
	}
	
	@Test
	void testCancelBooking() {
		when(bookingRepository.existsByPnrAndUserId("pnr", "user")).thenReturn(true);
		assertDoesNotThrow(() -> bookingService.cancelBooking("pnr"));
	}
	
	@Test
	void testCancelBookingBookingDoesNotExist() {
		when(bookingRepository.existsByPnrAndUserId("pnr", "user")).thenReturn(false);
		URSException exception = assertThrows(URSException.class, () -> bookingService.cancelBooking("pnr"));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("The booking does not exist for this account", exception.getMessage());
	}
	
	void setupWebClientResponse(Flight flight, List<Passenger> passengers) {
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);

		RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		when(uriSpecMock.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(headersSpecMock);
		when(uriSpecMock.uri(anyString())).thenReturn(headersSpecMock);
		when(uriSpecMock.uri(any(), ArgumentMatchers.<Object[]>any())).thenReturn(headersSpecMock);

		when(headersSpecMock.header(anyString(), any())).thenReturn(headersSpecMock);
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);

		when(client.put()).thenReturn(requestBodyUriSpec);
		when(requestBodyUriSpec.uri(ArgumentMatchers.<Function<UriBuilder, URI>>any())).thenReturn(requestBodySpec);
		when(requestBodySpec.header(anyString(), any())).thenReturn(requestBodySpec);
		when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		
		when(responseSpec.bodyToMono(PassengersResponse.class)).thenReturn(Mono.just(new PassengersResponse(passengers)));
		when(responseSpec.bodyToMono(Flight.class)).thenReturn(Mono.just(flight));
		when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(mock(ResponseEntity.class)));
	}
}
