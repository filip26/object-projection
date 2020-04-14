package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

public class SourcesPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	final List<ConversionMappingBuilder> conversionBuilder;

	final ArraySourceReaderBuilder arraySourceReaderBuilder;
	final ArraySourceWriterBuilder arraySourceWriterBuilder;
	
	final String projectionPropertyName;

	SourcesBuilderApi<P> sourcesBuilderApi;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	protected SourcesPropertyBuilderApi(ProjectionBuilder<P> projection, String projectionPropertyName) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();

		this.arraySourceReaderBuilder = ArraySourceReaderBuilder.newInstance();
		this.arraySourceWriterBuilder = ArraySourceWriterBuilder.newInstance();
		
		this.projectionPropertyName = projectionPropertyName;
	}

	public SourcesPropertyBuilderApi<P> optional() {
		arraySourceReaderBuilder.optional(true);
		arraySourceWriterBuilder.optional(true);
		return this;
	}

	public SourcesPropertyBuilderApi<P> required() {
		arraySourceReaderBuilder.optional(false);
		arraySourceWriterBuilder.optional(false);
		return this;
	}

	public SourcesBuilderApi<P> source(Class<?> sourceClass, String sourceProperty) {

		sourcesBuilderApi = new SourcesBuilderApi<>(projectionBuilder, projectionPropertyName)
								.source(sourceClass, sourceProperty);
		return sourcesBuilderApi;
	}

	public SourcesBuilderApi<P> source(Class<?> sourceClass) {
		return source(sourceClass, null);
	}
	
	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}
	
	public Projection<P> build(ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	public SourcesPropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	protected SourcesPropertyBuilderApi<P> targetGetter(Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	protected SourcesPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	protected SourcesPropertyBuilderApi<P> targetReference(boolean reference) {
		this.targetReference = reference;

		return this;
	}

	public Optional<PropertyReader> buildPropertyReader(ProjectionRegistry registry) throws ProjectionError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}

		SourceWriter[] sourceWriters = sourcesBuilderApi.buildSourceWriters(registry.getTypeConversions());

		Optional<ArraySourceWriter> sourceWriter = arraySourceWriterBuilder
							.sources(sourceWriters)
							.converters(converters)
							.targetType(targetGetter.getType())
							.build(registry.getTypeConversions());

		if (sourceWriter.isEmpty()) {
			return Optional.empty();
		}

		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriter.get())
					.target(targetGetter, targetReference)
					.build(registry).map(PropertyReader.class::cast)
					;		
	}

	public Optional<PropertyWriter> buildPropertyWriter(ProjectionRegistry registry) throws ProjectionError {
		
		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}

		SourceReader[] sourceReaders = sourcesBuilderApi.buildSourceReaders(registry.getTypeConversions());
		
		Optional<ArraySourceReader> sourceReader = arraySourceReaderBuilder
							.sources(sourceReaders)
							.converters(converters)
							.targetType(targetSetter.getType())
							.build(registry.getTypeConversions());
		
		if (sourceReader.isEmpty()) {
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast)
					;
	}
}
