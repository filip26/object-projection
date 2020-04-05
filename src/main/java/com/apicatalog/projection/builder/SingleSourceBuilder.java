package com.apicatalog.projection.builder;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.objects.ObjectType;
import com.apicatalog.projection.objects.getter.Getter;
import com.apicatalog.projection.objects.setter.Setter;
import com.apicatalog.projection.property.source.SingleSource;

public class SingleSourceBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	ConverterMapping[] converters;
	
	Class<?> sourceObjectClass;

	Getter sourceGetter;
	Setter sourceSetter;
	
	protected SingleSourceBuilder() {		
		this.mode = AccessMode.READ_WRITE;
		this.optional = false;
	}
	
	public static SingleSourceBuilder newInstance() {
		return new SingleSourceBuilder();
	}
	
	public SingleSource build(TypeAdapters typeAdapters) {
		
		final SingleSource source = new SingleSource(typeAdapters);

		source.setObjectClass(sourceObjectClass);

		// no setter nor getter? 
		if (sourceGetter == null && sourceSetter == null) {
			// nothing to do with this
			return null;
		}
		
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

		// set conversions to apply
		source.setConversions(converters);

		// set optional 
		source.setOptional(optional);
		
		// set qualifier
		source.setQualifier(qualifier);
		
		// set target class
		ObjectType targetType = sourceGetter != null ? sourceGetter.getType() : sourceSetter.getType(); 

		source.setTargetType(targetType);

		// extract actual target object class 
		if (source.getConversions() != null) {
			
			Stream.of(source.getConversions())
					.reduce((first, second) -> second)
					.ifPresent(m -> source.setTargetType(m.getSourceType()));

		}
		return source;
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
	
	public SingleSourceBuilder converters(ConverterMapping[] converters) {
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
}
