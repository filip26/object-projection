package com.apicatalog.projection.property.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ExtractionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.ObjectUtils;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSource implements Source {

	final Logger logger = LoggerFactory.getLogger(SingleSource.class);

	Getter getter;
	Setter setter;

	SourceType sourceType;
	
	ObjectType targetType;
	
	Conversion<Object, Object>[] readConversions;
	Conversion<Object, Object>[] writeConversions;
	
	boolean optional;

	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
		if (!isReadable()) {
			return Optional.empty();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Read {}.{}, optional = {}, depth = {}", sourceType, getter.getName(), optional, queue.length());
		}

		final Optional<Object> instance = context.get(sourceType);

		if (instance.isEmpty()) {
			if (optional) {
				return Optional.empty();
			}
			throw new ProjectionError("Source instance of " + sourceType + ",  is not present.");
		}

		// get source value
		Optional<Object> object = getter.get(instance.get());

		if (object.isEmpty()) {
			return Optional.empty();
		}

		logger.trace("{}.{} = {}", sourceType, getter.getName(), object.get());
		
		
		// apply explicit conversions
		if (readConversions != null) {
			try {
				for (Conversion<Object, Object> conversion : readConversions) {
					if (object.isPresent()) {
						object = Optional.ofNullable(conversion.convert(object.get()));
					}
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}

		}

		return object;
	}

	@Override
	public void write(ProjectionStack queue, ExtractionContext context, Object object) throws ProjectionError {
		
		if (!isWritable() || !context.isAccepted(sourceType)) {
			return;
		}
		
		logger.debug("Write {} to {}.{}, optional = {}, depth = {}", object, sourceType, setter.getName(), optional, queue.length());

		// apply explicit conversions
		if (writeConversions != null) {
			try {
				for (Conversion<Object, Object> conversion : writeConversions) {
					object = Optional.ofNullable(conversion.convert(object));
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}
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

		setter.set(instance.get(), object);
	}
	
	public void setGetter(Getter getter) {
		this.getter = getter;
	}
	
	public void setSetter(Setter setter) {
		this.setter = setter;
	}
		
	public void setOptional(boolean optional) {
		this.optional = optional;
	}
	
	@Override
	public boolean isReadable() {
		return getter != null;
	}

	@Override
	public boolean isWritable() {
		return setter != null;
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
	public boolean isAnyTypeOf(SourceType... sourceTypes) {
		for (SourceType type : sourceTypes) {
			if (type.isAssignableFrom(sourceType)) {
				return true;
			}
		}
		return false;
	}
	
	public void setWriteConversions(Conversion<Object, Object>[] writeConversions) {
		this.writeConversions = writeConversions;
	}
	
	public void setReadConversions(Conversion<Object, Object>[] readConversions) {
		this.readConversions = readConversions;
	}
}
