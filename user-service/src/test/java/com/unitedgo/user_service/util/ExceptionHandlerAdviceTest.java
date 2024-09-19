
+package com.unitedgo.user_service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
	public void handleUsernameNotFoundException() {
		UsernameNotFoundException exception = new UsernameNotFoundException("message");
		ErrorResponse errorResponse = exceptionHandlerAdvice.handleUsernameException(exception).getBody();
		assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getCode());
		assertEquals("message", errorResponse.getMessage());
	}

}
