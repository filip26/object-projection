package com.apicatalog.projection.api.object.impl;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.object.ObjectArraySourceApi;
import com.apicatalog.projection.api.object.ObjectProjectionApi;
import com.apicatalog.projection.api.object.ObjectPropertyApi;
import com.apicatalog.projection.api.object.ObjectProvidedApi;
import com.apicatalog.projection.api.object.ObjectSingleSourceApi;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;

public final class PropertyApiImpl<P> implements ObjectPropertyApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(PropertyApiImpl.class);

	final ProjectionApiImpl<P> projectionBuilder;

	final String targetPropertyName;
	
	final boolean reference;

	AbstractValueProviderApi<P> valueProvider;
	
	protected PropertyApiImpl(final ProjectionApiImpl<P> projectionBuilder, final String propertyName, final boolean reference) {
		this.projectionBuilder = projectionBuilder;
		this.targetPropertyName = propertyName;
		this.reference = reference;
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
					);
		
		this.valueProvider = sourcePropertyApi;
		
		return sourcePropertyApi;
	}

	@Override
	public ObjectArraySourceApi<P> sources() {
		
		if (logger.isTraceEnabled()) {
			logger.trace("sources()");
		}
		
		final ArraySourceApiImpl<P> sourcesPropertyApi = 
				new ArraySourceApiImpl<>(projectionBuilder, targetPropertyName);
		
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
				new ProvidedApiImpl<>(projectionBuilder, StringUtils.isNotBlank(qualifier) ? qualifier : null);		
		
		this.valueProvider = providedPropertyApi;
		
		return providedPropertyApi;
	}

	@Override
	public ObjectProjectionApi<P> constant(final String...values) {

		if (logger.isTraceEnabled()) {
			logger.trace("constant({})", Arrays.toString(values));
		}
		
		this.valueProvider = new ConstantPropertyApi<>(projectionBuilder, values);
		
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
	public Projection<P> build(final Registry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}
	
	protected Optional<PropertyReader> buildReader(final Registry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}

		try {
			final Getter targetGetter = ObjectUtils.getGetter(projectionBuilder.getType(), targetPropertyName);
			
			if (reference) {
				valueProvider.targetProjection(getProjection(targetGetter.getType()));
			}

			return valueProvider
						.targetGetter(targetGetter)
						.buildyReader(registry)
						;
		} catch (ObjectError e) {
			throw new ProjectionError("Can not map property " + targetPropertyName + " of " + projectionBuilder.getType().getClass().getCanonicalName() + ".", e);
		}
	}
	
	protected Optional<PropertyWriter> buildWriter(final Registry registry) throws ProjectionError {

		if (valueProvider == null) {
			return Optional.empty();
		}

		try {
			final Setter targetSetter = ObjectUtils.getSetter(projectionBuilder.getType(), targetPropertyName);
			
			if (reference) {
				valueProvider.targetProjection(getProjection(targetSetter.getType()));
			}
			
			return valueProvider
						.targetSetter(targetSetter)
						.buildyWriter(registry)
						;
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not map property " + targetPropertyName + " of " + projectionBuilder.getType().getClass().getCanonicalName() + ".", e);
		}
			
	}
	
	protected static final String getProjection(ObjectType type) {
		if (type.isArray()) {
			return type.getType().getComponentType().getCanonicalName();
		}
		if (type.isCollection()) {
			return type.getComponentType().getCanonicalName();
			
		}
		return type.getType().getCanonicalName();
	}
	
}