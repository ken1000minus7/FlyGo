package com.unitedgo.flights_service.validator;

import java.time.LocalDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDateConstraint, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		try {
			LocalDate date = LocalDate.parse(value);
			return date.isAfter(LocalDate.now());
		} catch (Exception e) {
			return false;
		}
	}
	
}
