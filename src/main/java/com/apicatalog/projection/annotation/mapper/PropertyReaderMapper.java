package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.builder.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.reader.TargetReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.SourcePropertyReader;
import com.apicatalog.projection.property.source.SingleSourceWriter;
import com.apicatalog.projection.property.target.TargetReader;

public class PropertyReaderMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyReaderMapper.class);
	
	final ProjectionRegistry registry;
	
	final SingleSourceReaderMapper singleSourceMapper;
	final ArraySourceReaderMapper arraySourceMapper;
	
	public PropertyReaderMapper(ProjectionRegistry registry) {
		this.registry = registry;

		this.singleSourceMapper = new SingleSourceReaderMapper(registry);
		this.arraySourceMapper = new ArraySourceReaderMapper(registry);
	}
	
	Optional<PropertyReader> getProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		final Optional<? extends PropertyReader> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = singleSourceMapper.getSourceProperty(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = arraySourceMapper.getSourcesProperty(field, defaultSourceClass);

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = getProvided(field);
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = getConstant(field);

		// direct mapping or a reference
		} else {
			mapping = getDefaultProperty(field, defaultSourceClass);
		}
		
		return mapping.map(PropertyReader.class::cast);
	}

	Optional<PropertyReader> getDefaultProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		if (defaultSourceClass == null) {
			logger.warn("Source class is missing. Property {} is ignored.", field.getName());
			return Optional.empty();				

		}

		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));

		final Optional<TargetReader> targetReader =  
					TargetReaderBuilder.newInstance()
						.getter(targetGetter, PropertyReaderMapper.isReference(targetGetter.getType()))
						.build(registry);
		
		if (targetReader.isEmpty()) {
			logger.warn("Target is not readable. Property {} is ignored.", field.getName());
			return Optional.empty();
		}

		final Optional<SingleSourceWriter> sourceWriter = 
				singleSourceMapper.getSingleSourceWriter(
						defaultSourceClass, 
						field.getName(),
						SingleSourceWriterBuilder.newInstance()
							.objectClass(defaultSourceClass)
							.optional(true)
							.targetType(targetReader.get().getType())
						);
				
		if (sourceWriter.isEmpty()) {
			logger.warn("Source is missing. Property {} is ignored.", field.getName());
			return Optional.empty();
		}
		
		return Optional.of(new SourcePropertyReader(sourceWriter.get(), targetReader.get()));	
	}

	Optional<PropertyReader> getProvided(final Field field) throws ProjectionError {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		return ProvidedPropertyReaderBuilder.newInstance()
					.optional(provided.optional())
					.qualifier(provided.name())
					.targetGetter(targetGetter)
					.targetReference(isReference(targetGetter.getType()))
					.build(registry);
	}				

	Optional<PropertyReader> getConstant(final Field field) {
		// nothing to do, constant is just writable
		return Optional.empty();
	}
	
	protected static final boolean isReference(final ObjectType objectType) {
		return objectType.isCollection()
				? objectType.getComponentType().isAnnotationPresent(Projection.class)
				: objectType.getType().isAnnotationPresent(Projection.class)
				;		
	}	
}