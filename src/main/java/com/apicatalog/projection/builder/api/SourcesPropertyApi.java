package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.ArraySourceWriterBuilder;
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
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public final class SourcesPropertyApi<P> extends AbstractValueProviderApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(SourcesPropertyApi.class);
	
	final ProjectionBuilder<P> projectionBuilder;

	final List<ConversionMappingBuilder> conversionBuilder;

	final ArraySourceReaderBuilder arraySourceReaderBuilder;
	final ArraySourceWriterBuilder arraySourceWriterBuilder;
	
	final String projectionPropertyName;

	SourcesApi<P> sourcesBuilderApi;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	protected SourcesPropertyApi(final ProjectionBuilder<P> projection, final String projectionPropertyName) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();

		this.arraySourceReaderBuilder = ArraySourceReaderBuilder.newInstance();
		this.arraySourceWriterBuilder = ArraySourceWriterBuilder.newInstance();
		
		this.projectionPropertyName = projectionPropertyName;
	}

	public SourcesPropertyApi<P> optional() {
		arraySourceReaderBuilder.optional(true);
		arraySourceWriterBuilder.optional(true);
		return this;
	}

	public SourcesPropertyApi<P> required() {
		arraySourceReaderBuilder.optional(false);
		arraySourceWriterBuilder.optional(false);
		return this;
	}

	public SourcesApi<P> source(final Class<?> sourceClass, final String sourceProperty) {

		sourcesBuilderApi = new SourcesApi<>(projectionBuilder, projectionPropertyName)
								.source(sourceClass, sourceProperty);
		return sourcesBuilderApi;
	}

	public SourcesApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, null);
	}
	
	public PropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}
	
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	public SourcesPropertyApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}

		final SourceWriter[] sourceWriters = sourcesBuilderApi.buildWriters(registry.getTypeConversions());

		final Optional<ArraySourceWriter> sourceWriter = arraySourceWriterBuilder
							.sources(sourceWriters)
							.converters(converters)
							.targetType(targetGetter.getType())
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
	protected Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionError {
		
		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}

		final SourceReader[] sourceReaders = sourcesBuilderApi.buildReaders(registry.getTypeConversions());
		
		final Optional<ArraySourceReader> sourceReader = arraySourceReaderBuilder
							.sources(sourceReaders)
							.converters(converters)
							.targetType(targetSetter.getType())
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
	protected SourcesPropertyApi<P> targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	@Override
	protected SourcesPropertyApi<P> targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected SourcesPropertyApi<P> targetReference(final boolean reference) {
		this.targetReference = reference;
		return this;
	}
}
