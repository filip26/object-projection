package com.apicatalog.projection.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.api.ArraySourceItemApi;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.PropertyApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;

public final class ArraySourceItemApiImpl<P> implements ArraySourceItemApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(ArraySourceItemApiImpl.class);
	
	final ProjectionBuilderImpl<P> projectionBuilder;

	final LinkedList<SourceHolder> sourceHolders;
	
	final String targetPropertyName;
	
	protected ArraySourceItemApiImpl(final ProjectionBuilderImpl<P> projection, final String targetPropertyName) {
		this.projectionBuilder = projection;
		this.sourceHolders = new LinkedList<>();
		this.targetPropertyName = targetPropertyName;
	}

	public ArraySourceItemApi<P> optional() {
		sourceHolders.getLast().optional(true);		
		return this;
	}

	public ArraySourceItemApi<P> required() {
		sourceHolders.getLast().optional(false);
		return this;
	}
	
	public ArraySourceItemApi<P> readOnly() {
		sourceHolders.getLast().mode(AccessMode.READ_ONLY);
		return this;
	}

	public ArraySourceItemApi<P> writeOnly() {
		sourceHolders.getLast().mode(AccessMode.WRITE_ONLY);
		return this;
	}

	@Override
	public ArraySourceItemApi<P> source(final Class<?> sourceClass, final String sourceProperty) {
		sourceHolders.add(
				new SourceHolder(
						sourceClass, 
						StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName)
					);
		
		return this;
	}

	@Override
	public ArraySourceItemApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}
		
	@Override
	public PropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	@Override
	public PropertyApi<P> map(final String propertyName, final boolean reference) {
		return projectionBuilder.map(propertyName, reference);
	}
	
	@Override
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionBuilderError {
		return projectionBuilder.build(factory);
	}

	@Override
	public ArraySourceItemApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		sourceHolders.getLast().conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}
	
	@Override
	public <S, T> LambdaConversionApi<ArraySourceItemApi<P>, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		sourceHolders.getLast().conversions.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}

	protected Collection<SingleSourceReaderBuilder> getReaders() throws ProjectionBuilderError {
		
		final ArrayList<SingleSourceReaderBuilder> sourceReaders = new ArrayList<>(sourceHolders.size());
		
		for (final SourceHolder holder : sourceHolders) {
			sourceReaders.add(getReader(holder));
		}
		
		return sourceReaders;
	}

	protected Collection<SingleSourceWriterBuilder> getWriters() throws ProjectionBuilderError {
		final ArrayList<SingleSourceWriterBuilder> sourceWriters = new ArrayList<>(sourceHolders.size());
		
		for (final SourceHolder holder : sourceHolders) {
			sourceWriters.add(getWriter(holder));
		}
		
		return sourceWriters;
	}

	SingleSourceReaderBuilder getReader(final SourceHolder sourceHolder) throws ProjectionBuilderError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source reader for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		try {
			
			for (ConversionMappingBuilder cb : sourceHolder.conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}

		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceHolder.objectClass, sourceHolder.propertyName);
		
		return SingleSourceReaderBuilder.newInstance()
							.objectClass(sourceHolder.objectClass)
							.getter(sourceGetter)
							.converters(converters)
							;	
	}

	SingleSourceWriterBuilder getWriter(final SourceHolder sourceHolder) throws ProjectionBuilderError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source writer for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		try {
			
			for (ConversionMappingBuilder cb : sourceHolder.conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceHolder.objectClass, sourceHolder.propertyName);
		
		return SingleSourceWriterBuilder.newInstance()
							.objectClass(sourceHolder.objectClass)
							.setter(sourceSetter)
							.converters(converters)
							;	
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

}
