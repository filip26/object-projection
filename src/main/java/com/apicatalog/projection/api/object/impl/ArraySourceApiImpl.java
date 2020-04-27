package com.apicatalog.projection.api.object.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.impl.LambdaConversionApiImpl;
import com.apicatalog.projection.api.object.ObjectArraySourceApi;
import com.apicatalog.projection.api.object.ObjectArraySourceItemApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ArraySourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.ArraySourceReader;

public final class ArraySourceApiImpl<P> extends AbstractValueProviderApi<P> implements ObjectArraySourceApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(ArraySourceApiImpl.class);
	
	final ProjectionApiImpl<P> projectionBuilder;

	final List<ConversionMappingBuilder> conversionBuilder;

	final String projectionPropertyName;

	ArraySourceItemApiImpl<P> arraySourceItem;
	
	Getter targetGetter;
	Setter targetSetter;
	
	String targetProjectionName;
	
	boolean optional;
	
	protected ArraySourceApiImpl(final ProjectionApiImpl<P> projection, final String projectionPropertyName) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();
		
		this.projectionPropertyName = projectionPropertyName;
	}
	
	public ObjectArraySourceApi<P> optional() {
		this.optional = true;
		return this;
	}

	public ObjectArraySourceApi<P> required() {
		this.optional = false;
		return this;
	}

	@Override
	public ObjectArraySourceItemApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

		arraySourceItem = new ArraySourceItemApiImpl<>(projectionBuilder, projectionPropertyName);
		
		arraySourceItem.source(sourceClass, sourceProperty);
								
		return arraySourceItem;
	}

	@Override
	public ObjectArraySourceItemApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, null);
	}

	@Override
	public ObjectArraySourceApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	@Override
	public <S, T> LambdaConversionApi<ObjectArraySourceApi<P>, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		conversionBuilder.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final Registry registry) throws ProjectionError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		for (ConversionMappingBuilder cb : conversionBuilder) {
			converters.add(cb.build());
		}

		final Collection<SingleSourceWriterBuilder> sourceWriters = arraySourceItem.getWriters();

		final ArraySourceWriterBuilder sourceWriter = 
					ArraySourceWriterBuilder
							.newInstance()
							.sources(sourceWriters)
							.optional(optional)
							.converters(converters)
							;

		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriter)
					.target(targetGetter)
					.targetProjection(targetProjectionName)
					.build(registry).map(PropertyReader.class::cast)
					;		
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(final Registry registry) throws ProjectionError {
		
		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		for (ConversionMappingBuilder cb : conversionBuilder) {
			converters.add(cb.build());
		}

		final Collection<SingleSourceReaderBuilder> sourceReaders = arraySourceItem.getReaders(); 

		final Optional<ArraySourceReader> sourceReader =
				ArraySourceReaderBuilder.newInstance()
							.sources(sourceReaders)
							.optional(optional)
							.converters(converters)
							.targetType(targetSetter.getType())
							.targetProjection(targetProjectionName)
							.build(registry.getTypeConversions());
		
		if (sourceReader.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Source reader does not exist. Property '{}' is ignored for composition.", targetSetter.getName());
			}
			return Optional.empty();
		}

		return Optional.ofNullable(SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter)
					.targetProjection(targetProjectionName)
					.build(registry)).map(PropertyWriter.class::cast)
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
	protected ArraySourceApiImpl<P> targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}
}
