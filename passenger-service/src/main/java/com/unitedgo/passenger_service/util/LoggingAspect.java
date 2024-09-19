package com.unitedgo.passenger_service.util;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Aspect
@Component
@Log4j2
public class LoggingAspect {
	
	@AfterThrowing(pointcut = "within(com.unitedgo.passenger_service.controller.*)", throwing = "exception")
	public void logURSException(URSException exception) {
		log.info("URS Exception thrown");
		log.error("Message: " + exception.getMessage());
	}
}
