package com.apicatalog.projection.api.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ArraySourceApi;
import com.apicatalog.projection.api.ProjectionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.PropertyApi;
import com.apicatalog.projection.api.ProvidedApi;
import com.apicatalog.projection.api.SingleSourceApi;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.FieldGetter;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.FieldSetter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class PropertyApiImpl<P> implements PropertyApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(PropertyApiImpl.class);

	final ProjectionBuilderImpl<P> projectionBuilder;

	final String targetPropertyName;
	
	final boolean targetReference;

	AbstractValueProviderApi<P> valueProvider;
	
	protected PropertyApiImpl(final ProjectionBuilderImpl<P> projectionBuilder, final String propertyName, final boolean reference) {
		this.projectionBuilder = projectionBuilder;
		this.targetPropertyName = propertyName;
		this.targetReference = reference;
	}
	
	@Override
	public SingleSourceApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}

	@Override
	public SingleSourceApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

		if (logger.isTraceEnabled()) {
			logger.trace("source({}, {})", sourceClass.getSimpleName(), sourceProperty);
		}
		
		final SingleSourceApiImpl<P> sourcePropertyApi = 
				new SingleSourceApiImpl<>(
					projectionBuilder,
					sourceClass,
					// use the same name if source property name is not present
					StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName
					)
				.targetReference(targetReference);
		
		this.valueProvider = sourcePropertyApi;
		
		return sourcePropertyApi;
	}

	@Override
	public ArraySourceApi<P> sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
		final ArraySourceApiImpl<P> sourcesPropertyApi = 
				new ArraySourceApiImpl<>(projectionBuilder, targetPropertyName)
						.targetReference(targetReference);
		
		this.valueProvider = sourcesPropertyApi;
		
		return sourcesPropertyApi;
	}

	@Override
	public ProvidedApi<P> provided() {
		return provided(null);
	}
	
	@Override
	public ProvidedApi<P> provided(final String qualifier) {
		
		if (logger.isTraceEnabled()) {
			logger.trace("provided({})", qualifier);
		}
		
		final ProvidedApiImpl<P> providedPropertyApi = 
				new ProvidedApiImpl<>(projectionBuilder, StringUtils.isNotBlank(qualifier) ? qualifier : null)
						.targetReference(targetReference);
		
		this.valueProvider = providedPropertyApi;
		
		return providedPropertyApi;
	}

	@Override
	public ProjectionApi<P> constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = 
				new ConstantPropertyApi<>(projectionBuilder, values)
						.targetReference(targetReference);
		
		return projectionBuilder;
	}
	
	@Override
	public PropertyApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName, false);
	}

	@Override
	public PropertyApi<P> map(String propertyName, boolean reference) {
		return projectionBuilder.map(propertyName, reference);
	}
	
	@Override
	public Projection<P> build(final ProjectionRegistry registry) throws ProjectionBuilderError {
		return projectionBuilder.build(registry);
	}
	
	protected Optional<PropertyReader> buildReader(final ProjectionRegistry registry) throws ProjectionBuilderError {

		if (valueProvider == null) {
			return Optional.empty();
		}
		
		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract getter
		final Getter targetGetter = FieldGetter.from(field, targetType);

		return valueProvider
					.targetGetter(targetGetter)
					.buildyReader(registry)
					;
	}
	
	protected Optional<PropertyWriter> buildWriter(final ProjectionRegistry registry) throws ProjectionBuilderError {

		if (valueProvider == null) {
			return Optional.empty();
		}

		final Field field = ObjectUtils.getProperty(projectionBuilder.projectionClass(), targetPropertyName);
		
		final ObjectType targetType = ObjectUtils.getTypeOf(field);

		// extract setter
		final Setter targetSetter = FieldSetter.from(field, targetType);

		return valueProvider
					.targetSetter(targetSetter)
					.buildyWriter(registry)
					;		
	}
}