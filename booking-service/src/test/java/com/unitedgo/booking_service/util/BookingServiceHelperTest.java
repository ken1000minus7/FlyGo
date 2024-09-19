package com.unitedgo.booking_service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest
public class BookingServiceHelperTest {

	@Test
	public void testGetUsername() {
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn("user");
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		assertEquals("user", BookingServiceHelper.getUsername());
	}
}
