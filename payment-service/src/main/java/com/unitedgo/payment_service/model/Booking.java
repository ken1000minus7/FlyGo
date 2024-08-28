package com.unitedgo.payment_service.model;

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
public class Booking {
	private String pnr;
	private Double totalAmount;
	private Booking.Status status;
	
	public enum Status {
		CANCELLED, PENDING, CONFIRMED
	}
}
