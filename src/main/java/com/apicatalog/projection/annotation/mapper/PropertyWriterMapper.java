package com.apicatalog.projection.annotation.mapper;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.Constant;
import com.apicatalog.projection.annotation.Provided;
import com.apicatalog.projection.annotation.Source;
import com.apicatalog.projection.annotation.Sources;
import com.apicatalog.projection.annotation.Visibility;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.ComposerBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.ConstantWriterBuilder;
import com.apicatalog.projection.builder.writer.ProvidedPropertyWriterBuilder;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.SourcePropertyWriter;
import com.apicatalog.projection.property.source.SingleSourceReader;

final class PropertyWriterMapper {

	final Logger logger = LoggerFactory.getLogger(PropertyWriterMapper.class);
	
	final Registry registry;
	
	final SingleSourceWriterMapper singleSourceMapper;
	final ArraySourceWriterMapper arraySourceMapper;
	
	public PropertyWriterMapper(final Registry registry) {
		this.registry = registry;

		this.singleSourceMapper = new SingleSourceWriterMapper(registry);
		this.arraySourceMapper = new ArraySourceWriterMapper(registry);
	}
	
	public Optional<PropertyWriter> getProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Get property {} : {}, object={}", field.getName(), field.getType().getSimpleName(), defaultSourceClass != null ? defaultSourceClass.getSimpleName() : "n/a");
		}
		
		final Optional<? extends PropertyWriter> mapping;
		
		// single source? 
		if (field.isAnnotationPresent(Source.class)) {
			mapping = singleSourceMapper.getSourceProperty(field, defaultSourceClass);

		// multiple sources?
		} else if (field.isAnnotationPresent(Sources.class) ) {
			mapping = arraySourceMapper.getSourcesPropertyMapping(field, defaultSourceClass);

		// provided -> global source
		} else if (field.isAnnotationPresent(Provided.class)) {
			mapping = getProvidedProperty(field);
			
		// constant value
		} else if (field.isAnnotationPresent(Constant.class)) {
			mapping = getConstantProperty(field);

		// direct mapping or a reference
		} else {
			try {
				mapping = getDefaultProperty(field, defaultSourceClass);
				
			} catch (ProjectionError e) {
				// properties in error without explicit mapping are ignored
				return Optional.empty();
			}
		}
		
		if (field.isAnnotationPresent(Visibility.class)) {
			mapping.ifPresent(m -> m.setVisibility(
										IntStream.of(field.getAnnotation(Visibility.class).level())
												.boxed()
												.collect(Collectors.toSet())
							));
		}
		
		return mapping.map(PropertyWriter.class::cast);
	}

	Optional<PropertyWriter> getDefaultProperty(final Field field, final Class<?> defaultSourceClass) throws ProjectionError {

		if (defaultSourceClass == null) {
			throw new ProjectionError("Source class is missing for property + " + field.getName() + ".");
		}

		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		final Optional<String> targetProjectionName = PropertyReaderMapper.getProjectionName(targetSetter.getType());

		final SingleSourceReaderBuilder singleSourceReaderBuilder =
				SingleSourceReaderBuilder.newInstance()
					.objectClass(defaultSourceClass)
					.optional(true)
					.targetType(targetSetter.getType());
		
		targetProjectionName.ifPresent(singleSourceReaderBuilder::targetProjection);
		
		final Optional<SingleSourceReader> sourceReader = 
				singleSourceMapper
					.getSingleSourceReader(
						defaultSourceClass, 
						field.getName(),
						singleSourceReaderBuilder
						)
					.build(registry.getTypeConversions());
		
		if (sourceReader.isEmpty()) {
			return Optional.empty();
		}
		
		final SourcePropertyWriter sourcePropertyWriter = SourcePropertyWriter.newInstance(sourceReader.get(), targetSetter); 
				  
		final ComposerBuilder composerBuilder = ComposerBuilder.newInstance().setter(targetSetter);
				
		targetProjectionName.ifPresent(composerBuilder::targetProjection);
				
		composerBuilder.build(registry).ifPresent(sourcePropertyWriter::setComposer);
	
		return Optional.of(sourcePropertyWriter);			
	}

	Optional<PropertyWriter> getProvidedProperty(final Field field) {
		
		final Provided provided = field.getAnnotation(Provided.class);
			
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));

		final ProvidedPropertyWriterBuilder providedPropertyWriterBuilder =
				ProvidedPropertyWriterBuilder.newInstance()
					.optional(provided.optional())
					.qualifier(provided.name())
					.targetSetter(targetSetter);
		
		PropertyReaderMapper.getProjectionName(targetSetter.getType()).ifPresent(providedPropertyWriterBuilder::targetProjection);
		
		return providedPropertyWriterBuilder.build(registry);
	}				

	Optional<PropertyWriter> getConstantProperty(final Field field) throws ProjectionError {
		
		final Constant constant = field.getAnnotation(Constant.class);
		
		final Setter targetSetter = FieldSetter.from(field, ObjectUtils.getTypeOf(field));
		
		final ConstantWriterBuilder constantWriterBuilder = ConstantWriterBuilder
					.newInstance()
					.constants(constant.value())
					.targetSetter(targetSetter);
				
		
		PropertyReaderMapper.getProjectionName(targetSetter.getType()).ifPresent(constantWriterBuilder::targetProjection);
		
		return constantWriterBuilder.build(registry).map(PropertyWriter.class::cast);
	}	
}
