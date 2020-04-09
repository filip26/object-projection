package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.explicit.BackwardExplicitConversion;
import com.apicatalog.projection.conversion.explicit.ForwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.ImplicitConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.property.source.ArraySource;
import com.apicatalog.projection.property.source.Source;

public class ArraySourceBuilder {

	final Logger logger = LoggerFactory.getLogger(ArraySourceBuilder.class);
	
	Source[] sources;
	
	boolean optional;
	
	Collection<ConverterMapping> converters;
	
	final ImplicitConversions typeConverters;
	
	protected ArraySourceBuilder(ImplicitConversions typeConverters) {
		this.typeConverters = typeConverters;
		this.optional = false;
	}

	public static final ArraySourceBuilder newInstance(ImplicitConversions typeConverters) {
		return new ArraySourceBuilder(typeConverters);
	}
	
	public Optional<ArraySource> build(TypeAdaptersLegacy typeAdapters) {
		
		final ArraySource source = new ArraySource();
		
		if (sources.length == 0) {
			return Optional.empty();
		}

		source.setSources(sources);

		// set conversions to apply
		setConversions(source, sources, converters != null ? converters.toArray(new ConverterMapping[0]) : null);

		// set optional 
		source.setOptional(optional);
						
		// readable/writable
		source.setReadable(Arrays.stream(sources).anyMatch(Source::isReadable));
		source.setWritable(Arrays.stream(sources).anyMatch(Source::isWritable));

		
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
	
	
	final void setConversions(ArraySource source, Source[] sources, ConverterMapping[] converters) {

		// set default source type for an array of sources
		source.setTargetType(ObjectType.of(Object[].class));
		
		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		// get source types
		final Collection<ObjectType> sourceTypes = Arrays.stream(sources).map(Source::getTargetType).collect(Collectors.toList());

		final ArrayList<Conversion<?, ?>> readConversions = new ArrayList<>(converters.length * 2);

		// add conversion if needed
		
		typeConverters.get(sourceTypes, converters[0].getSourceType()).ifPresent(readConversions::add);
		readConversions.add(ForwardExplicitConversion.of(converters[0].getConversion()));

		for (int i = 1; i < converters.length; i++) {

			typeConverters.get(converters[i - 1].getTargetType(), converters[i].getSourceType()).ifPresent(readConversions::add);
			
			readConversions.add(ForwardExplicitConversion.of(converters[i].getConversion()));
		}

		
		final ArrayList<Conversion<?, ?>> writeConversions = new ArrayList<>(converters.length * 2);

		for (int i = converters.length - 1; i > 0 ; i--) {

			writeConversions.add(ForwardExplicitConversion.of(converters[i].getConversion()));
			
			typeConverters.get(converters[i].getSourceType(), converters[i - 1].getTargetType()).ifPresent(writeConversions::add);
		}

		
		writeConversions.add(BackwardExplicitConversion.of(converters[0].getConversion()));

		typeConverters.get(converters[0].getSourceType(), sourceTypes).ifPresent(writeConversions::add);
			
		
		
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setReadConversions(readConversions.toArray(new Conversion[0]));
		source.setWriteConversions(writeConversions.toArray(new Conversion[0]));

	}
}
