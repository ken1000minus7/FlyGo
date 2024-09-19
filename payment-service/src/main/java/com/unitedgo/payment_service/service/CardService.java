package com.unitedgo.payment_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.unitedgo.payment_service.dto.CardDTO;
import com.unitedgo.payment_service.entity.Card;
import com.unitedgo.payment_service.repository.CardRepository;
import com.unitedgo.payment_service.util.PaymentServiceHelper;
import com.unitedgo.payment_service.util.URSException;

@Service
public class CardService {
	
	private CardRepository cardRepository;
	
	@Autowired
	public CardService(CardRepository cardRepository) {
		super();
		this.cardRepository = cardRepository;
	}


	public CardDTO addCard(CardDTO cardDTO) throws URSException {
		String username = PaymentServiceHelper.getUsername();
		if(cardRepository.existsByCardNumberAndUserId(cardDTO.getCardNumber(), username)) {
			throw new URSException("Card already added for this user", HttpStatus.CONFLICT);
		};
		Card card = Card.builder()
				.cardNumber(cardDTO.getCardNumber())
				.cvv(cardDTO.getCvv())
				.expiry(cardDTO.getExpiry())
				.name(cardDTO.getName())
				.type(cardDTO.getType())
				.userId(username)
				.build();
		Card savedCard = cardRepository.save(card);
		return CardDTO.builder()
				.cardId(savedCard.getCardId())
				.cardNumber(savedCard.getCardNumber())
				.cvv(savedCard.getCvv())
				.expiry(savedCard.getExpiry())
				.name(savedCard.getName())
				.type(savedCard.getType())
				.build();
	}
}
