package com.apicatalog.projection.builder.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.ConversionNotFound;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.SourceReader;

public final class ArraySourceReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReaderBuilder.class);
	
	Collection<SingleSourceReaderBuilder> sourceReaders;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	ObjectType targetType;
	
	String targetProjectionName;
	
	protected ArraySourceReaderBuilder() {
		this.optional = false;
	}

	public static final ArraySourceReaderBuilder newInstance() {
		return new ArraySourceReaderBuilder();
	}
	
	public Optional<ArraySourceReader> build(TypeConversions typeConversions) throws ProjectionError {
		
		final ArraySourceReader source = new ArraySourceReader();
		
		if (sourceReaders == null || sourceReaders.isEmpty() || targetType == null) {
			return Optional.empty();
		}

		try {
			final Collection<SourceReader> sources = new ArrayList<>(sourceReaders.size());

			ObjectType sourceTargetType  = targetType;
			
			if (targetType.isCollection()) {
				sourceTargetType = ObjectType.of(targetType.getComponentType());
				
			} else if (targetType.isArray()) {
				sourceTargetType = ObjectType.of(targetType.getType().getComponentType());				
			}

			for (final SingleSourceReaderBuilder sourceReaderBuilder : sourceReaders) {
				sourceReaderBuilder.targetType(sourceTargetType).targetProjection(targetProjectionName).build(typeConversions).ifPresent(sources::add);				
			}
			
			// set sources
			source.setSources(sources.toArray(new SourceReader[0]));
			
			// set conversions to apply
			buildChain(source, typeConversions);

			// set optional 
			source.setOptional(optional);
			
			return Optional.of(source);
			
		} catch (ConversionNotFound e) {
			throw new ProjectionError("Can not build array source reader.", e);
		}
	}	

	public ArraySourceReaderBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceReaderBuilder sources(final Collection<SingleSourceReaderBuilder> sourceReaders) {
		this.sourceReaders = sourceReaders;
		return this;
	}

	public ArraySourceReaderBuilder converters(final Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}

	public ArraySourceReaderBuilder targetType(final ObjectType targetType) {
		this.targetType = targetType;
		return this;
	}
	
	public ArraySourceReaderBuilder targetProjection(final String targetProjection) {
		this.targetProjectionName = targetProjection;
		return this;
	}

	final void buildChain(final ArraySourceReader source, final TypeConversions typeConversions) throws ConversionNotFound {

		final ArrayList<Conversion<Object, Object>> conversions = new ArrayList<>((converters != null ? converters.size() : 0) * 2 + 1);

		ObjectType sourceType = ObjectType.of(Object[].class);

		if (converters != null && !converters.isEmpty()) {

			final Iterator<ConverterMapping> it = converters.iterator();
			
			ConverterMapping mapping = it.next();
	
			// implicit conversion
			typeConversions.get(sourceType, mapping.getSourceType()).ifPresent(conversions::add);
			
			sourceType = mapping.getTargetType();
			
			// explicit conversion
			conversions.add(mapping.getConverter()::forward);
	
			while (it.hasNext()) {
	
				mapping = it.next();
				
				// implicit conversion
				typeConversions.get(
									sourceType,
									mapping.getSourceType()
									)
								.ifPresent(conversions::add);
	
				// explicit conversion
				conversions.add(mapping.getConverter()::forward);
	
				sourceType = mapping.getTargetType();
			}
		}
		
		targetType = SingleSourceReaderBuilder.getSourceTargetType(sourceType, targetType, targetProjectionName);
		
		// implicit conversion
		typeConversions.get(
				sourceType,
				targetType
				)
			.ifPresent(conversions::add);

	
		source.setConversions(conversions);
		
		// set default source type
		source.setTargetType(targetType);
	}
}
