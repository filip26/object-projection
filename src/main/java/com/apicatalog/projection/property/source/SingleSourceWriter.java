package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ExtractionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceWriter implements SourceWriter {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriter.class);

	Setter setter;

	SourceType sourceType;
	
	Collection<SourceType> sourceTypes;
	
	ObjectType targetType;
	
	Collection<Conversion<Object, Object>> conversions;
	
	boolean optional;

	@Override
	public void write(final ExtractionContext context, final Object object) throws ExtractionError {
		
		if (!context.isAccepted(sourceType)) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Write {} to {}.{}, optional = {}", object, sourceType, setter.getName(), optional);
		}

		Optional<Object> value = Optional.ofNullable(object);
		
		if (value.isEmpty()) {
			return;
		}

		// apply conversions
		if (conversions != null) {
			try {
				for (final Conversion<Object, Object> conversion : conversions) {

					if (logger.isTraceEnabled()) {
						logger.trace("Applying conversion on {}", value.get());
					}


					value = Optional.ofNullable(conversion.convert(value.get()));
					
					if (value.isEmpty()) {
						return;
					}
				}
			} catch (ConverterError e) {
				throw new ExtractionError(e);
			}
		}

		Optional<?> instance =  context.get(sourceType);

		try {
			if (instance.isEmpty()) {
				
				final Optional<Class<?>> instanceClass = context.getAssignableType(sourceType);
				
				if (instanceClass.isEmpty()) {
					return;
				}
				
				instance = Optional.of(ObjectUtils.newInstance(instanceClass.get()));
				context.set(sourceType.getName(), instance.get());
			}
			
			setter.set(instance.get(), value.get());
			
		} catch (ObjectError e) {
			throw new ExtractionError(e);
		}
	}
	
	public void setSetter(Setter setter) {
		this.setter = setter;
	}
		
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	public void setTargetType(ObjectType targetType) {
		this.targetType = targetType;
	}
	
	public ObjectType getTargetType() {
		return targetType;
	}
		
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
		this.sourceTypes = new HashSet<>(Arrays.asList(sourceType));
	}

	@Override
	public boolean isAnyTypeOf(final SourceType... sourceTypes) {
		return Arrays.stream(sourceTypes).anyMatch(type -> type.isAssignableFrom(sourceType));
	}
	
	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}	
}
