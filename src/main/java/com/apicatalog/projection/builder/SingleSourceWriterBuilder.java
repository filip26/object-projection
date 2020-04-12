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
	
	ObjectType targetType;

	protected SingleSourceWriterBuilder() {
		this.mode = AccessMode.WRITE_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceWriterBuilder newInstance() {
		return new SingleSourceWriterBuilder();
	}
	
	public Optional<SingleSourceWriter> build(TypeConversions typeConverters) {

		// no setter ? 
		if (sourceSetter == null || targetType == null) {
			// nothing to do with this
			return Optional.empty();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Building single source writer from {} to {}.{} : {}",
							targetType,
							sourceObjectClass.getSimpleName(),
							sourceSetter.getName(),
							sourceSetter.getType()
							);
		}
		
		final SingleSourceWriter source = new SingleSourceWriter();

		source.setSourceType(SourceType.of(qualifier, sourceObjectClass));
		
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
		source.setTargetType(targetType);

		// set conversions to apply
		buildChain(source, converters, typeConverters, targetType);
		
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
	
	public SingleSourceWriterBuilder targetType(ObjectType targetType) {
		this.targetType= targetType;
		return this;
	}	
	
	final void buildChain(SingleSourceWriter source, final Collection<ConverterMapping> converters, final TypeConversions typeConversions, ObjectType targetType) {

		final ArrayList<Conversion> conversions = new ArrayList<>((converters == null ? 0 : converters.size()) * 2 + 1);

		if (converters == null || converters.isEmpty()) {
			typeConversions.get(
					targetType,
					sourceSetter.getType())
				.ifPresent(conversions::add);
			source.setConversions(conversions.toArray(new Conversion[0]));
			
			if (logger.isTraceEnabled()) {
				logger.trace("1 conversion attached");
			}

			return;
		}
		
		final ConverterMapping[] mapping = converters.toArray(new ConverterMapping[0]); 

		typeConversions.get(
				targetType,
				mapping[mapping.length - 1].getTargetType())
			.ifPresent(conversions::add);
		
		for (int i = 1; i < mapping.length; i++) {
			
			conversions.add(BackwardExplicitConversion.of(mapping[mapping.length - i].getConversion()));
			
			typeConversions.get(
					mapping[mapping.length - i].getSourceType(),
					mapping[mapping.length - i - 1].getTargetType())
				.ifPresent(conversions::add);
		}
		
		conversions.add(BackwardExplicitConversion.of(mapping[0].getConversion()));
		typeConversions.get(mapping[0].getSourceType(), sourceSetter.getType()).ifPresent(conversions::add);
			
		if (logger.isTraceEnabled()) {
			logger.trace("{} conversions attached", conversions.size());
		}
		
		source.setConversions(conversions.toArray(new Conversion[0]));
	}
}
