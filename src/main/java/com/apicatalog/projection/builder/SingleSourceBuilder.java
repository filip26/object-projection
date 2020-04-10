package com.apicatalog.projection.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.type.TypeAdaptersLegacy;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.explicit.BackwardExplicitConversion;
import com.apicatalog.projection.conversion.explicit.ForwardExplicitConversion;
import com.apicatalog.projection.conversion.implicit.TypeConversions;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SingleSource;
import com.apicatalog.projection.property.source.Source;
import com.apicatalog.projection.source.SourceType;

public class SingleSourceBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;

	Getter sourceGetter;
	Setter sourceSetter;
	
	final TypeConversions typeConverters;
	
	protected SingleSourceBuilder(TypeConversions typeConverters) {
		this.typeConverters = typeConverters;
		this.mode = AccessMode.READ_WRITE;
		this.optional = false;
	}
	
	public static SingleSourceBuilder newInstance(TypeConversions typeConverters) {
		return new SingleSourceBuilder(typeConverters);
	}
	
	public Optional<SingleSource> build(TypeAdaptersLegacy typeAdapters) {

		// no setter nor getter? 
		if (sourceGetter == null && sourceSetter == null) {
			// nothing to do with this
			return Optional.empty();
		}

		final SingleSource source = new SingleSource();

		source.setSourceType(SourceType.of(StringUtils.isNotBlank(qualifier) ? qualifier : null, sourceObjectClass));
		
		// set source access
		switch (mode) {
		case READ_ONLY:
			source.setGetter(sourceGetter);
			break;
			
		case WRITE_ONLY:
			source.setSetter(sourceSetter);
			break;
		
		case READ_WRITE:
			source.setGetter(sourceGetter);
			source.setSetter(sourceSetter);
			break;
		}			

		// set default source type for an array of sources
		ObjectType targetType = sourceGetter != null ? sourceGetter.getType() : sourceSetter.getType(); 

		source.setTargetType(targetType);

		// set conversions to apply
		makeChain(source, converters != null ? converters.toArray(new ConverterMapping[0]) : null);

		
		//TODO add implicit conversions into the chain
		
		// set conversions to apply
//		source.setConversions(converters != null ? converters.toArray(new ConverterMapping[0]) : null);

		// set optional 
		source.setOptional(optional);
				

//		// extract actual target object class 
//		if (source.getConversions() != null) {
//			
//			Stream.of(source.getConversions())
//					.reduce((first, second) -> second)
//					.ifPresent(m -> source.setTargetType(m.getSourceType()));
//
//		}
		return Optional.of(source);
	}
	
	public SingleSourceBuilder mode(AccessMode mode) {
		this.mode = mode;
		return this;
	}
	
	public SingleSourceBuilder optional(boolean optional) {
		this.optional = optional;
		return this;
	}
	
	public SingleSourceBuilder qualifier(String qualifier) {
		this.qualifier = StringUtils.isBlank(qualifier) ? null : qualifier.strip();
		return this;
	}
	
	public SingleSourceBuilder converters(Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	public SingleSourceBuilder objectClass(Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
		return this;
	}
	
	public SingleSourceBuilder setter(Setter setter) {
		this.sourceSetter = setter;
		return this;
	}
	
	public SingleSourceBuilder getter(Getter getter) {
		this.sourceGetter = getter;
		return this;
	}
	
	final void makeChain(SingleSource source, ConverterMapping[] converters) {

		// no conversions to set
		if (converters == null || converters.length == 0) {
			return;
		}

		final ArrayList<Conversion> readConversions = new ArrayList<>(converters.length * 2);
		final ArrayList<Conversion> writeConversions = new ArrayList<>(converters.length * 2);

		typeConverters.get(source.getTargetType(), converters[0].getSourceType()).ifPresent(readConversions::add);
		readConversions.add(ForwardExplicitConversion.of(converters[0].getConversion()));

		for (int i = 1; i < converters.length; i++) {

			// read chain
			typeConverters.get(
					converters[i - 1].getTargetType(),
					converters[i].getSourceType())
				.ifPresent(readConversions::add);
			
			readConversions.add(ForwardExplicitConversion.of(converters[i].getConversion()));
			
			//write chain
			writeConversions.add(BackwardExplicitConversion.of(converters[converters.length - i].getConversion()));
			
			typeConverters.get(
					converters[converters.length - i].getSourceType(),
					converters[converters.length - i - 1].getTargetType())
				.ifPresent(writeConversions::add);

		}
		
		writeConversions.add(BackwardExplicitConversion.of(converters[0].getConversion()));
		typeConverters.get(converters[0].getSourceType(), source.getTargetType()).ifPresent(writeConversions::add);
			
		source.setTargetType(converters[converters.length - 1].getTargetType());
	
		source.setReadConversions(readConversions.toArray(new Conversion[0]));
		source.setWriteConversions(writeConversions.toArray(new Conversion[0]));
	}
}
