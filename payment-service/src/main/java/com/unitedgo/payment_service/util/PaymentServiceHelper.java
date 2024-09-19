package com.unitedgo.payment_service.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class PaymentServiceHelper {
	
	public static final String AUTHORIZATION = "Authorization"; 
	
	private PaymentServiceHelper() {
		/* Private Constructor */
	}
	
	public static String getUsername() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
