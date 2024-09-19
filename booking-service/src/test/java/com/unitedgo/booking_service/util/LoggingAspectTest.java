package com.unitedgo.booking_service.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
public class LoggingAspectTest {
	
	@InjectMocks
	private LoggingAspect loggingAspect;
	
	@Test
	public void testLogURSException() {
		assertDoesNotThrow(() -> loggingAspect.logURSException(new URSException("", HttpStatus.OK)));
	}
}
