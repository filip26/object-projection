package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.conversion.Conversion;
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
	
	protected ArraySourceWriterBuilder() {
		this.optional = false;
	}

	public static final ArraySourceWriterBuilder newInstance() {
		return new ArraySourceWriterBuilder();
	}
	
	public Optional<ArraySourceWriter> build(TypeConversions typeConversions) {
		
		final ArraySourceWriter source = new ArraySourceWriter();
		
		if (sources.length == 0) {
			return Optional.empty();
		}

		source.setSources(sources);

		// set conversions to apply
		buildChain(source, sources, converters != null ? converters.toArray(new ConverterMapping[0]) : null, typeConversions);

		// set optional 
		source.setOptional(optional);
		
		return Optional.of(source);
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
	
	final void buildChain(ArraySourceWriter source, SourceWriter[] sources, ConverterMapping[] converters, TypeConversions typeConversions) {

		// set default source type for an array of sources
		source.setTargetType(ObjectType.of(Object[].class));
		
		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		// get source types
		final Collection<ObjectType> sourceTypes = Arrays.stream(sources).map(SourceWriter::getTargetType).collect(Collectors.toList());

		final ArrayList<Conversion> writeConversions = new ArrayList<>(converters.length * 2);

		for (int i = 1; i < converters.length; i++) {

			writeConversions.add(BackwardExplicitConversion.of(converters[converters.length - i].getConversion()));
			
			typeConversions.get(
					converters[converters.length - i].getSourceType(),
					converters[converters.length - i - 1].getTargetType())
				.ifPresent(writeConversions::add);

		}
		
		writeConversions.add(BackwardExplicitConversion.of(converters[0].getConversion()));
		typeConversions.get(converters[0].getSourceType(), sourceTypes).ifPresent(writeConversions::add);
			
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setConversions(writeConversions.toArray(new Conversion[0]));
	}
}
