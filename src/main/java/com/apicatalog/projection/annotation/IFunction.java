package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.apicatalog.projection.ifnc.InvertibleFunction;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface IFunction {

	Class<? extends InvertibleFunction<?>> type();
	String[] value() default {};
	
}