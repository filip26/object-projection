package com.apicatalog.projection.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;

@Target(FIELD)
public @interface ProjectionConstraint {
	
	int visibleToDepth() default 1;
	boolean notNull() default false;
	
}
