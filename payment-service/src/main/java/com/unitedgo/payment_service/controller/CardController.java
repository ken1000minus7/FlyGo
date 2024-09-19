package com.unitedgo.payment_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.payment_service.dto.CardDTO;
import com.unitedgo.payment_service.service.CardService;
import com.unitedgo.payment_service.util.URSException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;

@RestController
@Validated
@Log4j2
@RequestMapping("/urs/payment/card")
public class CardController {
	
	private CardService cardService;
	
	@Autowired
	public CardController(CardService cardService) {
		super();
		this.cardService = cardService;
	}

	@PostMapping
	@CircuitBreaker(name = "paymentService", fallbackMethod = "addCardFallback")
	public ResponseEntity<CardDTO> addCard(@RequestBody @Valid CardDTO cardDTO) throws URSException {
		CardDTO savedCardDTO = cardService.addCard(cardDTO);
		return ResponseEntity.ok(savedCardDTO);
	}
	
	public ResponseEntity<?> addCardFallback(CardDTO cardDTO, Exception exception) throws Exception {
		if(exception instanceof URSException && ((URSException) exception).getCode().is4xxClientError()) {
			throw exception;
		}
		log.info("Circuit broken, entering fallback");
		throw new URSException("Can't add cards right now", HttpStatus.SERVICE_UNAVAILABLE);
	}
}
