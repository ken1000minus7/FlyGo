package com.unitedgo.payment_service.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExpiryValidator.class)
public @interface ValidExpiry {
	String message() default "Invalid expiry";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
