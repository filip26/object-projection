package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class ProjectionMapper {

	final Logger logger = LoggerFactory.getLogger(ProjectionMapper.class);

	final ProjectionRegistry factory;
	
	final PropertyReaderMapper propertyReaderMapper;
	final PropertyWriterMapper propertyWriterMapper;

	public ProjectionMapper(final ProjectionRegistry registry) {
		this(registry, new PropertyReaderMapper(registry), new PropertyWriterMapper(registry));
	}

	public ProjectionMapper(final ProjectionRegistry factory, final PropertyReaderMapper propertyReaderMapper, final PropertyWriterMapper propertyWriterMapper) {
		this.factory = factory;
		this.propertyReaderMapper = propertyReaderMapper;
		this.propertyWriterMapper = propertyWriterMapper;
	}

	public <P> Projection<P> getProjectionOf(final Class<P> targetProjectionClass) throws ProjectionError {

		if (targetProjectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		// ignore unannotated classes
		if (!targetProjectionClass.isAnnotationPresent(com.apicatalog.projection.annotation.Projection.class)) {
			return null;
		}
		
		logger.debug("Scan {}", targetProjectionClass.getCanonicalName());
		
		final com.apicatalog.projection.annotation.Projection projectionAnnotation = targetProjectionClass.getAnnotation(com.apicatalog.projection.annotation.Projection.class);
		
		final Class<?> defaultSourceClass = Class.class.equals(projectionAnnotation.value()) ? null : projectionAnnotation.value();
		
		final ArrayList<PropertyReader> readers = new ArrayList<>();
		final ArrayList<PropertyWriter> writers = new ArrayList<>();
		
		// check all declared fields
		for (Field field : targetProjectionClass.getDeclaredFields()) {
			
			// skip static and transient fields
			if (Modifier.isStatic(field.getModifiers())
					|| Modifier.isTransient(field.getModifiers())
					) {
					logger.trace("Skipped {}.{} because is transient or static", targetProjectionClass.getSimpleName(), field.getName());
					continue;
			}

			propertyWriterMapper
					.getProperty(field, defaultSourceClass)
					.ifPresent(
							projectionProperty -> {
									logger.trace("Writer {}.{} : {}", targetProjectionClass.getSimpleName(), field.getName(), field.getType().getSimpleName());
									writers.add(projectionProperty);
								}
							);
			
			propertyReaderMapper
					.getProperty(field, defaultSourceClass)
					.ifPresent(
							projectionProperty -> {
									logger.trace("Reader {}.{} : {}", targetProjectionClass.getSimpleName(), field.getName(), field.getType().getSimpleName());
									readers.add(projectionProperty);
								}
							);				

		}
		
		if (writers.isEmpty() && readers.isEmpty()) {
			logger.debug("Ignored {} because has no projected properties", targetProjectionClass.getSimpleName());
			return null;
		}
		
		return Projection.newInstance(
							targetProjectionClass, 
							readers.toArray(new PropertyReader[0]), 
							writers.toArray(new PropertyWriter[0])
							);
	}
}
