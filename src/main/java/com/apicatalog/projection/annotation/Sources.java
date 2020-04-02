/**
 * 
 */
package com.apicatalog.projection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.apicatalog.projection.reducer.std.ArrayCollector;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sources {

	Source[] value();
	
	Reduction reduce() default @Reduction(type = ArrayCollector.class);
	
	Conversion[] map() default {};
		
	boolean optional() default false;
}
