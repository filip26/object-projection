package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceWriter implements SourceWriter {

	final Logger logger = LoggerFactory.getLogger(SingleSourceWriter.class);

	Setter setter;

	SourceType sourceType;
	
	ObjectType targetType;
	
	Conversion[] conversions;
	
	boolean optional;

	@Override
	public void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError {
		
		if (!context.isAccepted(sourceType)) {
			return;
		}
		
		logger.debug("Write {} to {}.{}, optional = {}, depth = {}", object, sourceType, setter.getName(), optional, queue.length());

		Optional<Object> value = Optional.ofNullable(object);
		
		if (value.isEmpty()) {
			return;
		}
		
		// apply explicit conversions
		if (conversions != null) {
			try {
				for (final Conversion conversion : conversions) {
					if (value.isEmpty()) {
						break;
					}
					value = Optional.ofNullable(conversion.convert(value.get()));
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}
		}
		
		if (value.isEmpty()) {
			return;
		}
		
		Optional<?> instance =  context.get(sourceType);

		if (instance.isEmpty()) {
			
			Optional<Class<?>> instanceClass = context.getAssignableType(sourceType);
			
			if (instanceClass.isEmpty()) {
				return;
			}
			
			instance = Optional.of(ObjectUtils.newInstance(instanceClass.get()));
			context.set(sourceType.getName(), instance.get());
		}

		setter.set(instance.get(), value.get());
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
	}

	@Override
	public boolean isAnyTypeOf(final SourceType... sourceTypes) {
		return Arrays.stream(sourceTypes).anyMatch(type -> type.isAssignableFrom(sourceType));
	}
	
	public void setConversions(Conversion[] conversions) {
		this.conversions = conversions;
	}	
}
