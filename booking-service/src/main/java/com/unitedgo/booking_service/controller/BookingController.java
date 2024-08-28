package com.unitedgo.booking_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.booking_service.dto.BookingDTO;
import com.unitedgo.booking_service.service.BookingService;
import com.unitedgo.booking_service.util.URSException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@Validated
@RequestMapping("/urs/booking")
public class BookingController {

	@Autowired
	private BookingService bookingService;
	
	private static final String AUTHORIZATION = "Authorization";
	
	@PostMapping
	public ResponseEntity<BookingDTO> createBooking(@RequestParam String flightId, @RequestHeader(AUTHORIZATION) String authHeader) throws URSException {
		BookingDTO bookingDTO = bookingService.createBooking(flightId, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(bookingDTO);
	}
	
	@GetMapping("/{pnr}")
	public ResponseEntity<BookingDTO> getBookingDetails(@PathVariable String pnr, @RequestHeader(AUTHORIZATION) String authHeader) throws URSException {
		BookingDTO bookingDTO = bookingService.getBookingDetails(pnr, authHeader);
		return ResponseEntity.ok(bookingDTO);
	}
	
	@PutMapping("/{pnr}/confirm")
	public ResponseEntity<?> confirmBooking(@PathVariable String pnr) throws URSException {
		bookingService.confirmBooking(pnr);
		return ResponseEntity.noContent().build();
	}
	
	@PutMapping("/{pnr}/cancel")
	public ResponseEntity<?> cancelBooking(@PathVariable String pnr) throws URSException {
		bookingService.cancelBooking(pnr);
		return ResponseEntity.noContent().build();
	}
	
}
