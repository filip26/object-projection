package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.Builder;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.ConversionNotFound;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SourceWriter;

public final class ArraySourceWriterBuilder implements Builder<SourceWriter> {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriterBuilder.class);
	
	Collection<SingleSourceWriterBuilder> sourceWriters;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	ObjectType targetType;
	
	boolean targetReference;
	
	protected ArraySourceWriterBuilder() {
		this.optional = false;
	}

	public static final ArraySourceWriterBuilder newInstance() {
		return new ArraySourceWriterBuilder();
	}
	
	@Override
	public Optional<SourceWriter> build(TypeConversions typeConversions) throws ProjectionError {
		
		if (logger.isTraceEnabled()) {
			logger.trace("Build {} sources, {} converters, target={}", 
						sourceWriters != null ? sourceWriters.size() : 0,
						converters != null ? converters.size() : 0, 
						targetType
						);
		}
		
		final ArraySourceWriter source = new ArraySourceWriter();
		
		if (sourceWriters == null || sourceWriters.isEmpty() || targetType == null) {
			return Optional.empty();
		}
	
		try {

			// set conversions to apply
			buildChain(source, typeConversions);
			
			
			final Collection<SourceWriter> sources = new ArrayList<>(sourceWriters.size());

			ObjectType sourceTargetType = targetType;
			
			if (targetType.isCollection()) {
				sourceTargetType = ObjectType.of(targetType.getComponentType());
				
			} else if (targetType.isArray()) {
				sourceTargetType = ObjectType.of(targetType.getType().getComponentType());				
			}

			for (final SingleSourceWriterBuilder sourceWriterBuilder : sourceWriters) {
				sourceWriterBuilder.targetType(sourceTargetType, targetReference).build(typeConversions).ifPresent(sources::add);				
			}
			
			// set sources
			source.setSources(sources.toArray(new SourceWriter[0]));
			
			// set optional 
			source.setOptional(optional);
			
			return Optional.of(source);
			
		} catch (ConversionNotFound e) {
			throw new ProjectionError("Can not create sources.", e);
		}
	}	

	public ArraySourceWriterBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceWriterBuilder sources(final Collection<SingleSourceWriterBuilder> sourceWriters) {
		this.sourceWriters = sourceWriters;
		return this;
	}

	public ArraySourceWriterBuilder converters(final Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}

	public ArraySourceWriterBuilder targetType(ObjectType targetType, boolean targetReference) {
		this.targetType = targetType;
		this.targetReference = targetReference;
		return this;
	}

	final void buildChain(ArraySourceWriter source, TypeConversions typeConversions) throws ConversionNotFound {

		final ArrayList<Conversion<Object, Object>> conversions = new ArrayList<>((converters == null ? 0 : converters.size()) * 2 + 1);
		
		if (converters != null && !converters.isEmpty()) {

			final ConverterMapping[] mapping = converters.toArray(new ConverterMapping[0]);

			targetType = SingleSourceWriterBuilder.getSourceTargetType(targetType, targetReference, mapping[mapping.length - 1].getTargetType());
	
			typeConversions.get(
					targetType,
					mapping[mapping.length - 1].getTargetType())
				.ifPresent(conversions::add);
	
			
			for (int i = 1; i < mapping.length; i++) {
	
				// explicit conversion
				final Converter<Object, Object> converter = mapping[mapping.length - i].getConverter();
				
				conversions.add(converter::backward);
	
				// implicit conversion
				typeConversions.get(
						mapping[mapping.length - i].getSourceType(),
						mapping[mapping.length - i - 1].getTargetType())
					.ifPresent(conversions::add);
	
			}
			
			// explicit conversion
			final Converter<Object, Object> converter = mapping[0].getConverter();
			
			conversions.add(converter::backward);
			
			targetType = mapping[0].getSourceType();
			
		} else {
			targetType = SingleSourceWriterBuilder.getSourceTargetType(targetType, targetReference, ObjectType.of(Object[].class));						
		}

		// implicit conversion
		typeConversions.get(targetType, ObjectType.of(Object[].class)).ifPresent(conversions::add);

		source.setConversions(conversions);
		
		// set default target type
		source.setTargetType(targetType);
	}
}
