package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
	
	ObjectType targetType;
	
	protected ArraySourceReaderBuilder() {
		this.optional = false;
	}

	public static final ArraySourceReaderBuilder newInstance() {
		return new ArraySourceReaderBuilder();
	}
	
	public Optional<ArraySourceReader> build(TypeConversions typeConversions) {
		
		final ArraySourceReader source = new ArraySourceReader();
		
		if (sources == null || sources.length == 0 || targetType == null) {
			return Optional.empty();
		}

		source.setSources(sources);

		// set conversions to apply
		buildChain(source, sources, converters, typeConversions);

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

	public ArraySourceReaderBuilder targetType(ObjectType targetType) {
		this.targetType = targetType;
		return this;
	}

	final void buildChain(ArraySourceReader source, SourceReader[] sources, Collection<ConverterMapping> converters, TypeConversions typeConversions) {

		// set default source type for an array of sources
		source.setTargetType(ObjectType.of(Object[].class));
		
		// no conversions to set
		if (converters == null || converters.isEmpty()) {
			return;
		}

		// get source types
		final Collection<ObjectType> sourceTypes = Arrays.stream(sources).map(SourceReader::getTargetType).collect(Collectors.toList());

		final ArrayList<Conversion> readConversions = new ArrayList<>(converters.size() * 2);
		
		final Iterator<ConverterMapping> it = converters.iterator();
		
		ConverterMapping mapping = it.next();
		
		typeConversions.get(sourceTypes, mapping.getSourceType()).ifPresent(readConversions::add);
		readConversions.add(ForwardExplicitConversion.of(mapping.getConversion()));

		while (it.hasNext()) {

			ConverterMapping next = it.next();
			
			typeConversions.get(
								mapping.getTargetType(),
								next.getSourceType()
								)
							.ifPresent(readConversions::add);
			
			readConversions.add(ForwardExplicitConversion.of(next.getConversion()));
			
			mapping = next;
		}
		
		source.setTargetType(mapping.getTargetType());
	
		source.setConversions(readConversions.toArray(new Conversion[0]));
	}
}
