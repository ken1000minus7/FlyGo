package com.unitedgo.payment_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.unitedgo.payment_service.dto.PaymentDTO;
import com.unitedgo.payment_service.entity.Card;
import com.unitedgo.payment_service.entity.Payment;
import com.unitedgo.payment_service.model.Booking;
import com.unitedgo.payment_service.model.Booking.Status;
import com.unitedgo.payment_service.repository.CardRepository;
import com.unitedgo.payment_service.repository.PaymentRepository;
import com.unitedgo.payment_service.util.PaymentServiceHelper;
import com.unitedgo.payment_service.util.URSException;

import reactor.core.publisher.Mono;

@Service
public class PaymentService {
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private CardRepository cardRepository;
	
	@Autowired
	private WebClient.Builder webClient;
	
	private static final Double CREDIT_CARD_DISCOUNT = 0.9;
	private static final Double DEBIT_CARD_DISCOUNT = 0.95;
	
	@Transactional
	public PaymentDTO makePayment(String bookingId, Integer cardId, String authHeader) throws URSException {
		String username = PaymentServiceHelper.getUsername();
		Card card = cardRepository.findByCardIdAndUserId(cardId, username).orElseThrow(() -> 
			new URSException("Card does not exist for this user", HttpStatus.NOT_FOUND)
		);
		Booking booking = getBookingDetails(bookingId, authHeader);
		if(booking.getStatus() != Status.PENDING) {
			throw new URSException("Can't make payments for this booking", HttpStatus.BAD_REQUEST);
		}
		Double amountPaid = booking.getTotalAmount() * switch(card.getType()) {
			case CREDIT_CARD -> CREDIT_CARD_DISCOUNT;
			case DEBIT_CARD -> DEBIT_CARD_DISCOUNT;
			default -> 1;
		};
		Payment payment = Payment.builder()
					.bookingId(bookingId)
					.userId(username)
					.totalAmount(booking.getTotalAmount())
					.amountPaid(amountPaid)
					.card(card)
					.build();
		Payment savedPayment = paymentRepository.save(payment);
		confirmBooking(bookingId, authHeader);
		return PaymentDTO.builder()
					.totalAmount(savedPayment.getTotalAmount())
					.amountPaid(savedPayment.getAmountPaid())
					.cardNumber(savedPayment.getCard().getCardNumber())
					.paymentId(savedPayment.getPaymentId())
					.paymentMethod(savedPayment.getCard().getType())
					.bookingId(savedPayment.getBookingId())
					.build();
	}
	
	public Booking getBookingDetails(String bookingId, String authHeader) {
		return webClient.build()
					.get()
					.uri("http://booking-service/urs/booking/" + bookingId)
					.header(PaymentServiceHelper.AUTHORIZATION, authHeader)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, response -> 
						Mono.error(new URSException("Booking does not exist for this user", HttpStatus.NOT_FOUND))
					)
					.onStatus(HttpStatusCode::is5xxServerError, response -> 
						Mono.error(new URSException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE))
					)
					.bodyToMono(Booking.class)
					.block();
	}
	
	public void confirmBooking(String bookingId, String authHeader) {
		webClient.build()
					.put()
					.uri("http://booking-service/urs/booking/" + bookingId + "/confirm")
					.header(PaymentServiceHelper.AUTHORIZATION, authHeader)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, response -> 
						Mono.error(new URSException("Booking does not exist for this user", HttpStatus.NOT_FOUND))
					)
					.onStatus(HttpStatusCode::is5xxServerError, response -> 
						Mono.error(new URSException("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE))
					)
					.bodyToMono(Booking.class)
					.block();
					
	}

}
