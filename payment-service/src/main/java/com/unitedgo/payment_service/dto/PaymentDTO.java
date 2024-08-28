package com.unitedgo.payment_service.dto;

import com.unitedgo.payment_service.entity.Card;

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
public class PaymentDTO {
	
	private int paymentId;
	private Double totalAmount;
	private Double amountPaid;
	private String bookingId;
	private Card.Type paymentMethod;
	private long cardNumber;
}
