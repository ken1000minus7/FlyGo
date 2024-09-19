package com.unitedgo.payment_service.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpiryValidator implements ConstraintValidator<ValidExpiry, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.UK);
		try {
			LocalDate date = LocalDate.parse("01/" + value, dateTimeFormatter);
			return date.isAfter(LocalDate.now());
		} catch (Exception e) {
			return false;
		}
	}

}
