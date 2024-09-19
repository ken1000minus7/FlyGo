package com.unitedgo.payment_service.dto;

import org.hibernate.validator.constraints.Range;

import com.unitedgo.payment_service.entity.Card;
import com.unitedgo.payment_service.validator.ValidExpiry;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CardDTO {
	private int cardId;
	
	@Range(min = 1000000000000000L, max = 9999999999999999L, message = "${card.number}")
	@NotNull(message = "${card.number}")
	private long cardNumber;
	
	@NotNull(message = "${card.name}")
	private String name;
	
	@NotNull(message = "${card.expiry}")
	@ValidExpiry(message = "${card.expiry}")
	private String expiry;
	
	@Range(min = 100, max = 999, message = "${card.cvv}")
	private int cvv;
	
	@NotNull(message = "${card.type}")
	private Card.Type type;
}
