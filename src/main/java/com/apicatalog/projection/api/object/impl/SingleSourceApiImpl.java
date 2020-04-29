package com.apicatalog.projection.api.object.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.Registry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.impl.LambdaConversionApiImpl;
import com.apicatalog.projection.api.object.ObjectPropertyApi;
import com.apicatalog.projection.api.object.ObjectSingleSourceApi;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.reader.SourcePropertyReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.builder.writer.SourcePropertyWriterBuilder;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.PropertyReader;
import com.apicatalog.projection.property.PropertyWriter;
import com.apicatalog.projection.property.source.SourceReader;

public final class SingleSourceApiImpl<P> extends AbstractValueProviderApi<P> implements ObjectSingleSourceApi<P> {
	
	final ProjectionApiImpl<P> projectionBuilder;
	
	final SingleSourceReaderBuilder sourceReaderBuilder;
	final SingleSourceWriterBuilder sourceWriterBuilder;
	
	final List<ConversionMappingBuilder> conversions;
	
	final Class<?> sourceObjectClass;
	final String sourcePropertyName;
	
	Getter targetGetter;
	Setter targetSetter;
	
	String targetProjectionName;
	
	protected SingleSourceApiImpl(final ProjectionApiImpl<P> projectionBuilder, final Class<?> sourceObjectClass, final String sourcePropertyName) {
		this.projectionBuilder = projectionBuilder;
		
		this.sourceReaderBuilder = SingleSourceReaderBuilder.newInstance().objectClass(sourceObjectClass);
		this.sourceWriterBuilder = SingleSourceWriterBuilder.newInstance().objectClass(sourceObjectClass);
		
		this.sourceObjectClass = sourceObjectClass;
		this.sourcePropertyName = sourcePropertyName;
		this.conversions = new ArrayList<>();
	}
	
	@Override
	public ObjectSingleSourceApi<P> optional() {
		sourceReaderBuilder.optional(true);
		sourceWriterBuilder.optional(true);
		return this;
	}

	@Override
	public ObjectSingleSourceApi<P> required() {
		sourceReaderBuilder.optional(false);
		sourceWriterBuilder.optional(false);
		return this;
	}
	
	@Override
	public ObjectSingleSourceApi<P> readOnly() {
		sourceReaderBuilder.mode(AccessMode.READ_ONLY);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	@Override
	public ObjectSingleSourceApi<P> writeOnly() {
		sourceReaderBuilder.mode(AccessMode.WRITE_ONLY);
		sourceWriterBuilder.mode(AccessMode.WRITE_ONLY);
		return this;
	}

	@Override
	public ObjectSingleSourceApi<P> readWrite() {
		sourceReaderBuilder.mode(AccessMode.READ_WRITE);
		sourceWriterBuilder.mode(AccessMode.READ_ONLY);
		return this;
	}

	public ObjectSingleSourceApi<P> qualifier(final String qualifier) {
		
		final String name = StringUtils.isNotBlank(qualifier) ? qualifier : null;
		
		sourceReaderBuilder.qualifier(name);
		sourceWriterBuilder.qualifier(name);
		return this;
	}
	
	@Override
	public ObjectPropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}

	@Override
	public ObjectPropertyApi<P> map(final String propertyName, final boolean reference) {
		return projectionBuilder.map(propertyName, reference);
	}

	@Override
	public ObjectSingleSourceApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}
	
	@Override
	public <S, T> LambdaConversionApi<ObjectSingleSourceApi<P>, S, T> conversion(Class<? extends S> source, Class<? extends T> target) {
		
		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		conversions.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}
	
	@Override
	public Projection<P> build(final Registry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final Registry registry) throws ProjectionError {
			
		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		for (ConversionMappingBuilder cb : conversions) {
			converters.add(cb.build());
		}
			
		sourceWriterBuilder.converters(converters);

		try {
			// extract setter
			final Setter sourceSetter = ObjectUtils.getSetter(sourceObjectClass, sourcePropertyName);
	 
			sourceWriterBuilder
					.setter(sourceSetter);
	
			return SourcePropertyReaderBuilder.newInstance()
						.sourceWriter(sourceWriterBuilder)
						.target(targetGetter)
						.targetProjection(targetProjectionName)
						.build(registry).map(PropertyReader.class::cast);
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get setter for " + sourceObjectClass.getCanonicalName() + "." + sourcePropertyName + ".", e);
		}

	}
	
	@Override
	protected Optional<PropertyWriter> buildyWriter(final Registry registry) throws ProjectionError {

		final Collection<ConverterMapping> converters = new ArrayList<>(conversions.size()*2);
		
		for (ConversionMappingBuilder cb : conversions) {
			converters.add(cb.build());
		}
			
		sourceReaderBuilder.converters(converters);

		try {
			// extract getter
			final Getter sourceGetter = ObjectUtils.getGetter(sourceObjectClass, sourcePropertyName);
			
			final Optional<SourceReader> sourceReader = 
						sourceReaderBuilder
							.getter(sourceGetter)
							.targetType(targetSetter.getType())
							.targetProjection(targetProjectionName)
							.build(registry.getTypeConversions())
								.map(SourceReader.class::cast);
		
			if (sourceReader.isEmpty()) {
				return Optional.empty();
			}
	
			return Optional.ofNullable(SourcePropertyWriterBuilder.newInstance()
						.sourceReader(sourceReader.get())
						.target(targetSetter)
						.targetProjection(targetProjectionName)
						.build(registry)).map(PropertyWriter.class::cast);
			
		} catch (ObjectError e) {
			throw new ProjectionError("Can not get getter for " + sourceObjectClass.getCanonicalName() + "." + sourcePropertyName + ".", e);
		}
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
	protected SingleSourceApiImpl<P> targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;		
	}	
}