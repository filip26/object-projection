package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Projection;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.ExtractorBuilder;
import com.apicatalog.projection.builder.reader.ProvidedPropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.SourcePropertyReader;
import com.apicatalog.projection.property.source.SourceWriter;

final class PropertyReaderMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyReaderMapper.class);
	
	final Registry registry;
	
	final SingleSourceReaderMapper singleSourceMapper;
	final ArraySourceReaderMapper arraySourceMapper;
	
	public PropertyReaderMapper(final Registry registry) {
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
			
		} else if (field.isAnnotationPresent(Constant.class)) {
			
			// ignore
			mapping = Optional.empty();
			
		// direct mapping or a reference
		} else {
			try {
				mapping = getDefaultProperty(field, defaultSourceClass);
				
			} catch (ProjectionError e) {
				// properties in error without explicit mapping are ignored
				return Optional.empty();
			}				
		}
		
		return mapping.map(PropertyReader.class::cast);
	}

	Optional<PropertyReader> getDefaultProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		if (defaultSourceClass == null) {
			throw new ProjectionError("Source class is missing for property + " + field.getName() + ".");				
		}

		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final SingleSourceWriterBuilder singleSourceWriterBuilder = 
					SingleSourceWriterBuilder.newInstance()
						.objectClass(defaultSourceClass)
						.optional(true)
						.targetType(targetGetter.getType());
		
		Optional<String> projectionName = PropertyReaderMapper.getProjectionName(targetGetter.getType());
		
		projectionName.ifPresent(singleSourceWriterBuilder::targetProjection);
		
		final Optional<SourceWriter> sourceWriter =  
				singleSourceMapper
					.getSingleSourceWriter(
						defaultSourceClass, 
						field.getName(),
						singleSourceWriterBuilder
						)
					.build(registry.getTypeConversions());
				
		if (sourceWriter.isEmpty()) {
			return Optional.empty();
		}
		
		final SourcePropertyReader sourcePropertyReader = SourcePropertyReader.newInstance(sourceWriter.get(), targetGetter);
  
		final ExtractorBuilder extractorBuilder = 
				ExtractorBuilder.newInstance()
					.getter(targetGetter);
		
		projectionName.ifPresent(extractorBuilder::targetProjection);
		
		extractorBuilder.build(registry).ifPresent(sourcePropertyReader::setExtractor);
			
		return Optional.of(sourcePropertyReader);	
	}

	Optional<PropertyReader> getProvided(final Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Getter targetGetter = FieldGetter.from(field, ObjectUtils.getTypeOf(field));
		
		final ProvidedPropertyReaderBuilder builder = ProvidedPropertyReaderBuilder.newInstance()
					.optional(provided.optional())
					.qualifier(provided.name())
					.targetGetter(targetGetter);
		
		getProjectionName(targetGetter.getType()).ifPresent(builder::targetProjection);
		
		return builder.build(registry);
	}				
	
//	protected static final boolean isReference(final ObjectType objectType) {
//		return (objectType.isCollection() && objectType.getComponentType().isAnnotationPresent(Projection.class))
//				|| (objectType.isArray() &&  objectType.getType().getComponentType().isAnnotationPresent(Projection.class))
//				|| objectType.getType().isAnnotationPresent(Projection.class)
//				;		
//	}
	
	protected static final Optional<String> getProjectionName(final ObjectType objectType) {
		if (objectType.isCollection() && objectType.getComponentType().isAnnotationPresent(Projection.class)) {
			return Optional.of(objectType.getComponentType().getCanonicalName());
		}
		
		if ((objectType.isArray() &&  objectType.getType().getComponentType().isAnnotationPresent(Projection.class))) {
			return Optional.of(objectType.getType().getComponentType().getCanonicalName());			
		}
		
		if (objectType.getType().isAnnotationPresent(Projection.class)) {
			return Optional.of(objectType.getType().getCanonicalName());
		}
		
		return Optional.empty();
	}
}
