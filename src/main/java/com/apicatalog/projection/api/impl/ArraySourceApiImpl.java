package com.apicatalog.projection.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.ArraySourceApi;
import com.apicatalog.projection.api.ArraySourceItemApi;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ArraySourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.ArraySourceWriter;

public final class ArraySourceApiImpl<P> extends AbstractValueProviderApi<P> implements ArraySourceApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(ArraySourceApiImpl.class);
	
	final ProjectionApiImpl<P> projectionBuilder;

	final List<ConversionMappingBuilder> conversionBuilder;

	final String projectionPropertyName;

	ArraySourceItemApiImpl<P> arraySourceItem;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	boolean optional;
	
	protected ArraySourceApiImpl(final ProjectionApiImpl<P> projection, final String projectionPropertyName) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();
		
		this.projectionPropertyName = projectionPropertyName;
	}
	
	public ArraySourceApi<P> optional() {
		this.optional = true;
		return this;
	}

	public ArraySourceApi<P> required() {
		this.optional = false;
		return this;
	}

	@Override
	public ArraySourceItemApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

		arraySourceItem = new ArraySourceItemApiImpl<>(projectionBuilder, projectionPropertyName);
		
		arraySourceItem.source(sourceClass, sourceProperty);
								
		return arraySourceItem;
	}

	@Override
	public ArraySourceItemApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, null);
	}

	@Override
	public ArraySourceApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	@Override
	public <S, T> LambdaConversionApi<ArraySourceApi<P>, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		conversionBuilder.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionBuilderError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}

		final Collection<SingleSourceWriterBuilder> sourceWriters = arraySourceItem.getWriters();

		final Optional<ArraySourceWriter> sourceWriter = 
					ArraySourceWriterBuilder
							.newInstance()
							.sources(sourceWriters)
							.optional(optional)
							.converters(converters)
							.targetType(targetGetter.getType(), targetReference)
							.build(registry.getTypeConversions());

		if (sourceWriter.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Source writer does not exist. Property '{}' is ignored for extraction.", targetGetter.getName());
			}
			return Optional.empty();
		}

		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriter.get())
					.target(targetGetter, targetReference)
					.build(registry).map(PropertyReader.class::cast)
					;		
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionBuilderError {
		
		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}

		final Collection<SingleSourceReaderBuilder> sourceReaders = arraySourceItem.getReaders(); 

		final Optional<ArraySourceReader> sourceReader =
				ArraySourceReaderBuilder.newInstance()
							.sources(sourceReaders)
							.optional(optional)
							.converters(converters)
							.targetType(targetSetter.getType(), targetReference)
							.build(registry.getTypeConversions());
		
		if (sourceReader.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Source reader does not exist. Property '{}' is ignored for composition.", targetSetter.getName());
			}
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast)
					;
	}
	
	@Override
	protected ArraySourceApiImpl<P> targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	@Override
	protected ArraySourceApiImpl<P> targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected ArraySourceApiImpl<P> targetReference(final boolean reference) {
		this.targetReference = reference;
		return this;
	}
}
