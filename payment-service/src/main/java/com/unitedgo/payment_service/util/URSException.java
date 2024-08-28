package com.unitedgo.payment_service.util;

import org.springframework.http.HttpStatus;

public class URSException extends Exception {
	private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
	
	public URSException(String message, HttpStatus statusCode) {
		super(message);
		this.code = statusCode;
	}
	
	public ErrorResponse toErrorResponse() {
		return ErrorResponse.builder()
				.message(this.getMessage())
				.code(this.code.value())
				.build();
	}

	public HttpStatus getCode() {
		return code;
	}

	public void setCode(HttpStatus code) {
		this.code = code;
	}
}
