package com.apicatalog.projection.property.source;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.context.ProjectionStack;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceReader implements SourceReader {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReader.class);

	Getter getter;

	SourceType sourceType;
	
	ObjectType targetType;
	
	Conversion[] conversions;
	
	boolean optional;

	@Override
	public Optional<Object> read(ProjectionStack queue, CompositionContext context) throws ProjectionError {
		
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
		if (conversions != null) {
			try {
				for (final Conversion conversion : conversions) {
					if (object.isEmpty()) {
						break;
					}
					object = Optional.ofNullable(conversion.convert(object.get()));
				}
			} catch (ConverterError e) {
				throw new ProjectionError(e);
			}

		}

		return object;
	}
	
	public void setGetter(Getter getter) {
		this.getter = getter;
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
	
	public void setConversions(Conversion[] conversions) {
		this.conversions = conversions;
	}
}
