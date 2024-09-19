package com.unitedgo.payment_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.unitedgo.payment_service.dto.PaymentDTO;
import com.unitedgo.payment_service.entity.Card;
import com.unitedgo.payment_service.entity.Payment;
import com.unitedgo.payment_service.entity.Card.Type;
import com.unitedgo.payment_service.model.Booking;
import com.unitedgo.payment_service.model.Booking.Status;
import com.unitedgo.payment_service.repository.CardRepository;
import com.unitedgo.payment_service.repository.PaymentRepository;
import com.unitedgo.payment_service.util.URSException;

import reactor.core.publisher.Mono;

@SpringBootTest
public class PaymentServiceTest {
	
	@Mock
	private PaymentRepository paymentRepository;
	
	@Mock
	private CardRepository cardRepository;
	
	@Mock
	private WebClient.Builder webClient;
	
	@InjectMocks
	private PaymentService paymentService;
	
	@BeforeEach
	public void initialize() {
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(authentication.getPrincipal()).thenReturn("user");
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
	}
	
	@Test
	public void testMakePayment() throws URSException {
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
		RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		when(client.put()).thenReturn(requestBodyUriSpec);
		
		when(uriSpecMock.uri(anyString())).thenReturn(headersSpecMock);
		when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
		
		when(headersSpecMock.header(anyString(), anyString())).thenReturn(headersSpecMock);
		when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
		
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		when(requestBodySpec.retrieve()).thenReturn(responseSpec);
		
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);
		
		Card card = Card.builder()
				.cardNumber(1234)
				.cvv(123)
				.expiry("11/32")
				.name("name")
				.type(Type.CREDIT_CARD)
				.cardId(1)
				.userId("user")
				.build();
		Booking booking = Booking.builder()
					.pnr("pnr")
					.status(Status.PENDING)
					.totalAmount(100.0)
					.build();
		Payment payment = Payment.builder()
				.bookingId("pnr")
				.userId("user")
				.totalAmount(booking.getTotalAmount())
				.amountPaid(booking.getTotalAmount() * PaymentService.CREDIT_CARD_DISCOUNT)
				.card(card)
				.build();
		
		when(responseSpec.bodyToMono(Booking.class)).thenReturn(Mono.just(booking));
		when(cardRepository.findByCardIdAndUserId(1, "user")).thenReturn(Optional.of(card));
		when(paymentRepository.save(any())).thenReturn(payment);
		
		PaymentDTO paymentDTO = paymentService.makePayment("pnr", 1, "");
		assertEquals(payment.getAmountPaid(), paymentDTO.getAmountPaid());
		assertEquals(payment.getBookingId(), paymentDTO.getBookingId());
		assertEquals(payment.getCard().getCardNumber(), paymentDTO.getCardNumber());
		assertEquals(payment.getTotalAmount(), paymentDTO.getTotalAmount());
		assertEquals(payment.getCard().getType(), paymentDTO.getPaymentMethod());
		
		card.setType(Type.DEBIT_CARD);
		payment.setAmountPaid(booking.getTotalAmount() * PaymentService.DEBIT_CARD_DISCOUNT);
		paymentDTO = paymentService.makePayment("pnr", 1, "");
		assertEquals(payment.getAmountPaid(), paymentDTO.getAmountPaid());
		assertEquals(payment.getBookingId(), paymentDTO.getBookingId());
		assertEquals(payment.getCard().getCardNumber(), paymentDTO.getCardNumber());
		assertEquals(payment.getTotalAmount(), paymentDTO.getTotalAmount());
		assertEquals(payment.getCard().getType(), paymentDTO.getPaymentMethod());
		
	}
	
	@Test
	public void makePaymentCardNotFound() {
		when(cardRepository.findByCardIdAndUserId(1, "user")).thenReturn(Optional.empty());
		URSException exception = assertThrows(URSException.class, () -> paymentService.makePayment("pnr", 1, ""));
		assertEquals(HttpStatus.NOT_FOUND, exception.getCode());
		assertEquals("Card does not exist for this user", exception.getMessage());
	}
	
	@Test
	public void makePaymentInvalidBookingStatus() {
		when(cardRepository.findByCardIdAndUserId(1, "user")).thenReturn(Optional.of(new Card()));
		Booking booking = Booking.builder().status(Status.CANCELLED).build();
		
		WebClient client = mock(WebClient.class);
		when(webClient.build()).thenReturn(client);
		RequestHeadersUriSpec uriSpecMock = mock(RequestHeadersUriSpec.class);
		RequestHeadersSpec headersSpecMock = mock(RequestHeadersSpec.class);
		ResponseSpec responseSpec = mock(ResponseSpec.class);
		
		when(client.get()).thenReturn(uriSpecMock);
		
		when(uriSpecMock.uri(anyString())).thenReturn(headersSpecMock);
		
		when(headersSpecMock.header(anyString(), anyString())).thenReturn(headersSpecMock);
		
		when(headersSpecMock.retrieve()).thenReturn(responseSpec);
		
		when(responseSpec.onStatus(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(responseSpec);
		
		when(responseSpec.bodyToMono(Booking.class)).thenReturn(Mono.just(booking));
		
		URSException exception = assertThrows(URSException.class, () -> paymentService.makePayment("pnr", 1, ""));
		assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
		assertEquals("Can't make payments for this booking", exception.getMessage());
	}
}
