package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.SourcePropertyBuilder;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.ProjectionProperty;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public class SourcePropertyBuilderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	final SingleSourceReaderBuilder sourceReaderBuilder;
	final SingleSourceWriterBuilder sourceWriterBuilder;
	
	SourcePropertyBuilder sourcePropertyBuilder;
	
	List<ConversionMappingBuilder> conversionBuilder;
	
	Class<?> sourceObjectClass;
	String sourcePropertyName;
	
	final TypeConversions typeConversions;
	
	protected SourcePropertyBuilderApi(ProjectionBuilder<P> projectionBuilder, Class<?> sourceObjectClass, String sourcePropertyName, TypeConversions typeConversions) {
		this.projectionBuilder = projectionBuilder;
		this.typeConversions = typeConversions;
		
		this.sourceReaderBuilder = SingleSourceReaderBuilder.newInstance().objectClass(sourceObjectClass);
		this.sourceWriterBuilder = SingleSourceWriterBuilder.newInstance().objectClass(sourceObjectClass);
		
		this.sourcePropertyBuilder = SourcePropertyBuilder.newInstance();
		this.sourceObjectClass = sourceObjectClass;
		this.sourcePropertyName = sourcePropertyName;
		this.conversionBuilder = new ArrayList<>();
	}
	
	public SourcePropertyBuilderApi<P> optional() {
		sourceReaderBuilder.optional(true);
		sourceWriterBuilder.optional(true);
		return this;
	}

	public SourcePropertyBuilderApi<P> required() {
		sourceReaderBuilder.optional(false);
		sourceWriterBuilder.optional(false);
		return this;
	}
	
	public SourcePropertyBuilderApi<P> readOnly() {
		sourceReaderBuilder.mode(AccessMode.READ_ONLY);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);

		sourcePropertyBuilder = sourcePropertyBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	public SourcePropertyBuilderApi<P> writeOnly() {
		sourceReaderBuilder.mode(AccessMode.WRITE_ONLY);
		sourceWriterBuilder.mode(AccessMode.WRITE_ONLY);

		sourcePropertyBuilder = sourcePropertyBuilder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	public SourcePropertyBuilderApi<P> qualifier(String qualifier) {
		sourceReaderBuilder.qualifier(qualifier);
		sourceWriterBuilder.qualifier(qualifier);
		return this;
	}
	
	protected SourcePropertyBuilderApi<P> targetGetter(Getter targetGetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetGetter(targetGetter); 
		return this;
	}

	protected SourcePropertyBuilderApi<P> targetSetter(Setter targetSetter) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetSetter(targetSetter);
		return this;
	}
	
	protected SourcePropertyBuilderApi<P> targetReference(boolean targetReference) {
		sourcePropertyBuilder = sourcePropertyBuilder.targetReference(targetReference);
		return this;		
	}
	
	public MappedPropertyBuilderApi<P> map(String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public Projection<P> build(ProjectionRegistry factory, TypeAdaptersLegacy typeAdapters) throws ProjectionError {
		return projectionBuilder.build(factory, typeAdapters);
	}

	public SourcePropertyBuilderApi<P> conversion(Class<? extends Converter<?, ?>> converter, String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	protected Optional<SourceReader> buildSourceReader(TypeConversions typeConversions, SingleSourceReaderBuilder sourceBuilder) {
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourcePropertyName);
		
		return sourceBuilder.getter(sourceGetter).build(typeConversions).map(SourceReader.class::cast);
	}	

	protected Optional<SourceWriter> buildSourceWriter(TypeConversions typeConversions, SingleSourceWriterBuilder sourceBuilder) {
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourcePropertyName);
		
		return sourceBuilder.setter(sourceSetter).build(typeConversions).map(SourceWriter.class::cast);		
	}	

	protected Optional<ProjectionProperty> buildProperty(ProjectionRegistry factory, TypeAdaptersLegacy typeAdapters) throws ProjectionError {

		Collection<ConverterMapping> converters = new ArrayList<>(conversionBuilder.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversionBuilder) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		sourceReaderBuilder.converters(converters);
		sourceWriterBuilder.converters(converters);

		final Optional<SourceReader> sourceReader = buildSourceReader(typeConversions, sourceReaderBuilder);
		final Optional<SourceWriter> sourceWriter = buildSourceWriter(typeConversions, sourceWriterBuilder);
		
		if (sourceReader.isEmpty() && sourceWriter.isEmpty()) {
			return Optional.empty();
		}
		
		sourceReader.ifPresent(reader -> sourcePropertyBuilder.sourceReader(reader));
		sourceWriter.ifPresent(writer -> sourcePropertyBuilder.sourceWriter(writer));

		return sourcePropertyBuilder.build(factory, typeAdapters).map(ProjectionProperty.class::cast);
	}
}
