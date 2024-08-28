package com.unitedgo.passenger_service.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

	@ExceptionHandler(URSException.class)
	public ResponseEntity<ErrorResponse> handleURSException(URSException e) {
		return ResponseEntity.status(e.getCode())
				.body(e.toErrorResponse());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
					ErrorResponse.builder()
						.code(HttpStatus.BAD_REQUEST.value())
						.message(e.getMessage())
						.build()
				);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
					ErrorResponse.builder()
						.code(HttpStatus.BAD_REQUEST.value())
						.message(e.getMessage())
						.build()
				);
	}
}
