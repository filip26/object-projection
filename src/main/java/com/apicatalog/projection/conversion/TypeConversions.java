package com.apicatalog.projection.conversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.object.ObjectType;

public class TypeConversions {
	
	final Logger logger = LoggerFactory.getLogger(TypeConversions.class);

	static final String MSG_CONVERTER_FROM_TO = "Get converter from {} to {}";

	/**
	 * 
	 * @param sourceType
	 * @param targetType
	 * @return <code>Optional.empty()</code> if no conversion is needed or a conversion
	 * @throws ConversionNotFound if a conversion is needed but does not exist
	 */
	public Optional<Conversion<Object, Object>> get(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {

		if (sourceType == null || targetType == null) {
			throw new IllegalArgumentException();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug(MSG_CONVERTER_FROM_TO, sourceType, targetType);
		}
		
		// no conversion needed?
		if (targetType.isAssignableFrom(sourceType) || sourceType.getType() == Object.class) {
			return Optional.empty();
		}

		if (sourceType.isCollection()) {
			if (targetType.isCollection()) {
				return collectionToCollection(sourceType, targetType);
				
			} else if (targetType.isArray()) {
				return collectionToArray(sourceType, targetType);
			}
			
		} else if (sourceType.isArray()) {
			if (targetType.isCollection()) {
				return arrayToCollection(sourceType, targetType);
				
			} else if (targetType.isArray()) {
				return arrayToArray(sourceType, targetType);
			}
		}
		
		if (targetType.isCollection()) {
			return objectToCollection(sourceType, targetType);
		}
		
		if (targetType.isArray()) {
			return objectToArray(sourceType, targetType);
		}

		Optional<Conversion<Object, Object>> conversion = get(sourceType.getType(), targetType.getType());
		if (conversion.isEmpty()) {
			throw new ConversionNotFound(sourceType, targetType);
		}
		
		return conversion;
	}
	
	Optional<Conversion<Object, Object>> get(Class<?> source, Class<?> target) {

		if (source == null || target == null) {
			throw new IllegalArgumentException();
		}

		return Optional.ofNullable(SimpleTypeConversions.get(source, target));		
	}

	Optional<Conversion<Object, Object>> collectionToCollection(final ObjectType sourceType, final ObjectType targetType) throws ConversionNotFound {
		
		final Conversion<Object, Object> componentConversion = 
				!targetType.getComponentType().isAssignableFrom(sourceType.getComponentType())
						? get(sourceType.getComponentType(), targetType.getComponentType())
								.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
						: null;

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.empty();
		}		
		
		final Conversion<Object, Object> conversion = o -> {
			
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
	
	Optional<Conversion<Object, Object>> collectionToArray(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {

		final Conversion<Object, Object> componentConversion = 
							!targetType.getType().getComponentType().isAssignableFrom(sourceType.getComponentType())
									? get(sourceType.getComponentType(), targetType.getType().getComponentType())
											.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
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
	
	Optional<Conversion<Object, Object>> arrayToCollection(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {
		
		final Conversion<Object, Object> componentConversion = 
								!targetType.getComponentType().isAssignableFrom(sourceType.getType().getComponentType())
									? get(sourceType.getType().getComponentType(), targetType.getComponentType())
											.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
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
		
	Optional<Conversion<Object, Object>> arrayToArray(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {
		
		final Conversion<Object, Object> componentConversion =
											!targetType.getType().getComponentType().isAssignableFrom(sourceType.getType().getComponentType())
											? get(sourceType.getType().getComponentType(), targetType.getType().getComponentType())
													.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
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

			return converted;
		});
	}

	Optional<Conversion<Object, Object>> objectToArray(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {

		final Conversion<Object, Object> componentConversion = 
							!targetType.getType().getComponentType().isAssignableFrom(sourceType.getType())
									? get(sourceType.getType(), targetType.getType().getComponentType())
											.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
									: null;

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.of(o -> {
				final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetType.getType().getComponentType(), 1);

				converted[0] = o;

				return converted;
			});
		}
		
		return Optional.of(o -> {

			final Object[] converted = (Object[])java.lang.reflect.Array.newInstance(targetType.getType().getComponentType(), 1);

			converted[0] = componentConversion.convert(o);

			return converted;
		});
	}

	Optional<Conversion<Object, Object>> objectToCollection(ObjectType sourceType, ObjectType targetType) throws ConversionNotFound {
		
		final Conversion<Object, Object> componentConversion = 
								!targetType.getComponentType().isAssignableFrom(sourceType.getType())
									? get(sourceType.getType(), targetType.getComponentType())
											.orElseThrow(() -> new ConversionNotFound(sourceType, targetType))
									: null;	

		// no conversion needed?
		if (componentConversion == null) {
			return Optional.of(Arrays::asList);
		}
		
		return Optional.of(o -> {
			
			final ArrayList<Object> collection = new ArrayList<>(1);
			
			collection.add(componentConversion.convert(o));

			return collection;
		});		
	}

}
