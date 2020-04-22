package com.apicatalog.projection.api.object.impl;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.object.ObjectArraySourceApi;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.api.object.ObjectPropertyApi;
import com.apicatalog.projection.api.object.ObjectProvidedApi;
import com.apicatalog.projection.api.object.ObjectSingleSourceApi;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class PropertyApiImpl<P> implements ObjectPropertyApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(PropertyApiImpl.class);

	final ProjectionApiImpl<P> projectionBuilder;

	final String targetPropertyName;
	
	final boolean targetReference;

	AbstractValueProviderApi<P> valueProvider;
	
	protected PropertyApiImpl(final ProjectionApiImpl<P> projectionBuilder, final String propertyName, final boolean reference) {
		this.projectionBuilder = projectionBuilder;
		this.targetPropertyName = propertyName;
		this.targetReference = reference;
	}
	
	@Override
	public ObjectSingleSourceApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}

	@Override
	public ObjectSingleSourceApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

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
	public ObjectArraySourceApi<P> sources() {
		
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
	public ObjectProvidedApi<P> provided() {
		return provided(null);
	}
	
	@Override
	public ObjectProvidedApi<P> provided(final String qualifier) {
		
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
	public ObjectProjectionApi<P> constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = 
				new ConstantPropertyApi<>(projectionBuilder, values)
						.targetReference(targetReference);
		
		return projectionBuilder;
	}
	
	@Override
	public ObjectPropertyApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName, false);
	}

	@Override
	public ObjectPropertyApi<P> map(String propertyName, boolean reference) {
		return projectionBuilder.map(propertyName, reference);
	}
	
	@Override
	public Projection<P> build(final ProjectionRegistry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}
	
	protected Optional<PropertyReader> buildReader(final ProjectionRegistry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}
		
//		final Field field = ObjectUtils.getProperty(projectionBuilder.getType(), targetPropertyName);
//		
//		final ObjectType targetType = ObjectUtils.getTypeOf(field);
//
//		// extract getter
//		final Getter targetGetter = FieldGetter.from(field, targetType);

		try {
			return valueProvider
						.targetGetter(ObjectUtils.getGetter(projectionBuilder.getType(), targetPropertyName))
						.buildyReader(registry)
						;
		} catch (ObjectError e) {
			throw new ProjectionError("Can not map property " + targetPropertyName + " of " + projectionBuilder.getType().getClass().getCanonicalName() + ".", e);
		}
	}
	
	protected Optional<PropertyWriter> buildWriter(final ProjectionRegistry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}

//		final Field field = ObjectUtils.getProperty(projectionBuilder.getType(), targetPropertyName);
//		
//		final ObjectType targetType = ObjectUtils.getTypeOf(field);
//
//		// extract setter
//		final Setter targetSetter = FieldSetter.from(field, targetType);

		try {
			return valueProvider
						.targetSetter(ObjectUtils.getSetter(projectionBuilder.getType(), targetPropertyName))
						.buildyWriter(registry)
						;
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not map property " + targetPropertyName + " of " + projectionBuilder.getType().getClass().getCanonicalName() + ".", e);
		}
			
	}
}