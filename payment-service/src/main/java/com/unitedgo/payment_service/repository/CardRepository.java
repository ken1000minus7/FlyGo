package com.unitedgo.payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unitedgo.payment_service.entity.Card;


@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
	
	Optional<Card> findByCardIdAndUserId(int cardId, String userId);
	
	boolean existsByCardNumberAndUserId(long cardNumber, String userId);
}
