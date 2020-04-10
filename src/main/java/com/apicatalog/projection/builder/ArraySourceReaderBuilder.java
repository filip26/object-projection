package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.explicit.ForwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySourceReader;
import com.apicatalog.projection.property.source.SourceReader;

public class ArraySourceReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceReaderBuilder.class);
	
	SourceReader[] sources;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	protected ArraySourceReaderBuilder() {
		this.optional = false;
	}

	public static final ArraySourceReaderBuilder newInstance() {
		return new ArraySourceReaderBuilder();
	}
	
	public Optional<ArraySourceReader> build(TypeConversions typeConversions) {
		
		final ArraySourceReader source = new ArraySourceReader();
		
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

	public ArraySourceReaderBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceReaderBuilder sources(SourceReader[] sources) {
		this.sources = sources;
		return this;
	}

	public ArraySourceReaderBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	final void buildChain(ArraySourceReader source, SourceReader[] sources, ConverterMapping[] converters, TypeConversions typeConversions) {

		// set default source type for an array of sources
		source.setTargetType(ObjectType.of(Object[].class));
		
		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		// get source types
		final Collection<ObjectType> sourceTypes = Arrays.stream(sources).map(SourceReader::getTargetType).collect(Collectors.toList());

		final ArrayList<Conversion> readConversions = new ArrayList<>(converters.length * 2);

		typeConversions.get(sourceTypes, converters[0].getSourceType()).ifPresent(readConversions::add);
		readConversions.add(ForwardExplicitConversion.of(converters[0].getConversion()));

		for (int i = 1; i < converters.length; i++) {

			// read chain
			typeConversions.get(
					converters[i - 1].getTargetType(),
					converters[i].getSourceType())
				.ifPresent(readConversions::add);
			
			readConversions.add(ForwardExplicitConversion.of(converters[i].getConversion()));
		}
		
			
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setConversions(readConversions.toArray(new Conversion[0]));
	}
}
