package com.unitedgo.payment_service.dto;

import org.hibernate.validator.constraints.Range;

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
public class CardDTO {
	private int cardId;
	
	@Range(min = 1000000000000000L, max = 9999999999999999L)
	private long cardNumber;
	private String name;
	private String expiry;
	
	@Range(min = 100, max = 999)
	private int cvv;
	private Card.Type type;
}
