package com.apicatalog.projection.builder.writer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.conversion.explicit.BackwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySourceWriter;
import com.apicatalog.projection.property.source.SourceWriter;

public class ArraySourceWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceWriterBuilder.class);
	
	SourceWriter[] sources;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	ObjectType targetType;
	
	protected ArraySourceWriterBuilder() {
		this.optional = false;
	}

	public static final ArraySourceWriterBuilder newInstance() {
		return new ArraySourceWriterBuilder();
	}
	
	public Optional<ArraySourceWriter> build(TypeConversions typeConversions) throws ProjectionError {
		
		final ArraySourceWriter source = new ArraySourceWriter();
		
		if (sources == null || sources.length == 0 || targetType == null) {
			return Optional.empty();
		}

		source.setSources(sources);

		try {
			// set conversions to apply
			buildChain(source, converters, typeConversions);
	
			// set default target type
			source.setTargetType(targetType);
			
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

	final void buildChain(ArraySourceWriter source, Collection<ConverterMapping> converters, TypeConversions typeConversions) throws UnknownConversion {

		final ArrayList<Conversion> conversions = new ArrayList<>((converters == null ? 0 : converters.size()) * 2 + 1);
		
		// no conversions to set
		if (converters == null || converters.isEmpty()) {

			typeConversions.get(
					targetType,
					ObjectType.of(Object[].class))
				.ifPresent(conversions::add);
			source.setConversions(conversions.toArray(new Conversion[0]));

			return;
		}

		final ConverterMapping[] mapping = converters.toArray(new ConverterMapping[0]);

		typeConversions.get(
				targetType,
				mapping[mapping.length - 1].getTargetType())
			.ifPresent(conversions::add);

		
		for (int i = 1; i < mapping.length; i++) {

			conversions.add(BackwardExplicitConversion.of(mapping[mapping.length - i].getConversion()));
			
			typeConversions.get(
					mapping[mapping.length - i].getSourceType(),
					mapping[mapping.length - i - 1].getTargetType())
				.ifPresent(conversions::add);

		}
		
		conversions.add(BackwardExplicitConversion.of(mapping[0].getConversion()));
		typeConversions.get(mapping[0].getSourceType(), ObjectType.of(Object[].class)).ifPresent(conversions::add);
			

	
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
