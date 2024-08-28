package com.unitedgo.booking_service.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class BookingServiceHelper {
	
	public static String getUsername() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
