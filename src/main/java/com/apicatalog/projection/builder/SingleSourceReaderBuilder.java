package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.explicit.ForwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.source.SourceType;

public class SingleSourceReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;
	
	 Getter sourceGetter;

	protected SingleSourceReaderBuilder() {
		this.mode = AccessMode.READ_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceReaderBuilder newInstance() {
		return new SingleSourceReaderBuilder();
	}

	public Optional<SingleSourceReader> build(TypeConversions typeConverters) {

		
		// no getter ? 
		if (sourceGetter == null) {
			// nothing to do with this
			return Optional.empty();
		}

		final SingleSourceReader source = new SingleSourceReader();

		source.setSourceType(SourceType.of(qualifier, sourceObjectClass));
		
		// set source access
		switch (mode) {
		
		case READ_ONLY:
		case READ_WRITE:
			source.setGetter(sourceGetter);
			break;
			
		default:
			// nothing to do with this
			return Optional.empty(); 
		}			

		// set default source type for an array of sources
		ObjectType targetType = sourceGetter.getType(); 

		source.setTargetType(targetType);

		// set conversions to apply
		buildChain(source, converters, typeConverters);

		// set optional 
		source.setOptional(optional);

		return Optional.of(source);
	}
	
	public SingleSourceReaderBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public SingleSourceReaderBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public SingleSourceReaderBuilder qualifier(String qualifier) {
		this.qualifier = StringUtils.isBlank(qualifier) ? null : qualifier.strip();
		return this;
	}
	
	public SingleSourceReaderBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	public SingleSourceReaderBuilder objectClass(Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
		return this;
	}
	
	public SingleSourceReaderBuilder getter(Getter getter) {
		this.sourceGetter = getter;
		return this;
	}
	
	final void buildChain(final SingleSourceReader source, final Collection<ConverterMapping> converters, final TypeConversions typeConversions) {

		// no conversions to set
		if (converters == null || converters.isEmpty()) {
			return;
		}

		final ArrayList<Conversion> conversions = new ArrayList<>(converters.size() * 2);

		final Iterator<ConverterMapping> it = converters.iterator();
		
		ConverterMapping mapping = it.next();
		
		typeConversions.get(source.getTargetType(), mapping.getSourceType()).ifPresent(conversions::add);
		conversions.add(ForwardExplicitConversion.of(mapping.getConversion()));

		while (it.hasNext()) {
			ConverterMapping next = it.next();

			typeConversions.get(
					mapping.getTargetType(),
					next.getSourceType())
				.ifPresent(conversions::add);
			
			conversions.add(ForwardExplicitConversion.of(next.getConversion()));			

			mapping = next;
		}

		source.setTargetType(mapping.getTargetType());
	
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
