package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.apicatalog.projection.converter.Converter;

/**
 * Defines a {@link Converter} to be used for conversion of a source property.
 * 
 * @author filip
 *  
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Conversion {

	Class<? extends Converter<?, ?>> type();
	String[] value() default {};
	
}
