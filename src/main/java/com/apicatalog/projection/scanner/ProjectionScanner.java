package com.apicatalog.projection.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionProperty;
import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Provider;

public class ProjectionScanner {

	final Logger logger = LoggerFactory.getLogger(ProjectionScanner.class);
	
	public Projection scan(final Class<?> annotatedClass) {
		logger.trace("scan {}", annotatedClass);
		
		if (annotatedClass == null) {
			throw new IllegalArgumentException();
		}
		
		if (!annotatedClass.isAnnotationPresent(ObjectProjection.class)) {
			return null;
		}
		
		final Projection projection = new Projection(annotatedClass);
		
		for (Field field : annotatedClass.getDeclaredFields()) {
			
	       if (java.lang.reflect.Modifier.isStatic(field.getModifiers())
	    		   || !field.isAnnotationPresent(Provider.class)
	    		   ) {
                   continue;
           }
			
			final Provider provider = field.getAnnotation(Provider.class);
								
			final ProjectionProperty property = new ProjectionProperty(field.getName());
			
			if (Collection.class.equals(field.getType())) {
				property.setItemClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
			}
			property.setTargetClass(field.getType());			
			
			property.setProviders(new Provider[] {provider});
			
			projection.add(property);
		}
		
		return projection;
	}
	
}
