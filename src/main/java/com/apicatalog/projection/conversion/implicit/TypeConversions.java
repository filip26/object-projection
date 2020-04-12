package com.apicatalog.projection.conversion.implicit;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.object.ObjectType;

public class TypeConversions {
	
	final Logger logger = LoggerFactory.getLogger(TypeConversions.class);

	static final String MSG_CONVERTER_FROM_TO = "Get converter from {} to {}";

	public Optional<Conversion> get(Class<?> source, Class<?> target) {

		if (source == null || target == null) {
			throw new IllegalArgumentException();
		}
		
		if (target.isAssignableFrom(source)) {
			return Optional.empty();
		}

		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, source.getCanonicalName(), target.getCanonicalName());
		}
		

		final Optional<Conversion> conversion = Optional.ofNullable(PrimitiveTypeConversions.get(source, target));
		
		if (logger.isTraceEnabled() && conversion.isEmpty()) {
			logger.trace("No conversion from {} to {} does exist", source.getSimpleName(), target.getSimpleName());
		}

		if (logger.isTraceEnabled() && conversion.isPresent()) {
			logger.trace("Found conversion {} from {} to {}.",  conversion.get(), source.getSimpleName(), target.getSimpleName());
		}

		return conversion;
	}

	public Optional<Conversion> get(ObjectType sourceType, ObjectType targetType) {

		if (sourceType == null || targetType == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, sourceType, targetType);
		}

		//TODO deal with collections
		
		return get(sourceType.getType(), targetType.getType());
	}
}
