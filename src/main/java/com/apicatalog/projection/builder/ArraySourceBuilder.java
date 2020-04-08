package com.apicatalog.projection.builder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.Source;
import com.apicatalog.projection.type.adapter.TypeAdapters;

public class ArraySourceBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceBuilder.class);
	
	Source[] sources;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	protected ArraySourceBuilder() {
		this.optional = false;
	}

	public static final ArraySourceBuilder newInstance() {
		return new ArraySourceBuilder();
	}
	
	public Optional<ArraySource> build(TypeAdapters typeAdapters) {
		
		final ArraySource source = new ArraySource(typeAdapters);
		
		if (sources.length == 0) {
			return Optional.empty();
		}

		source.setSources(sources);

		//TODO add sources -> first converter conversion
		//TODO add implicit conversions into the chain
		
		// set conversions to apply
		source.setConversions(converters != null ? converters.toArray(new ConverterMapping[0]) : null);

		// set optional 
		source.setOptional(optional);
				
		// set target type
		source.setTargetType(ObjectType.of(Object[].class));
		
		// readable/writable
		source.setReadable(Arrays.stream(sources).anyMatch(Source::isReadable));
		source.setWritable(Arrays.stream(sources).anyMatch(Source::isWritable));
				
		// extract actual target object class 
		if (source.getConversions() != null) {
			Stream.of(source.getConversions())
					.reduce((first, second) -> second)
					.ifPresent(m -> source.setTargetType(m.getSourceType()));
		}
		
		return Optional.of(source);
	}	
	
	public ArraySourceBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceBuilder sources(Source[] sources) {
		this.sources = sources;
		return this;
	}

	public ArraySourceBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
}
