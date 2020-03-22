package com.apicatalog.projection.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;

public class ProjectionScanner {

	final Logger logger = LoggerFactory.getLogger(ProjectionScanner.class);
	
	public ProjectionMapping scan(final Class<?> targetProjectionClass) {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(Projection.class)) {
			return null;
		}
		
		final Projection objectProjection = targetProjectionClass.getAnnotation(Projection.class);
		
		final ProjectionMapping metaProjection = new ProjectionMapping(targetProjectionClass);
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("  skipping property {} of {} because is transient or static", field.getName(), targetProjectionClass.getCanonicalName());
					continue;
			}

			final PropertyMapping property = new PropertyMapping(field.getName());
			
			final SourceMapping mapping = new SourceMapping();
			
			// set default source object class
			if (!Class.class.equals(objectProjection.value())) {
				mapping.setObjectClass(objectProjection.value());
			}
			
			// set default source object property name -> use the same name
			mapping.setPropertyName(field.getName());

			// is the property annotated? 
			if (field.isAnnotationPresent(Source.class)) {
				
				final Source source = field.getAnnotation(Source.class);
				
				// override source property class
				if (!Class.class.equals(source.type())) {
					mapping.setObjectClass(source.type());
				}
				// override source property name
				if (StringUtils.isNotBlank(source.value())) {
					mapping.setPropertyName(source.value());
				}
				// set source object qualifier
				if (StringUtils.isNotBlank(source.qualifier())) {
					mapping.setQualifier(source.qualifier());
				}
				// set conversions to apply
				mapping.setFunctions(source.map());
				
				//TODO optional/nullable?
								
			}

			if (mapping.getObjectClass() == null) {
				logger.warn("Source class of property {} of {} is ignored. Use @ObjectProjection(value=...) or @Source(value=...).", field.getName(), targetProjectionClass.getCanonicalName());
				continue;
			}
			
			property.setSources(new SourceMapping[] {mapping});
			
			if (Collection.class.equals(field.getType())) {
				property.setItemClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
			}
			property.setTargetClass(field.getType());			
			
			logger.trace("  found property {}: {}", property.getName(), property.getTargetClass().getCanonicalName());
			metaProjection.add(property);
		}
		
		return metaProjection;
	}
	
}
