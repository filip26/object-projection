package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public final class SourcePropertyApi<P> extends AbstractValueProviderApi<P> {
	
	final ProjectionBuilder<P> projectionBuilder;
	
	final SingleSourceReaderBuilder sourceReaderBuilder;
	final SingleSourceWriterBuilder sourceWriterBuilder;
	
	final List<ConversionMappingBuilder> conversions;
	
	final Class<?> sourceObjectClass;
	final String sourcePropertyName;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	protected SourcePropertyApi(final ProjectionBuilder<P> projectionBuilder, final Class<?> sourceObjectClass, final String sourcePropertyName) {
		this.projectionBuilder = projectionBuilder;
		
		this.sourceReaderBuilder = SingleSourceReaderBuilder.newInstance().objectClass(sourceObjectClass);
		this.sourceWriterBuilder = SingleSourceWriterBuilder.newInstance().objectClass(sourceObjectClass);
		
		this.sourceObjectClass = sourceObjectClass;
		this.sourcePropertyName = sourcePropertyName;
		this.conversions = new ArrayList<>();
	}
	
	public SourcePropertyApi<P> optional() {
		sourceReaderBuilder.optional(true);
		sourceWriterBuilder.optional(true);
		return this;
	}

	public SourcePropertyApi<P> required() {
		sourceReaderBuilder.optional(false);
		sourceWriterBuilder.optional(false);
		return this;
	}
	
	public SourcePropertyApi<P> readOnly() {
		sourceReaderBuilder.mode(AccessMode.READ_ONLY);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	public SourcePropertyApi<P> writeOnly() {
		sourceReaderBuilder.mode(AccessMode.WRITE_ONLY);
		sourceWriterBuilder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	public SourcePropertyApi<P> qualifier(final String qualifier) {
		
		final String name = StringUtils.isNotBlank(qualifier) ? qualifier : null;
		
		sourceReaderBuilder.qualifier(name);
		sourceWriterBuilder.qualifier(name);
		return this;
	}
	
	public PropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	public SourcePropertyApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionError {
			
		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		sourceWriterBuilder.converters(converters);

		final Optional<SourceWriter> sourceWriter = buildSourceWriter(registry.getTypeConversions(), sourceWriterBuilder);
		
		if (sourceWriter.isEmpty()) {
			return Optional.empty();
		}

		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriter.get())
					.target(targetGetter, targetReference)
					.build(registry).map(PropertyReader.class::cast);
	}
	
	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		sourceReaderBuilder.converters(converters);

		final Optional<SourceReader> sourceReader = buildSourceReader(registry.getTypeConversions(), sourceReaderBuilder);
		
		if (sourceReader.isEmpty()) {
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast);
	}
	
	@Override
	protected SourcePropertyApi<P> targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	@Override
	protected SourcePropertyApi<P> targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected SourcePropertyApi<P> targetReference(final boolean targetReference) {
		this.targetReference = targetReference;
		return this;		
	}
	
	Optional<SourceReader> buildSourceReader(final TypeConversions typeConversions, final SingleSourceReaderBuilder sourceBuilder) throws ProjectionError {
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourcePropertyName);
		
		return sourceBuilder.getter(sourceGetter).targetType(targetSetter.getType(), targetReference).build(typeConversions).map(SourceReader.class::cast);
	}	

	Optional<SourceWriter> buildSourceWriter(final TypeConversions typeConversions, final SingleSourceWriterBuilder sourceBuilder) throws ProjectionError {
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourcePropertyName);

		return sourceBuilder.setter(sourceSetter).targetType(targetGetter.getType(), targetReference).build(typeConversions).map(SourceWriter.class::cast);		
	}
}