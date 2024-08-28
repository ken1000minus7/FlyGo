package com.unitedgo.booking_service.dto;

import java.util.List;

import com.unitedgo.booking_service.entity.Booking;
import com.unitedgo.booking_service.model.Flight;
import com.unitedgo.booking_service.model.Passenger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDTO {
	private String pnr;
	private Double totalAmount;
	private Flight flight;
	private List<Passenger> passengers;
	private Booking.Status status;
}
