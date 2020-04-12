package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.builder.ArraySourceReaderBuilder;
import com.apicatalog.projection.builder.ArraySourceWriterBuilder;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public class SourcesPropertyBuilderApi<P> {
	
	ProjectionBuilder<P> projectionBuilder;

	final List<ConversionMappingBuilder> conversionBuilder;

	SourcesBuilderApi<P> sourcesBuilderApi;
	
	SourcePropertyBuilder sourcePropertyBuilder;
	
	ArraySourceReaderBuilder arraySourceReaderBuilder;
	ArraySourceWriterBuilder arraySourceWriterBuilder;
	
	final String projectionPropertyName;
	
	final TypeConversions typeConversions;
	
	Getter targetGetter;
	Setter targetSetter;
	boolean targetReference;
	
	protected SourcesPropertyBuilderApi(ProjectionBuilder<P> projection, String projectionPropertyName, TypeConversions typeConversions) {
		this.projectionBuilder = projection;
		this.conversionBuilder = new ArrayList<>();
		this.sourcePropertyBuilder = SourcePropertyBuilder.newInstance();
		this.arraySourceReaderBuilder = ArraySourceReaderBuilder.newInstance();
		this.arraySourceWriterBuilder = ArraySourceWriterBuilder.newInstance();
		this.projectionPropertyName = projectionPropertyName;
		this.typeConversions = typeConversions;
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

		sourcesBuilderApi = new SourcesBuilderApi<>(projectionBuilder, projectionPropertyName, typeConversions)
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
		sourcePropertyBuilder = sourcePropertyBuilder.targetGetter(targetGetter); 
		return this;
	}

	protected SourcesPropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		this.targetSetter = targetSetter;
		sourcePropertyBuilder = sourcePropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	protected SourcesPropertyBuilderApi<P> targetReference(boolean reference) {
		this.targetReference = reference;
		sourcePropertyBuilder = sourcePropertyBuilder.targetReference(reference);
		return this;
	}

	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry factory) throws ProjectionError {

		if (Optional.ofNullable(sourcesBuilderApi).isEmpty()) {
			return Optional.empty();
		}
		
		final Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size());
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
						
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}

		SourceReader[] sourceReaders = sourcesBuilderApi.buildSourceReaders(typeConversions);
		SourceWriter[] sourceWriters = sourcesBuilderApi.buildSourceWriters(typeConversions);

		Optional<ArraySourceReader> sourceReader = arraySourceReaderBuilder
														.sources(sourceReaders)
														.converters(converters)
														.targetType(targetSetter.getType())
														.build(typeConversions);

		Optional<ArraySourceWriter> sourceWriter = arraySourceWriterBuilder
														.sources(sourceWriters)
														.converters(converters)
														.targetType(targetGetter.getType())
														.build(typeConversions);

		if (sourceReader.isEmpty() && sourceWriter.isEmpty()) {
			return Optional.empty();
		}
				
		sourceReader.ifPresent(reader -> sourcePropertyBuilder.sourceReader(reader));
		sourceWriter.ifPresent(writer -> sourcePropertyBuilder.sourceWriter(writer));
		return sourcePropertyBuilder.build(factory).map(ProjectionProperty.class::cast);
	}
}
