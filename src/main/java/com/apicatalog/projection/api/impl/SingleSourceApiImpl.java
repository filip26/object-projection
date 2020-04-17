package com.apicatalog.projection.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionBuilderError;
import com.apicatalog.projection.api.PropertyApi;
import com.apicatalog.projection.api.SingleSourceApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;

public final class SingleSourceApiImpl<P> extends AbstractValueProviderApi<P> implements SingleSourceApi<P> {
	
	final ProjectionApiImpl<P> projectionBuilder;
	
	final SingleSourceReaderBuilder sourceReaderBuilder;
	final SingleSourceWriterBuilder sourceWriterBuilder;
	
	final List<ConversionMappingBuilder> conversions;
	
	final Class<?> sourceObjectClass;
	final String sourcePropertyName;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	protected SingleSourceApiImpl(final ProjectionApiImpl<P> projectionBuilder, final Class<?> sourceObjectClass, final String sourcePropertyName) {
		this.projectionBuilder = projectionBuilder;
		
		this.sourceReaderBuilder = SingleSourceReaderBuilder.newInstance().objectClass(sourceObjectClass);
		this.sourceWriterBuilder = SingleSourceWriterBuilder.newInstance().objectClass(sourceObjectClass);
		
		this.sourceObjectClass = sourceObjectClass;
		this.sourcePropertyName = sourcePropertyName;
		this.conversions = new ArrayList<>();
	}
	
	@Override
	public SingleSourceApi<P> optional() {
		sourceReaderBuilder.optional(true);
		sourceWriterBuilder.optional(true);
		return this;
	}

	@Override
	public SingleSourceApi<P> required() {
		sourceReaderBuilder.optional(false);
		sourceWriterBuilder.optional(false);
		return this;
	}
	
	@Override
	public SingleSourceApi<P> readOnly() {
		sourceReaderBuilder.mode(AccessMode.READ_ONLY);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	@Override
	public SingleSourceApi<P> writeOnly() {
		sourceReaderBuilder.mode(AccessMode.WRITE_ONLY);
		sourceWriterBuilder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	@Override
	public SingleSourceApi<P> readWrite() {
		sourceReaderBuilder.mode(AccessMode.READ_WRITE);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	//TODO ?!
	public SingleSourceApi<P> qualifier(final String qualifier) {
		
		final String name = StringUtils.isNotBlank(qualifier) ? qualifier : null;
		
		sourceReaderBuilder.qualifier(name);
		sourceWriterBuilder.qualifier(name);
		return this;
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
	public SingleSourceApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}
	
	@Override
	public <S, T> LambdaConversionApi<SingleSourceApi<P>, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		conversions.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}
	
	@Override
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionBuilderError {
		return projectionBuilder.build(factory);
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionBuilderError {
			
		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}
		
		sourceWriterBuilder.converters(converters);

		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourcePropertyName);
		//TODO null setter

 
		sourceWriterBuilder
				.setter(sourceSetter);

		return SourcePropertyReaderBuilder.newInstance()
					.sourceWriter(sourceWriterBuilder)
					.target(targetGetter, targetReference)
					.build(registry).map(PropertyReader.class::cast);
	}
	
	@Override
	protected Optional<PropertyWriter> buildyWriter(final ProjectionRegistry registry) throws ProjectionBuilderError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		try {
			for (ConversionMappingBuilder cb : conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionBuilderError(e);
		}
		
		sourceReaderBuilder.converters(converters);

		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourcePropertyName);
		//TODO null getter
		
		final Optional<SourceReader> sourceReader = 
					sourceReaderBuilder
						.getter(sourceGetter)
						.targetType(targetSetter.getType(), targetReference)
						.build(registry.getTypeConversions())
							.map(SourceReader.class::cast);
	
		if (sourceReader.isEmpty()) {
			return Optional.empty();
		}

		return SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry).map(PropertyWriter.class::cast);
	}
	
	@Override
	protected SingleSourceApiImpl<P> targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	@Override
	protected SingleSourceApiImpl<P> targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected SingleSourceApiImpl<P> targetReference(final boolean targetReference) {
		this.targetReference = targetReference;
		return this;		
	}	
}