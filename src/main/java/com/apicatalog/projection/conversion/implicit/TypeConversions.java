package com.apicatalog.projection.conversion.implicit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.conversion.Conversion;
import com.apicatalog.projection.conversion.UnknownConversion;
import com.apicatalog.projection.object.ObjectType;

public class TypeConversions {
	
	final Logger logger = LoggerFactory.getLogger(TypeConversions.class);

	static final String MSG_CONVERTER_FROM_TO = "Get converter from {} to {}";


	public Optional<Conversion> get(ObjectType sourceType, ObjectType targetType) throws UnknownConversion {

		if (sourceType == null || targetType == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, sourceType, targetType);
		}

		if (sourceType.isCollection()) {
			if (targetType.isCollection()) {
				return collectionToCollection(sourceType, targetType);
			}
			if (targetType.isArray()) {
				return collectionToArray(sourceType, targetType);
			}
		}
		if (sourceType.isArray()) {
			if (targetType.isCollection()) {
				return arrayToCollection(sourceType, targetType);
			}
			if (targetType.isArray()) {
				return arrayToArray(sourceType, targetType);
			}			
		}

		return get(sourceType.getType(), targetType.getType());
	}
	
	Optional<Conversion> get(Class<?> source, Class<?> target) {

		if (source == null || target == null) {
			throw new IllegalArgumentException();
		}
		
//		if (logger.isDebugEnabled()) {
//			logger.debug(MSG_CONVERTER_FROM_TO, source.getCanonicalName(), target.getCanonicalName());
//		}
		

		final Optional<Conversion> conversion = Optional.ofNullable(SimpleTypeConversions.get(source, target));
		
//		if (logger.isTraceEnabled() && conversion.isEmpty()) {
//			logger.trace("No conversion from {} to {} does exist", source.getSimpleName(), target.getSimpleName());
//		}
//
//		if (logger.isTraceEnabled() && conversion.isPresent()) {
//			logger.trace("Found conversion {} from {} to {}.",  conversion.get(), source.getSimpleName(), target.getSimpleName());
//		}

		return conversion;
	}

	Optional<Conversion> collectionToCollection(final ObjectType sourceType, final ObjectType targetType) throws UnknownConversion {
		
		final Conversion componentConversion = 
				!targetType.getComponentType().isAssignableFrom(sourceType.getComponentType())
						? get(sourceType.getComponentType(), targetType.getComponentType())
								.orElseThrow(UnknownConversion::new)
						: null;

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.empty();
		}		
		
		final Conversion conversion = o -> {
			
			final Collection<?> collection = (Collection<?>)o;
			
			if (collection == null || collection.isEmpty()) {
				return Collections.emptyList();
			}
			
			Collection<Object> converted = null;
			
			if (Set.class.isAssignableFrom(targetType.getType())) {
				converted = new LinkedHashSet<>(collection.size());
			
			} else {
				converted = new ArrayList<>(collection.size());
			}

			for (Object object : collection) {
				converted.add(componentConversion.convert(object));
			}			
			
			return converted;
		};
		
		return Optional.of(conversion);
	}
	
	Optional<Conversion> collectionToArray(ObjectType sourceType, ObjectType targetType) throws UnknownConversion {

		final Conversion componentConversion = 
							!targetType.getType().getComponentType().isAssignableFrom(sourceType.getComponentType())
									? get(sourceType.getComponentType(), targetType.getType().getComponentType())
											.orElseThrow(UnknownConversion::new)
									: null;

		// no conversion needed?
		if (componentConversion == null) {

			return Optional.of(o -> {
				
						final Collection<?> collection = (Collection<?>)o;
				
						final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetType.getType().getComponentType(), collection.size());
			
						int index = 0;
						for (Object object : collection) {
							converted[index++] = object;
						}
						
						return converted;
				});
		}
		
		return Optional.of(o -> {
			
			final Collection<?> collection = (Collection<?>)o;
			
			final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetType.getType().getComponentType(), collection.size());

			int index = 0;
			
			for (Object object : collection) {
				converted[index++] = componentConversion.convert(object);
			}
			return converted;
		});
	}
	
	Optional<Conversion> arrayToCollection(ObjectType sourceType, ObjectType targetType) throws UnknownConversion {
		
		final Conversion componentConversion = 
								!targetType.getComponentType().isAssignableFrom(sourceType.getType().getComponentType())
									? get(sourceType.getType().getComponentType(), targetType.getComponentType())
											.orElseThrow(UnknownConversion::new)
									: null;	

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.of(o -> Arrays.asList((Object[])o));
		}
		
		return Optional.of(array -> {
			
			final ArrayList<Object> collection = new ArrayList<>(((Object[])array).length);
			
			for (Object object : (Object[])array) {
				collection.add(componentConversion.convert(object));
			}

			return collection;
		});		
	}
	
	Optional<Conversion> arrayToArray(ObjectType sourceType, ObjectType targetType) throws UnknownConversion {
		
		final Conversion componentConversion = 
							!targetType.getType().getComponentType().isAssignableFrom(sourceType.getType().getComponentType())
									? get(sourceType.getType().getComponentType(), targetType.getType().getComponentType())
											.orElseThrow(UnknownConversion::new)
									: null;

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.empty();
		}
		
		return Optional.of(array -> {
			
			final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetType.getType().getComponentType(), ((Object[])array).length);

			int index = 0;
			
			for (Object object : (Object[])array) {
				converted[index++] = componentConversion.convert(object);
			}

			return (String[])converted;
		});
	}

}
