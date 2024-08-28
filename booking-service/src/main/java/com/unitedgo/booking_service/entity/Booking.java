package com.unitedgo.booking_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {
	
	@Id
	private String pnr;
	private String flightId;
	private String userId;
	
	@Enumerated(EnumType.STRING)
	private Booking.Status status;
	private int passengerCount;
	private Double ticketPrice;
	
	public enum Status {
		PENDING, CONFIRMED, CANCELLED
	}
}
