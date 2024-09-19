package com.unitedgo.flights_service.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.validation.ConstraintValidatorContext;

@SpringBootTest
public class DateValidatorTest {
	private DateValidator dateValidator = new DateValidator();
	
	@Test
	public void testIsValid() {
		assertTrue(dateValidator.isValid("3030-12-12", mock(ConstraintValidatorContext.class)));
	}
	
	@Test
	public void testIsValidInvalidDate() {
		assertFalse(dateValidator.isValid("2023-12-12", mock(ConstraintValidatorContext.class)));
		assertFalse(dateValidator.isValid("adasdsadds", mock(ConstraintValidatorContext.class)));
	}
}
