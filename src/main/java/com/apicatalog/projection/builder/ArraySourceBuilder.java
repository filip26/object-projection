package com.apicatalog.projection.builder;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.reducer.ReducerMapping;
import com.apicatalog.projection.source.ArraySource;
import com.apicatalog.projection.source.SingleSource;

public class ArraySourceBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceBuilder.class);
	
	SingleSource[] sources;
	
	boolean optional;
	
	ReducerMapping reduction;
	
	ConverterMapping[] converters;
	
	protected ArraySourceBuilder() {
		this.optional = false;
	}

	public static final ArraySourceBuilder newInstance() {
		return new ArraySourceBuilder();
	}
	
	public ArraySource build(TypeAdapters typeAdapters) {
		
		final ArraySource source = new ArraySource(typeAdapters);
		
		if (sources.length == 0) {
			return null;
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
					.ifPresent(m -> source.setTargetType(ObjectType.of(m.getSourceClass(), m.getSourceComponentClass())));
		}
		
		return source;
	}	
	
	public ArraySourceBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public ArraySourceBuilder sources(SingleSource[] sources) {
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
