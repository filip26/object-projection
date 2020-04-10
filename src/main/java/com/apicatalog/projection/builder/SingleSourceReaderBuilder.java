package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Collection;
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

		source.setSourceType(SourceType.of(StringUtils.isNotBlank(qualifier) ? qualifier : null, sourceObjectClass));
		
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
		buildChain(source, converters != null ? converters.toArray(new ConverterMapping[0]) : null, typeConverters);

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
	
	final void buildChain(final SingleSourceReader source, final ConverterMapping[] converters, final TypeConversions typeConversions) {

		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		final ArrayList<Conversion> conversions = new ArrayList<>(converters.length * 2);

		typeConversions.get(source.getTargetType(), converters[0].getSourceType()).ifPresent(conversions::add);
		conversions.add(ForwardExplicitConversion.of(converters[0].getConversion()));

		for (int i = 1; i < converters.length; i++) {
			typeConversions.get(
					converters[i - 1].getTargetType(),
					converters[i].getSourceType())
				.ifPresent(conversions::add);
			
			conversions.add(ForwardExplicitConversion.of(converters[i].getConversion()));			
		}
					
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
