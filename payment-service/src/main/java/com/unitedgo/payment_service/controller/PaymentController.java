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

@RestController
@Validated
@RequestMapping("/urs/payment")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@PostMapping
	public ResponseEntity<PaymentDTO> makePayment(
			@RequestParam String pnr, 
			@RequestParam Integer cardId,
			@RequestHeader(PaymentServiceHelper.AUTHORIZATION) String authHeader
	) throws URSException {
		PaymentDTO paymentDTO = paymentService.makePayment(pnr, cardId, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentDTO);
	}

}
