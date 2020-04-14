package com.apicatalog.projection.builder.api;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Projection;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.builder.ConversionMappingBuilder;
import com.apicatalog.projection.builder.reader.SingleSourceReaderBuilder;
import com.apicatalog.projection.builder.writer.SingleSourceWriterBuilder;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SourceReader;
import com.apicatalog.projection.property.source.SourceWriter;

public final class SourcesApi<P> {
	
	final Logger logger = LoggerFactory.getLogger(SourcesApi.class);
	
	final ProjectionBuilder<P> projectionBuilder;

	final LinkedList<SourceHolder> sourceHolders;
	
	final String targetPropertyName;
	
	protected SourcesApi(final ProjectionBuilder<P> projection, final String targetPropertyName) {
		this.projectionBuilder = projection;
		this.sourceHolders = new LinkedList<>();
		this.targetPropertyName = targetPropertyName;
	}

	public SourcesApi<P> optional() {
		sourceHolders.getLast().optional(true);		
		return this;
	}

	public SourcesApi<P> required() {
		sourceHolders.getLast().optional(false);
		return this;
	}
	
	public SourcesApi<P> readOnly() {
		sourceHolders.getLast().mode(AccessMode.READ_ONLY);
		return this;
	}

	public SourcesApi<P> writeOnly() {
		sourceHolders.getLast().mode(AccessMode.WRITE_ONLY);
		return this;
	}

	public SourcesApi<P> source(final Class<?> sourceClass, final String sourceProperty) {
		sourceHolders.add(
				new SourceHolder(
						sourceClass, 
						StringUtils.isNotBlank(sourceProperty) ? sourceProperty : targetPropertyName)
					);
		
		return this;
	}

	public SourcesApi<P> source(final Class<?> sourceClass) {
		return source(sourceClass, targetPropertyName);
	}
	
	public PropertyApi<P> map(final String propertyName) {
		return projectionBuilder.map(propertyName);
	}
	
	public Projection<P> build(final ProjectionRegistry factory) throws ProjectionError {
		return projectionBuilder.build(factory);
	}

	public SourcesApi<P> conversion(final Class<? extends Converter<?, ?>> converter, final String...params) {
		sourceHolders.getLast().conversions.add(ConversionMappingBuilder.newInstance().converter(converter).parameters(params));
		return this;
	}

	protected SourceReader[] buildReaders(final TypeConversions typeConversions) throws ProjectionError {
		final ArrayList<SourceReader> sources = new ArrayList<>(sourceHolders.size());
		
		for (SourceHolder holder : sourceHolders) {
			buildReader(typeConversions, holder).ifPresent(sources::add);
		}
		return sources.toArray(new SourceReader[0]);
	}

	protected SourceWriter[] buildWriters(final TypeConversions typeConversions) throws ProjectionError {
		
		final ArrayList<SourceWriter> sources = new ArrayList<>(sourceHolders.size());
		
		for (SourceHolder holder : sourceHolders) {
			buildWriter(typeConversions, holder).ifPresent(sources::add);
		}
		
		return sources.toArray(new SourceWriter[0]);
	}

	Optional<SourceReader> buildReader(final TypeConversions typeConversions, final SourceHolder sourceHolder) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source reader for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		try {
			
			for (ConversionMappingBuilder cb : sourceHolder.conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		// extract getter
		final Getter sourceGetter = ObjectUtils.getGetter(sourceHolder.objectClass, sourceHolder.propertyName);
		
		return sourceHolder.readerBuilder
							.objectClass(sourceHolder.objectClass)
							.getter(sourceGetter)
							.converters(converters)
							.targetType(ObjectType.of(Object.class))
							.build(typeConversions).map(SourceReader.class::cast)
							;	
	}

	Optional<SourceWriter> buildWriter(final TypeConversions typeConversions, final SourceHolder sourceHolder) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build source writer for {}", sourceHolder.propertyName);
		}
		
		final List<ConverterMapping> converters = new ArrayList<>(sourceHolder.conversions.size());
				
		try {
			
			for (ConversionMappingBuilder cb : sourceHolder.conversions) {
				converters.add(cb.build());
			}
			
		} catch (ConverterError e) {
			throw new ProjectionError(e);
		}
		
		// extract setter
		final Setter sourceSetter = ObjectUtils.getSetter(sourceHolder.objectClass, sourceHolder.propertyName);
		
		return sourceHolder.writerBuilder
							.objectClass(sourceHolder.objectClass)
							.setter(sourceSetter)
							.converters(converters)
							.targetType(ObjectType.of(Object.class))
							.build(typeConversions).map(SourceWriter.class::cast)
							;	
	}

	final class SourceHolder {
		
		final List<ConversionMappingBuilder> conversions;
		final SingleSourceReaderBuilder readerBuilder;
		final SingleSourceWriterBuilder writerBuilder;
		final Class<?> objectClass;
		final String propertyName;
		
		public SourceHolder(final Class<?> objectClass, final String propertyName) {
			this.objectClass = objectClass;
			this.propertyName = propertyName;
			this.readerBuilder = SingleSourceReaderBuilder.newInstance();
			this.writerBuilder = SingleSourceWriterBuilder.newInstance();
			this.conversions = new ArrayList<>(5);
		}

		public void optional(final boolean optional) {
			readerBuilder.optional(optional);
			writerBuilder.optional(optional);
		}

		public void mode(final AccessMode mode) {
			readerBuilder.mode(mode);
			writerBuilder.mode(mode);
		}
	}
}
