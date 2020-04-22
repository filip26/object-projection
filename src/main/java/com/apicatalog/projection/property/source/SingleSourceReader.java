package com.apicatalog.projection.property.source;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.CompositionError;
import com.apicatalog.projection.context.CompositionContext;
import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.converter.ConverterError;
import com.apicatalog.projection.object.ObjectError;
import com.apicatalog.projection.object.ObjectType;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.source.SourceType;

public final class SingleSourceReader implements SourceReader {

	final Logger logger = LoggerFactory.getLogger(SingleSourceReader.class);

	Getter getter;

	SourceType sourceObjectType;
	
	Collection<SourceType> sourceTypes;
	
	ObjectType type;
	
	Collection<Conversion<Object, Object>> conversions;
	
	boolean optional;

	@Override
	public Optional<Object> read(CompositionContext context) throws CompositionError {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Read {}.{}, optional = {}", sourceObjectType, getter.getName(), optional);
		}

		final Optional<Object> instance = context.get(sourceObjectType);

		if (instance.isEmpty()) {
			if (optional) {
				return Optional.empty();
			}
			throw new CompositionError("Source instance of " + sourceObjectType + ",  is not present.");
		}

		try { 
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
				for (final Conversion<Object, Object> conversion : conversions) {
					
					object = Optional.ofNullable(conversion.convert(object.get()));
					
					if (object.isEmpty()) {
						break;
					}
				}
			}
	
			return object;
			
		} catch (ObjectError | ConverterError e) {
			throw new CompositionError(e);
		}

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
	
	public ObjectType getTargetType() {
		return type;
	}
		
	public void setSourceObjectType(SourceType sourceObjectType) {
		this.sourceObjectType = sourceObjectType;
		this.sourceTypes = new HashSet<>(Arrays.asList(sourceObjectType));
	}
	
	public void setConversions(Collection<Conversion<Object, Object>> conversions) {
		this.conversions = conversions;
	}

	@Override
	public Collection<SourceType> getSourceTypes() {
		return sourceTypes;
	}
}
