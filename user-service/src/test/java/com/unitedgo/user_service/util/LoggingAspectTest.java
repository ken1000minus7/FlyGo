package com.unitedgo.user_service.util;

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
		loggingAspect.logURSException(new URSException("", HttpStatus.OK));
	}
}
