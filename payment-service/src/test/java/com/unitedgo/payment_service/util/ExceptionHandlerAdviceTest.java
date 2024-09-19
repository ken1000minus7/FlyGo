package com.unitedgo.payment_service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class ExceptionHandlerAdviceTest {
	
	@InjectMocks
	private ExceptionHandlerAdvice exceptionHandlerAdvice;
	
	@Test
	public void testHandleURSException() {
		URSException exception = new URSException("test exception", HttpStatus.ACCEPTED);
		exception.setCode(HttpStatus.CREATED);
		ResponseEntity<ErrorResponse> response = exceptionHandlerAdvice.handleURSException(exception);
		ErrorResponse errorResponse = response.getBody();
		assertEquals(exception.getMessage(), errorResponse.getMessage());
		assertEquals(exception.getCode().value(), errorResponse.getCode());
	}
	
	@Test 
	public void testHandleValidationException() {
		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(new MethodParameter(Mockito.mock(Method.class), -1), Mockito.mock(BindingResult.class));
		ErrorResponse errorResponse = exceptionHandlerAdvice.handleValidationException(exception).getBody();
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertTrue(exception.getMessage().startsWith("Validation failed for argument"));
	}
	
	@Test 
	public void testHandleConstraintViolationException() {
		ConstraintViolationException exception= new ConstraintViolationException("message", Set.of());
		ErrorResponse errorResponse = exceptionHandlerAdvice.handleConstraintViolationException(exception).getBody();
		assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getCode());
		assertEquals("message", errorResponse.getMessage());
	}

}
