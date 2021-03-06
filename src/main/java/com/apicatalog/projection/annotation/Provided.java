package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Provided {

	boolean optional() default false;
	
	AccessMode mode() default AccessMode.READ_WRITE;
	
	String name() default "";

}
