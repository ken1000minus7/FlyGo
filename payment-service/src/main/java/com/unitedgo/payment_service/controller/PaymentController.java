package com.unitedgo.payment_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.payment_service.dto.PaymentDTO;
import com.unitedgo.payment_service.service.PaymentService;
import com.unitedgo.payment_service.util.PaymentServiceHelper;
import com.unitedgo.payment_service.util.URSException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;

@RestController
@Validated
@Log4j2
@RequestMapping("/urs/payment")
public class PaymentController {
	
	private PaymentService paymentService;
	
	@Autowired
	public PaymentController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}

	@PostMapping
	@CircuitBreaker(name = "paymentService", fallbackMethod = "makePaymentFallback")
	public ResponseEntity<PaymentDTO> makePayment(
			@RequestParam String pnr, 
			@RequestParam Integer cardId,
			@RequestHeader(PaymentServiceHelper.AUTHORIZATION) String authHeader
	) throws URSException {
		PaymentDTO paymentDTO = paymentService.makePayment(pnr, cardId, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentDTO);
	}
	
	public ResponseEntity<?> makePaymentFallback(String pnr, Integer cardId, String authHeader, Exception exception) throws Exception {
		if(exception instanceof URSException && ((URSException) exception).getCode().is4xxClientError()) {
			throw exception;
		}
		log.info("Circuit broken, entering fallback");
		throw new URSException("Cannot make payments right now", HttpStatus.SERVICE_UNAVAILABLE);
	}

}
