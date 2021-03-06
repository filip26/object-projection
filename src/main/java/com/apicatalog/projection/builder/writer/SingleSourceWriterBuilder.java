package com.apicatalog.projection.builder.writer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.AccessMode;
import com.apicatalog.projection.api.ProjectionError;
import com.apicatalog.projection.builder.Builder;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.ConversionNotFound;
import com.apicatalog.projection.conversion.TypeConversions;
import com.apicatalog.projection.converter.Converter;
import com.apicatalog.projection.converter.ConverterMapping;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.source.SingleSourceWriter;
import com.apicatalog.projection.property.source.SourceWriter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceWriterBuilder implements Builder<SourceWriter> {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriterBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	boolean optional;
	
	String qualifier;
	
	AccessMode mode;
	
	Collection<ConverterMapping> converters;
	
	Class<?> sourceObjectClass;
	
	Setter sourceSetter;
	
	ObjectType targetType;
	
	String targetProjectionName;

	protected SingleSourceWriterBuilder() {
		this.mode = AccessMode.WRITE_ONLY;
		this.optional = false;
	}
	
	public static SingleSourceWriterBuilder newInstance() {
		return new SingleSourceWriterBuilder();
	}
	
	@Override
	public Optional<SourceWriter> build(TypeConversions typeConverters) throws ProjectionError {

		if (logger.isDebugEnabled()) {
			logger.debug("Build single source writer from {} to {}.{} : {}",
							targetType,
							sourceObjectClass != null ? sourceObjectClass.getSimpleName() : "n/a",
							sourceSetter != null ? sourceSetter.getName() : "n/a",
							sourceSetter != null ? sourceSetter.getType() : "n/a"
							);
		}

		// no setter ? 
		if (sourceSetter == null || targetType == null) {
			// nothing to do with this
			return Optional.empty();
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

		try {
			// set conversions to apply
			buildChain(source, typeConverters);
			
			// set optional 
			source.setOptional(optional);
	
			return Optional.of(source);
			
		} catch (ConversionNotFound e) {
			throw new ProjectionError("Can not build source writer.", e);
		}
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
	
	public SingleSourceWriterBuilder targetProjection(final String projectionName) {
		this.targetProjectionName = projectionName;
		return this;
	}
	
	final void buildChain(final SingleSourceWriter source, final TypeConversions typeConversions) throws ConversionNotFound {

		final ArrayList<Conversion<Object, Object>> conversions = new ArrayList<>((converters == null ? 0 : converters.size()) * 2 + 1);
		
		if (converters != null && !converters.isEmpty()) {
		
			final ConverterMapping[] mapping = converters.toArray(new ConverterMapping[0]); 
	
			targetType = getSourceTargetType(targetType, targetProjectionName, mapping[mapping.length - 1].getTargetType());
			
			typeConversions.get(
					targetType,
					mapping[mapping.length - 1].getTargetType())
				.ifPresent(conversions::add);
			
			for (int i = 1; i < mapping.length; i++) {
				
				// explicit conversion
				final Converter<Object, Object> converter = mapping[mapping.length - i].getConverter();
				
				conversions.add(converter::backward);
	
				// implicit conversion
				typeConversions.get(
						mapping[mapping.length - i].getSourceType(),
						mapping[mapping.length - i - 1].getTargetType())
					.ifPresent(conversions::add);
			}
			
			// explicit conversion
			final Converter<Object, Object> converter = mapping[0].getConverter();
			
			conversions.add(converter::backward);

			targetType = mapping[0].getSourceType();
			
		} else {
			
			targetType = getSourceTargetType(targetType, targetProjectionName, sourceSetter.getType());			
		}
		
		// implicit conversion
		typeConversions.get(targetType, sourceSetter.getType()).ifPresent(conversions::add);
			
		if (logger.isTraceEnabled()) {
			logger.trace("{} conversions attached", conversions.size());
		}
		
		source.setConversions(conversions);
		
		// set default source type for an array of sources
		source.setTargetType(targetType);

	}
	
	public static final ObjectType getSourceTargetType(final ObjectType sourceType, final String sourceProjectionName, final ObjectType targetType) {
		
		if (StringUtils.isNotBlank(sourceProjectionName)) {
			
			if (sourceType.isCollection()) {
				
				if (targetType.isCollection()) {
					return ObjectType.of(sourceType.getType(), targetType.getComponentType());
					
				} else if (targetType.isArray()) {
					return ObjectType.of(sourceType.getType(), targetType.getType().getComponentType());
					
				} else {
					return ObjectType.of(sourceType.getType(), targetType.getType());
				}
				
			} else if (sourceType.isArray()) {
				
				if (targetType.isCollection()) {
					return ObjectType.of(typeToArrayType(targetType.getComponentType()));
					
				} else if (targetType.isArray()) {
					return ObjectType.of(typeToArrayType(targetType.getType().getComponentType()));

				} else {
					return ObjectType.of(typeToArrayType(targetType.getType()));
				}
				
			} else {

				if (targetType.isCollection()) {
					return ObjectType.of(targetType.getComponentType());
					
				} else if (targetType.isArray()) {
					return ObjectType.of(targetType.getType().getComponentType());
					
				} else {
					return targetType;
				}
				
			}
		}
		return sourceType;
	}
	
	static final Class<?> typeToArrayType(Class<?> type) {
		return Array.newInstance(type, 0).getClass();
	}

}
