package com.apicatalog.projection.builder;

import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.Source;
import com.apicatalog.projection.reducer.ReducerMapping;

public class ArraySourceBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceBuilder.class);
	
	Source[] sources;
	
	boolean optional;
	
	ReducerMapping reduction;
	
	ConverterMapping[] converters;
	
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

		// set reduction
		source.setReduction(reduction);

		// set conversions to apply
		source.setConversions(converters);

		// set optional 
		source.setOptional(optional);
				
		// set target type
		source.setTargetType(reduction.getTargetType());
				
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

	public ArraySourceBuilder reducer(ReducerMapping reduction) {
		this.reduction = reduction;
		return this;
	}

	public ArraySourceBuilder converters(ConverterMapping[] converters) {
		this.converters = converters;
		return this;
	}
}
