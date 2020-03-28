package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.apicatalog.projection.converter.Converter;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Conversion {

	Class<? extends Converter<? extends Object, ? extends Object>> type();
	String[] value() default {};
	
}
