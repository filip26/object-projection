package com.apicatalog.projection.api.map.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.impl.LambdaConversionApiImpl;
import com.apicatalog.projection.api.map.MapArraySourceItemApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;

public final class MapArraySourceItemApiImpl extends MapProjectionApiWrapper implements MapArraySourceItemApi {
	
	final Logger logger = LoggerFactory.getLogger(MapArraySourceItemApiImpl.class);
	
	final LinkedList<SourceHolder> sourceHolders;
	
	final String targetPropertyName;
	
	protected MapArraySourceItemApiImpl(final MapProjectionBuilderApi projectionApi, final String targetPropertyName) {
		super(projectionApi);
		this.sourceHolders = new LinkedList<>();
		this.targetPropertyName = targetPropertyName;
	}

	public MapArraySourceItemApi optional() {
		sourceHolders.getLast().optional(true);		
		return this;
	}

	public MapArraySourceItemApi required() {
		sourceHolders.getLast().optional(false);
		return this;
	}
	
	public MapArraySourceItemApi readOnly() {
		sourceHolders.getLast().mode(AccessMode.READ_ONLY);
		return this;
	}

	public MapArraySourceItemApi writeOnly() {
		sourceHolders.getLast().mode(AccessMode.WRITE_ONLY);
		return this;
	}

	@Override
	public MapArraySourceItemApi source(final Class<?> sourceClass, final String sourceProperty) {
		sourceHolders.add(
				new SourceHolder(
						sourceClass, 
						StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName)
					);
		
		return this;
	}

	@Override
	public MapArraySourceItemApi source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}

	
	
	@Override
	public MapArraySourceItemApi conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		sourceHolders.getLast().conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}
	
	@Override
	public <S, T> LambdaConversionApi<MapArraySourceItemApi, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		sourceHolders.getLast().conversions.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}

	protected Collection<SingleSourceReaderBuilder> getReaders() throws ProjectionError {
		
		final ArrayList<SingleSourceReaderBuilder> sourceReaders = new ArrayList<>(sourceHolders.size());
		
		for (final SourceHolder holder : sourceHolders) {
			sourceReaders.add(getReader(holder));
		}
		
		return sourceReaders;
	}

	protected Collection<SingleSourceWriterBuilder> getWriters() throws ProjectionError {
		final ArrayList<SingleSourceWriterBuilder> sourceWriters = new ArrayList<>(sourceHolders.size());
		
		for (final SourceHolder holder : sourceHolders) {
			sourceWriters.add(getWriter(holder));
		}
		
		return sourceWriters;
	}

	SingleSourceReaderBuilder getReader(final SourceHolder sourceHolder) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source reader for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		for (ConversionMappingBuilder cb : sourceHolder.conversions) {
			converters.add(cb.build());
		}

		try {
			// extract getter
			final Getter sourceGetter = ObjectUtils.getGetter(sourceHolder.objectClass, sourceHolder.propertyName);
			
			return SingleSourceReaderBuilder.newInstance()
								.objectClass(sourceHolder.objectClass)
								.getter(sourceGetter)
								.converters(converters)
								;
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get getter for " + sourceHolder.objectClass.getCanonicalName() + "." + sourceHolder.propertyName + ".", e);
		}

	}

	SingleSourceWriterBuilder getWriter(final SourceHolder sourceHolder) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source writer for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		for (ConversionMappingBuilder cb : sourceHolder.conversions) {
			converters.add(cb.build());
		}

		try {
			// extract setter
			final Setter sourceSetter = ObjectUtils.getSetter(sourceHolder.objectClass, sourceHolder.propertyName);
			
			return SingleSourceWriterBuilder.newInstance()
								.objectClass(sourceHolder.objectClass)
								.setter(sourceSetter)
								.converters(converters)
								;
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get setter for " + sourceHolder.objectClass.getCanonicalName() + "." + sourceHolder.propertyName + ".", e);
		}
	}
	
	final class SourceHolder {
		
		final List<ConversionMappingBuilder> conversions;
		final Class<?> objectClass;
		final String propertyName;
		
		boolean optional;
		AccessMode mode;
		
		public SourceHolder(final Class<?> objectClass, final String propertyName) {
			this.objectClass = objectClass;
			this.propertyName = propertyName;
			this.conversions = new ArrayList<>(15);
		}

		public void optional(final boolean optional) {
			this.optional = optional;
		}

		public void mode(final AccessMode mode) {
			this.mode = mode;
		}
	}

	@Override
	public Projection<Map<String, Object>> build(Registry registry) throws ProjectionError {
		return projectionBuilder.build(registry);
	}
}
