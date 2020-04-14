package com.apicatalog.projection.builder.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
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
	
	public Optional<ArraySourceReader> build(TypeConversions typeConversions) throws ProjectionError {
		
		final ArraySourceReader source = new ArraySourceReader();
		
		if (sources == null || sources.length == 0 || targetType == null) {
			return Optional.empty();
		}

		// set sources
		source.setSources(sources);
		
		// set default source type
		source.setTargetType(targetType);

		try {
			// set conversions to apply
			buildChain(source, converters, typeConversions);
	
			// set optional 
			source.setOptional(optional);
			
			return Optional.of(source);
			
		} catch (UnknownConversion e) {
			throw new ProjectionError(e);
		}
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

	final void buildChain(ArraySourceReader source, Collection<ConverterMapping> converters, TypeConversions typeConversions) throws UnknownConversion {

		final ArrayList<Conversion> conversions = new ArrayList<>((converters != null ? converters.size() : 0) * 2 + 1);
		
		if (converters == null || converters.isEmpty()) {
			typeConversions.get(ObjectType.of(Object[].class), targetType).ifPresent(conversions::add);
			source.setConversions(conversions.toArray(new Conversion[0]));
			return;
		}
							
		final Iterator<ConverterMapping> it = converters.iterator();
		
		ConverterMapping mapping = it.next();
		
		typeConversions.get(ObjectType.of(Object[].class), mapping.getSourceType()).ifPresent(conversions::add);
		conversions.add(ForwardExplicitConversion.of(mapping.getConversion()));

		while (it.hasNext()) {

			ConverterMapping next = it.next();
			
			typeConversions.get(
								mapping.getTargetType(),
								next.getSourceType()
								)
							.ifPresent(conversions::add);
			
			conversions.add(ForwardExplicitConversion.of(next.getConversion()));
			
			mapping = next;
		}
		
		typeConversions.get(
				mapping.getTargetType(),
				targetType)
			.ifPresent(conversions::add);

	
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
