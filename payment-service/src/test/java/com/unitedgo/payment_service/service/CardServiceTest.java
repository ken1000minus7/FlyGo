package com.unitedgo.payment_service.service;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.unitedgo.payment_service.dto.CardDTO;
import com.unitedgo.payment_service.entity.Card;
import com.unitedgo.payment_service.entity.Card.Type;
import com.unitedgo.payment_service.repository.CardRepository;
import com.unitedgo.payment_service.util.URSException;

@SpringBootTest
public class CardServiceTest {
	
	@Mock
	private CardRepository cardRepository;
	
	@InjectMocks
	private CardService cardService;
	
	@BeforeEach
	public void initialize() {
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(authentication.getPrincipal()).thenReturn("user");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}
	
	@Test
	public void testAddCard() throws URSException {
		CardDTO cardDTO = CardDTO.builder()
				.cardNumber(1234)
				.cvv(123)
				.expiry("11/56")
				.name("name")
				.type(Type.CREDIT_CARD)
				.cardId(1)
				.build();
		Card card = Card.builder()
				.cardNumber(cardDTO.getCardNumber())
				.cvv(cardDTO.getCvv())
				.expiry(cardDTO.getExpiry())
				.name(cardDTO.getName())
				.type(cardDTO.getType())
				.cardId(1)
				.userId("user")
				.build();
		when(cardRepository.existsByCardNumberAndUserId(1234, "user")).thenReturn(false);
		when(cardRepository.save(any())).thenReturn(card);
		CardDTO output = cardService.addCard(cardDTO);
		assertEquals(cardDTO, output);
	}
	
	@Test
	public void testAddCardCardAlreadyExists() throws URSException {
		CardDTO cardDTO = CardDTO.builder()
				.cardNumber(1234)
				.cvv(123)
				.expiry("11/56")
				.name("name")
				.type(Type.CREDIT_CARD)
				.cardId(1)
				.build();
		when(cardRepository.existsByCardNumberAndUserId(1234, "user")).thenReturn(true);
		URSException exception = assertThrows(URSException.class, () -> cardService.addCard(cardDTO));
		assertEquals(HttpStatus.CONFLICT, exception.getCode());
		assertEquals("Card already added for this user", exception.getMessage());
	}
}
