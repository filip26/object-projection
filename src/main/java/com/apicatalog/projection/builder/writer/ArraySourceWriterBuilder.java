package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SourceWriter;

public final class ArraySourceWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriterBuilder.class);
	
	SourceWriter[] sources;
	
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
	
	public Optional<ArraySourceWriter> build(TypeConversions typeConversions) throws ProjectionError {
		
		if (logger.isTraceEnabled()) {
			logger.trace("Build {} sources, {} converters, target={}", 
						sources != null ? sources.length : 0,
						converters != null ? converters.size() : 0, 
						targetType
						);
		}
		
		final ArraySourceWriter source = new ArraySourceWriter();
		
		if (sources == null || sources.length == 0 || targetType == null) {
			return Optional.empty();
		}

		source.setSources(sources);

		try {
			// set conversions to apply
			buildChain(source, typeConversions);

			// set optional 
			source.setOptional(optional);
			
			return Optional.of(source);
			
		} catch (UnknownConversion e) {
			throw new ProjectionError(e);
		}
	}	

	public ArraySourceWriterBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceWriterBuilder sources(SourceWriter[] sources) {
		this.sources = sources;
		return this;
	}

	public ArraySourceWriterBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}

	public ArraySourceWriterBuilder targetType(ObjectType targetTYpe) {
		this.targetType = targetTYpe;
		return this;
	}

	final void buildChain(ArraySourceWriter source, TypeConversions typeConversions) throws UnknownConversion {

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
