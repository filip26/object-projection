package com.apicatalog.projection.api.map.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.api.LambdaConversionApi;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.api.impl.LambdaConversionApiImpl;
import com.apicatalog.projection.api.map.MapArraySourceApi;
import com.apicatalog.projection.api.map.MapArraySourceItemApi;
import com.apicatalog.projection.api.map.MapProjectionBuilderApi;
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

public final class MapArraySourceApiImpl extends AbstractValueProviderApi implements MapArraySourceApi {
	
	final Logger logger = LoggerFactory.getLogger(MapArraySourceApiImpl.class);
	
	final List<ConversionMappingBuilder> conversionBuilder;

	final String projectionPropertyName;

	MapArraySourceItemApiImpl arraySourceItem;
	
	Getter targetGetter;
	Setter targetSetter;
	
	boolean targetReference;
	
	boolean optional;
	
	protected MapArraySourceApiImpl(final MapProjectionBuilderApi projectionApi, final String projectionPropertyName) {
		super(projectionApi);
		this.conversionBuilder = new ArrayList<>();
		
		this.projectionPropertyName = projectionPropertyName;
	}
	
	public MapArraySourceApi optional() {
		this.optional = true;
		return this;
	}

	public MapArraySourceApi required() {
		this.optional = false;
		return this;
	}

	@Override
	public MapArraySourceItemApi source(final Class<?> sourceClass, final String sourceProperty) {

		arraySourceItem = new MapArraySourceItemApiImpl(projectionBuilder, projectionPropertyName);
		
		arraySourceItem.source(sourceClass, sourceProperty);
								
		return arraySourceItem;
	}

	@Override
	public MapArraySourceItemApi source(final Class<?> sourceClass) {
		return source(sourceClass, null);
	}

	@Override
	public MapArraySourceApi conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		conversionBuilder.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	@Override
	protected Optional<PropertyReader> buildyReader(final ProjectionRegistry registry) throws ProjectionError {

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
					.target(targetGetter, targetReference)
					.build(registry).map(PropertyReader.class::cast)
					;		
	}

	@Override
	protected Optional<PropertyWriter> buildyWriter(ProjectionRegistry registry) throws ProjectionError {
		
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
							.targetType(targetSetter.getType(), targetReference)
							.build(registry.getTypeConversions());
		
		if (sourceReader.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Source reader does not exist. Property '{}' is ignored for composition.", targetSetter.getName());
			}
			return Optional.empty();
		}

		return Optional.ofNullable(SourcePropertyWriterBuilder.newInstance()
					.sourceReader(sourceReader.get())
					.target(targetSetter, targetReference)
					.build(registry)).map(PropertyWriter.class::cast)
					;
	}
	
	@Override
	protected MapArraySourceApiImpl targetGetter(final Getter targetGetter) {
		this.targetGetter = targetGetter; 
		return this;
	}

	@Override
	protected MapArraySourceApiImpl targetSetter(final Setter targetSetter) {
		this.targetSetter = targetSetter;
		return this;
	}
	
	@Override
	protected MapArraySourceApiImpl targetReference(final boolean reference) {
		this.targetReference = reference;
		return this;
	}

	@Override
	public <S, T> LambdaConversionApi<MapArraySourceApi, S, T> conversion(Class<? extends S> source,
			Class<? extends T> target) {

		final ConversionMappingBuilder builder = ConversionMappingBuilder.newInstance().types(source, target);
		
		conversionBuilder.add(builder);
		
		return new LambdaConversionApiImpl<>(builder, this);
	}

}
