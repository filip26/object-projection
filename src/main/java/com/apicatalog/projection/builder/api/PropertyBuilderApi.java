package com.apicatalog.projection.builder.api;

import java.lang.reflect.Field;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.writer.ConstantPropertyWriterBuilder;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public class PropertyBuilderApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(PropertyBuilderApi.class);

	final ProjectionBuilder<P> projectionBuilder;
	
	SourcePropertyBuilderApi<P> sourcePropertyBuilder;
	ProvidedPropertyBuilderApi<P> providedPropertyBuilder;
	SourcesPropertyBuilderApi<P> sourcesPropertyBuilder;

	ConstantPropertyWriterBuilder constantBuilder;
	
	String targetPropertyName;
	
	boolean reference;
	
	protected PropertyBuilderApi(ProjectionBuilder<P> projection, String propertyName, boolean reference) {
		this.projectionBuilder = projection;
		this.targetPropertyName = propertyName;
		this.reference = reference;
	}
	
	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass) {
		return source(sourceClass, null);
	}

	public SourcePropertyBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		if (logger.isTraceEnabled()) {
			logger.trace("source({}, {})", sourceClass.getSimpleName(), sourceProperty);
		}
		
		SourcePropertyBuilderApi<P> builder = new SourcePropertyBuilderApi<>(
				projectionBuilder,
				sourceClass,
				// use the same name if source property name is not present
				StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName
				);
		this.sourcePropertyBuilder = builder;
		return builder;
	}

	public SourcesPropertyBuilderApi<P> sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
		SourcesPropertyBuilderApi<P> builder = new SourcesPropertyBuilderApi<>(projectionBuilder, targetPropertyName);
		this.sourcesPropertyBuilder = builder;
		return builder;
	}

	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	public ProvidedPropertyBuilderApi<P> provided() {
		return provided(null);
	}
	
	public ProvidedPropertyBuilderApi<P> provided(String qualifier) {
		ProvidedPropertyBuilderApi<P> builder = new ProvidedPropertyBuilderApi<>(projectionBuilder).qualifier(qualifier);
		this.providedPropertyBuilder = builder;
		return builder;
	}

	public ProjectionBuilder<P> constant(String...values) {
		this.constantBuilder = ConstantPropertyWriterBuilder.newInstance().constants(values);
		
		return projectionBuilder;
	}
	
	protected Optional<PropertyReader> buildPropertyReader(ProjectionRegistry registry) throws ProjectionError {
				
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		if  (Optional.ofNullable(sourcePropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcePropertyBuilder.targetGetter(targetGetter);
			sourcePropertyBuilder.targetReference(reference);
			
			return sourcePropertyBuilder.buildPropertyReader(registry);
			
		} else 	if (Optional.ofNullable(providedPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			providedPropertyBuilder.targetGetter(targetGetter);
			providedPropertyBuilder.targetReference(reference);
			
			return providedPropertyBuilder.buildPropertyReader(registry);
						
		} else 	if  (Optional.ofNullable(sourcesPropertyBuilder).isPresent()) {

			// extract getter
			final Getter targetGetter = FieldGetter.from(field, targetType);

			sourcesPropertyBuilder.targetGetter(targetGetter);
			sourcesPropertyBuilder.targetReference(reference);
			
			return sourcesPropertyBuilder.buildPropertyReader(registry);

		}
		return Optional.empty();
	}
	
	protected Optional<PropertyWriter> buildPropertyWriter(final ProjectionRegistry registry) throws ProjectionError {
		
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract setter
		final Setter targetSetter = FieldSetter.from(field, targetType);
		
		if  (Optional.ofNullable(sourcePropertyBuilder).isPresent()) {

			sourcePropertyBuilder.targetSetter(targetSetter);
			sourcePropertyBuilder.targetReference(reference);
			
			return sourcePropertyBuilder.buildPropertyWriter(registry);
			
		} else 	if (Optional.ofNullable(providedPropertyBuilder).isPresent()) {

			providedPropertyBuilder.targetSetter(targetSetter);
			providedPropertyBuilder.targetReference(reference);
			
			return providedPropertyBuilder.buildPropertyWriter(registry);
			
		} else 	if (Optional.ofNullable(constantBuilder).isPresent()) {
			
			constantBuilder.targetSetter(targetSetter, reference);
			
			return constantBuilder.build(registry).map(PropertyWriter.class::cast);
			
		} else 	if  (Optional.ofNullable(sourcesPropertyBuilder).isPresent()) {

			sourcesPropertyBuilder.targetSetter(targetSetter);
			sourcesPropertyBuilder.targetReference(reference);
			
			return sourcesPropertyBuilder.buildPropertyWriter(registry);

		}
		return Optional.empty();
	}
}
