package com.apicatalog.projection.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionProperty;
import com.apicatalog.projection.PropertyMapping;
import com.apicatalog.projection.annotation.ObjectProjection;
import com.apicatalog.projection.annotation.Source;

public class ProjectionScanner {

	final Logger logger = LoggerFactory.getLogger(ProjectionScanner.class);
	
	public Projection scan(final Class<?> annotatedClass) {
		logger.trace("scan {}", annotatedClass);
		
		if (annotatedClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!annotatedClass.isAnnotationPresent(ObjectProjection.class)) {
			return null;
		}
		
		final ObjectProjection objectProjection = annotatedClass.getAnnotation(ObjectProjection.class);
		
		final Projection projection = new Projection(annotatedClass);
		
		// check all declared fields
		for (Field field : annotatedClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					continue;
			}

			final ProjectionProperty property = new ProjectionProperty(field.getName());
			
			final PropertyMapping mapping = new PropertyMapping();
			
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
				// set value id to reference
				if (StringUtils.isNotBlank(source.id())) {
					mapping.setId(source.id());
				}
				// set conversions to apply
				mapping.setFunctions(source.map());
				
				//TODO optional/nullable?
								
			}
			property.setMapping(new PropertyMapping[] {mapping});
			
			if (Collection.class.equals(field.getType())) {
				property.setItemClass((Class<?>)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
			}
			property.setTargetClass(field.getType());			
			

			projection.add(property);
		}
		
		return projection;
	}
	
}
