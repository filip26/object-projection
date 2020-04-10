package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.explicit.BackwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SingleSourceWriter;
import com.apicatalog.projection.source.SourceType;

public class SingleSourceWriterBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriterBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;
	
	Setter sourceSetter;

	protected SingleSourceWriterBuilder() {
		this.mode = AccessMode.WRITE_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceWriterBuilder newInstance() {
		return new SingleSourceWriterBuilder();
	}
	
	public Optional<SingleSourceWriter> build(TypeConversions typeConverters) {

		// no setter ? 
		if (sourceSetter == null) {
			// nothing to do with this
			return Optional.empty();
		}

		final SingleSourceWriter source = new SingleSourceWriter();

		source.setSourceType(SourceType.of(StringUtils.isNotBlank(qualifier) ? qualifier : null, sourceObjectClass));
		
		// set source access
		switch (mode) {

		case READ_WRITE:
		case WRITE_ONLY:
			source.setSetter(sourceSetter);
			break;

		default:
			// nothing to do with this
			return Optional.empty(); 
		}			

		// set default source type for an array of sources
		ObjectType targetType = sourceSetter.getType(); 

		source.setTargetType(targetType);

		// set conversions to apply
		buildChain(source, converters != null ? converters.toArray(new ConverterMapping[0]) : null, typeConverters);

		// set optional 
		source.setOptional(optional);

		return Optional.of(source);
	}
	
	public SingleSourceWriterBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public SingleSourceWriterBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public SingleSourceWriterBuilder qualifier(String qualifier) {
		this.qualifier = StringUtils.isBlank(qualifier) ? null : qualifier.strip();
		return this;
	}
	
	public SingleSourceWriterBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	public SingleSourceWriterBuilder objectClass(Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
		return this;
	}

	public SingleSourceWriterBuilder setter(Setter setter) {
		this.sourceSetter = setter;
		return this;
	}
	
	final void buildChain(SingleSourceWriter source, final ConverterMapping[] converters, final TypeConversions typeConversions) {

		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		final ArrayList<Conversion> conversions = new ArrayList<>(converters.length * 2);

		for (int i = 1; i < converters.length; i++) {
			
			conversions.add(BackwardExplicitConversion.of(converters[converters.length - i].getConversion()));
			
			typeConversions.get(
					converters[converters.length - i].getSourceType(),
					converters[converters.length - i - 1].getTargetType())
				.ifPresent(conversions::add);

		}
		
		conversions.add(BackwardExplicitConversion.of(converters[0].getConversion()));
		typeConversions.get(converters[0].getSourceType(), source.getTargetType()).ifPresent(conversions::add);
			
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
