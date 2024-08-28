package com.unitedgo.payment_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unitedgo.payment_service.dto.CardDTO;
import com.unitedgo.payment_service.service.CardService;
import com.unitedgo.payment_service.util.URSException;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/urs/payment/card")
public class CardController {
	
	@Autowired
	private CardService cardService;
	
	@PostMapping
	public ResponseEntity<CardDTO> addCard(@RequestBody @Valid CardDTO cardDTO) throws URSException {
		CardDTO savedCardDTO = cardService.addCard(cardDTO);
		return ResponseEntity.ok(savedCardDTO);
	}
}
