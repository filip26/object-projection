package com.apicatalog.projection.property.source;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceReader implements SourceReader {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReader.class);

	Getter getter;

	SourceType sourceObjectType;
	
	ObjectType type;
	
	Collection<Conversion<Object, Object>> conversions;
	
	boolean optional;

	@Override
	public Optional<Object> read(CompositionContext context) throws ProjectionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {}.{}, optional = {}", sourceObjectType, getter.getName(), optional);
		}

		final Optional<Object> instance = context.get(sourceObjectType);

		if (instance.isEmpty()) {
			if (optional) {
				return Optional.empty();
			}
			throw new ProjectionError("Source instance of " + sourceObjectType + ",  is not present.");
		}

		// get source value
		Optional<Object> object = getter.get(instance.get());

		if (object.isEmpty()) {
			return Optional.empty();
		}

		if (logger.isTraceEnabled()) {
			logger.trace("{}.{} = {}", sourceObjectType, getter.getName(), object.get());
		}

		// apply conversions
		if (conversions != null) {
			try {
				for (final Conversion<Object, Object> conversion : conversions) {
					
					object = Optional.ofNullable(conversion.convert(object.get()));
					
					if (object.isEmpty()) {
						break;
					}
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
	
	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public ObjectType getType() {
		return type;
	}
		
	public void setSourceObjectType(SourceType sourceObjectType) {
		this.sourceObjectType = sourceObjectType;
	}
	
	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}
}
