package com.apicatalog.projection.builder.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class PropertyApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(PropertyApi.class);

	final ProjectionBuilder<P> projectionBuilder;

	final String targetPropertyName;
	
	final boolean targetReference;

	AbstractValueProviderApi<P> valueProviderApi;
	
	protected PropertyApi(final ProjectionBuilder<P> projectionBuilder, final String propertyName, final boolean reference) {
		this.projectionBuilder = projectionBuilder;
		this.targetPropertyName = propertyName;
		this.targetReference = reference;
	}
	
	public SourcePropertyApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}

	public SourcePropertyApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

		if (logger.isTraceEnabled()) {
			logger.trace("source({}, {})", sourceClass.getSimpleName(), sourceProperty);
		}
		
		final SourcePropertyApi<P> sourcePropertyApi = 
				new SourcePropertyApi<>(
					projectionBuilder,
					sourceClass,
					// use the same name if source property name is not present
					StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName
					)
				.targetReference(targetReference);
		
		this.valueProviderApi = sourcePropertyApi;
		
		return sourcePropertyApi;
	}

	public SourcesPropertyApi<P> sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
		final SourcesPropertyApi<P> sourcesPropertyApi = 
				new SourcesPropertyApi<>(projectionBuilder, targetPropertyName)
						.targetReference(targetReference);
		
		this.valueProviderApi = sourcesPropertyApi;
		
		return sourcesPropertyApi;
	}

	public ProvidedPropertyApi<P> provided() {
		return provided(null);
	}
	
	public ProvidedPropertyApi<P> provided(final String qualifier) {
		
		if (logger.isTraceEnabled()) {
			logger.trace("provided({})", qualifier);
		}
		
		final ProvidedPropertyApi<P> providedPropertyApi = 
				new ProvidedPropertyApi<>(projectionBuilder)
						.qualifier(StringUtils.isNotBlank(qualifier) ? qualifier : null)
						.targetReference(targetReference);
		
		this.valueProviderApi = providedPropertyApi;
		
		return providedPropertyApi;
	}

	public ProjectionBuilder<P> constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProviderApi = 
				new ConstantPropertyApi<>(projectionBuilder, values)
						.targetReference(targetReference);
		
		return projectionBuilder;
	}
	
	public Projection<P> build(final ProjectionRegistry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}
	
	protected Optional<PropertyReader> buildReader(final ProjectionRegistry registry) throws ProjectionError {

		if (valueProviderApi == null) {
			return Optional.empty();
		}
		
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract getter
		final Getter targetGetter = FieldGetter.from(field, targetType);

		return valueProviderApi
					.targetGetter(targetGetter)
					.buildyReader(registry)
					;
	}
	
	protected Optional<PropertyWriter> buildWriter(final ProjectionRegistry registry) throws ProjectionError {

		if (valueProviderApi == null) {
			return Optional.empty();
		}

		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract setter
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return valueProviderApi
					.targetSetter(targetSetter)
					.buildyWriter(registry)
					;		
	}
}