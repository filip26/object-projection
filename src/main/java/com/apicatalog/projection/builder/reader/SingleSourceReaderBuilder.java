package com.apicatalog.projection.builder.reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.source.SingleSourceReader;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceReaderBuilder {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReaderBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;
	
	Getter sourceGetter;
	
	ObjectType targetType;
	boolean targetReference;

	protected SingleSourceReaderBuilder() {
		this.mode = AccessMode.READ_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceReaderBuilder newInstance() {
		return new SingleSourceReaderBuilder();
	}

	public Optional<SingleSourceReader> build(TypeConversions typeConverters) throws ProjectionError {

		// no getter ? 
		if (sourceGetter == null || targetType == null) {
			// nothing to do with this
			return Optional.empty();
		}

		final SingleSourceReader source = new SingleSourceReader();

		source.setSourceObjectType(SourceType.of(qualifier, sourceObjectClass));
		
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

		try {
			// set conversions to apply
			buildChain(source, typeConverters);
	
			// set optional 
			source.setOptional(optional);
	
			return Optional.of(source);
			
		} catch (UnknownConversion e) {
			throw new ProjectionError(e);
		}
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
	
	public SingleSourceReaderBuilder converters(final Collection<ConverterMapping> converters) {
		this.converters = converters;
		return this;
	}
	
	public SingleSourceReaderBuilder objectClass(final Class<?> sourceObjectClass) {
		this.sourceObjectClass = sourceObjectClass;
		return this;
	}
	
	public SingleSourceReaderBuilder getter(final Getter getter) {
		this.sourceGetter = getter;
		return this;
	}
	
	public SingleSourceReaderBuilder targetType(final ObjectType targetType, final boolean targetReference) {
		this.targetType= targetType;
		this.targetReference = targetReference;
		return this;
	}	
	
	final void buildChain(final SingleSourceReader source, final TypeConversions typeConversions) throws UnknownConversion {

		final ArrayList<Conversion<Object, Object>> conversions = new ArrayList<>((converters != null ? converters.size() : 0) * 2 + 1);
		
		ObjectType sourceType = sourceGetter.getType();

		if (converters != null && !converters.isEmpty()) {
			
			final Iterator<ConverterMapping> it = converters.iterator();
			
			ConverterMapping mapping = it.next();
	
			// implicit conversion
			typeConversions.get(sourceType, mapping.getSourceType()).ifPresent(conversions::add);
			
			sourceType = mapping.getTargetType();
			
			// explicit conversion
			conversions.add(mapping.getConverter()::forward);
	
			while (it.hasNext()) {
				
				mapping = it.next();
	
				// implicit conversion
				typeConversions.get(
						sourceType,
						mapping.getSourceType())
					.ifPresent(conversions::add);
				
				// explicit conversion
				conversions.add(mapping.getConverter()::forward);			
	
				sourceType = mapping.getTargetType();
			}
		}
		
		targetType = getSourceTargetType(sourceType, targetType, targetReference);
		
		// implicit conversion
		typeConversions.get(
							sourceType,
							targetType
							)
			.ifPresent(conversions::add);

		source.setConversions(conversions);
		
		// set default source type for an array of sources
		source.setType(targetType);
	}
	
	public static final ObjectType getSourceTargetType(final ObjectType sourceType, final ObjectType targetType, final boolean targetReference) {
		
		if (targetReference) {
			if (targetType.isCollection()) {
				
				if (sourceType.isCollection()) {
					return ObjectType.of(targetType.getType(), sourceType.getComponentType());
					
				} else if (sourceType.isArray()) {
					return ObjectType.of(targetType.getType(), sourceType.getType().getComponentType());
					
				} else {
					return ObjectType.of(targetType.getType(), sourceType.getType());
				}
				
			} else if (targetType.isArray()) {

				//TODO

			} else {

				if (sourceType.isCollection()) {
					return ObjectType.of(sourceType.getComponentType());
					
				} else if (sourceType.isArray()) {
					return ObjectType.of(sourceType.getType().getComponentType());
					
				} else {
					return sourceType;
				}
				
			}
		}
		return targetType;
	}
}
