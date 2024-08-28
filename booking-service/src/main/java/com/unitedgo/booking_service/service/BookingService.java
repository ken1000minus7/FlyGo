package com.unitedgo.booking_service.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.unitedgo.booking_service.dto.BookingDTO;
import com.unitedgo.booking_service.entity.Booking;
import com.unitedgo.booking_service.model.Flight;
import com.unitedgo.booking_service.model.Passenger;
import com.unitedgo.booking_service.model.PassengersResponse;
import com.unitedgo.booking_service.repository.BookingRepository;
import com.unitedgo.booking_service.util.BookingServiceHelper;
import com.unitedgo.booking_service.util.URSException;

import reactor.core.publisher.Mono;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private WebClient.Builder webClient;
	
	private static final String AUTHORIZATION = "Authorization";
	private static final Double GST = 1.18;
	
	@Transactional
	public BookingDTO createBooking(String flightId, String authHeader) throws URSException {
		String username = BookingServiceHelper.getUsername();
		int passengersBooked = bookingRepository.getPassengerCount(flightId).orElse(0);
		Flight flight = getFlightDetails(flightId, authHeader);
		List<Passenger> passengers = getCurrentPassengers(flightId, authHeader);
		
		if(passengers.isEmpty()) {
			throw new URSException("No passengers have been added", HttpStatus.BAD_REQUEST);
		}
		if(passengers.size() + passengersBooked > 200) {
			throw new URSException("Not enough seats available", HttpStatus.BAD_REQUEST);
		}
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.UK);
		LocalDateTime localDateTime = LocalDateTime.parse(flight.getDepartureTime(), dateTimeFormatter);
		if(!localDateTime.isAfter(LocalDateTime.now())) {
			throw new URSException("This flight is no longer available", HttpStatus.BAD_REQUEST);
		}
		
		String pnr = Long.toHexString(System.currentTimeMillis()).toUpperCase();
		Booking booking = Booking.builder()
					.flightId(flightId)
					.pnr(pnr)
					.passengerCount(passengers.size())
					.ticketPrice(flight.getPrice() * GST)
					.status(Booking.Status.PENDING)
					.userId(username)
					.build();
		Booking savedBooking = bookingRepository.save(booking);
		webClient.build()
			.put()
			.uri(uriBuilder -> 
				uriBuilder.host("passenger-service")
					.path("/urs/passenger/book")
					.queryParam("pnr", pnr)
					.queryParam("flightId", flightId)
					.build()
			)
			.header(AUTHORIZATION, authHeader)
			.retrieve()
			.onStatus(HttpStatusCode::is5xxServerError, t -> {
				return Mono.error(new URSException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE));
			})
			.toBodilessEntity()
			.block();
		passengers.forEach(passenger -> passenger.setPnr(pnr));
		return BookingDTO.builder()
					.flight(flight)
					.pnr(pnr)
					.totalAmount(savedBooking.getPassengerCount() * savedBooking.getTicketPrice())
					.passengers(passengers)
					.status(savedBooking.getStatus())
					.build();
	}
	
	public BookingDTO getBookingDetails(String pnr, String authHeader) throws URSException {
		String username = BookingServiceHelper.getUsername();
		Booking booking = bookingRepository.findByPnrAndUserId(pnr, username).orElseThrow(() ->
			new URSException("This PNR does not exist for this account", HttpStatus.NOT_FOUND)
		);
		Flight flight = getFlightDetails(booking.getFlightId(), authHeader);
		List<Passenger> passengers = getBookedPassengers(pnr, authHeader);
		return BookingDTO.builder()
					.flight(flight)
					.pnr(pnr)
					.totalAmount(booking.getTicketPrice() * booking.getPassengerCount())
					.passengers(passengers)
					.status(booking.getStatus())
					.build();
	}
	
	@Transactional
	public void confirmBooking(String pnr) throws URSException {
		String username = BookingServiceHelper.getUsername();
		if(!bookingRepository.existsByPnrAndUserId(pnr, username)) {
			throw new URSException("The booking does not exist for this account", HttpStatus.NOT_FOUND);
		}
		bookingRepository.updateBookingStatus(Booking.Status.CONFIRMED, pnr, username);
	}
	
	@Transactional
	public void cancelBooking(String pnr) throws URSException {
		String username = BookingServiceHelper.getUsername();
		if(!bookingRepository.existsByPnrAndUserId(pnr, username)) {
			throw new URSException("The booking does not exist for this account", HttpStatus.NOT_FOUND);
		}
		bookingRepository.updateBookingStatus(Booking.Status.CANCELLED, pnr, username);
	}
	
	public Flight getFlightDetails(String flightId, String authHeader) {
		return webClient.build()
					.get()
					.uri("http://flights-service/urs/flights/info/" + flightId)
					.header(AUTHORIZATION, authHeader)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, t -> {
						return Mono.error(new URSException("Invalid flight id", HttpStatus.BAD_REQUEST));
					})
					.onStatus(HttpStatusCode::is5xxServerError, t -> {
						return Mono.error(new URSException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE));
					})
					.bodyToMono(Flight.class)
					.block();
	}
	
	public List<Passenger> getCurrentPassengers(String flightId, String authHeader) {
		return webClient.build()
					.get()
					.uri(uriBuilder -> {
						return uriBuilder.host("passenger-service")
							.path("/urs/passenger/flight")
							.queryParam("flightId", flightId)
							.build();
					})
					.header(AUTHORIZATION, authHeader)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, t -> {
						return Mono.error(new URSException("Invalid flight id", HttpStatus.BAD_REQUEST));
					})
					.onStatus(HttpStatusCode::is5xxServerError, t -> {
						return Mono.error(new URSException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE));
					})
					.bodyToMono(PassengersResponse.class)
					.block()
					.getPassengers();
	}
	
	public List<Passenger> getBookedPassengers(String pnr, String authHeader) {
		return webClient.build()
					.get()
					.uri(uriBuilder -> {
						return uriBuilder.host("passenger-service")
							.path("/urs/passenger/booking")
							.queryParam("pnr", pnr)
							.build();
					})
					.header(AUTHORIZATION, authHeader)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, t -> {
						return Mono.error(new URSException("Invalid pnr", HttpStatus.BAD_REQUEST));
					})
					.onStatus(HttpStatusCode::is5xxServerError, t -> {
						return Mono.error(new URSException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE));
					})
					.bodyToMono(PassengersResponse.class)
					.block()
					.getPassengers();
	}
}
