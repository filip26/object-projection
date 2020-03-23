/**
 * 
 */
package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Source {

	Class<?> type() default Class.class;
	String value() default "";
	
	String qualifier() default "";
	
	IFunction[] map() default {};

	boolean optional() default false;
	
	int maxDepth() default 1;
}