package com.apicatalog.projection.conversion.implicit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.object.ObjectType;

public class TypeConversions {
	
	final Logger logger = LoggerFactory.getLogger(TypeConversions.class);

	static final String MSG_CONVERTER_FROM_TO = "Get converter from {} to {}";

	@Deprecated
	public Optional<Conversion> get(Collection<ObjectType> sources, ObjectType target)  {
		
		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, sources, target);
		}
		
		if (sources.isEmpty() || target == null) {
			throw new IllegalArgumentException();
		}

		if (sources.size() > 1 && !target.isArray()) {
			return Optional.empty();
		}
		
		if (sources.size() == 1 && !target.isArray()) {
			System.out.println("TODOOOOOOOOOO");	//FIXME
		}
		
		final Collection<Conversion> conversions = new ArrayList<>();
		
		for (ObjectType source : sources) {
			Conversion conversion = get(source.getType(), target.getType().getComponentType()).orElse(null);
			conversions.add(conversion);
		}
		
		return Optional.of( new MixedArray2Array(conversions.toArray(new TypeConversion[0]),target.getType().getComponentType()));				
	}

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

	public Optional<Conversion> get(ObjectType source, Collection<ObjectType> targets) {
		
		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, source, targets);
		}
		
		if (targets.isEmpty() || source == null) {
			throw new IllegalArgumentException();
		}

		if (targets.size() > 1 && !source.isArray()) {
			return Optional.empty();
		}
		
		if (targets.size() == 1 && !source.isArray()) {
			System.out.println("TODOOOOOOOOOO");	//FIXME
		}
		
		final Collection<Conversion> conversions = new ArrayList<>();
		
		for (ObjectType target : targets) {
			Conversion conversion = get(source.getType().getComponentType(), target.getType()).orElse(null);
			conversions.add(conversion);
		}
		
		
		return Optional.of(new MixedArray2Array(
				conversions.toArray(new TypeConversion[0]),
				Object.class
				));		
	}
}
