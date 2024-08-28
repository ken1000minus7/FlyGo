package com.unitedgo.payment_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int cardId;
	
	private long cardNumber;
	private String name;
	private String expiry;
	private int cvv;
	private String userId;
	
	@Enumerated(EnumType.STRING)
	private Card.Type type;
	
	public enum Type {
		CREDIT_CARD, DEBIT_CARD
	}
}
