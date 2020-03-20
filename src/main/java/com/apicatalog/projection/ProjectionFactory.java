package com.apicatalog.projection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.fnc.InvertibleFunction;

public class ProjectionFactory {

	final Logger logger = LoggerFactory.getLogger(ProjectionFactory.class);
	
	final ProjectionIndex index;
	
	public ProjectionFactory(ProjectionIndex index) {
		this.index = index;
	}
	
	public <P> P compose(Class<? extends P> projectionClass, Object...objects) throws ProjectionError {
		if (projectionClass == null) {
			throw new IllegalArgumentException();
		}
		logger.trace("compose projection {} of {} ", projectionClass, objects);
		
		final Projection metaProjection = index.get(projectionClass);
		
		if (metaProjection == null) {
			throw new ProjectionError("The projection for " + projectionClass + " is not present.");
		}
				
		final Sources sources = Sources.of(objects);
		if (sources == null) {
			throw new IllegalStateException();
		}

		P projection = null;
		
		
		for (ProjectionProperty metaProperty : metaProjection.getProperties()) {
			logger.trace("process property {} of {}", metaProperty.getName(), metaProjection.getProjectionClass());
			
			final Object value = compose(metaProperty, sources);
			
			logger.trace("set value {} to property {} of {}", value, metaProperty.getName(), metaProjection.getProjectionClass());
			if (value != null) {
				setPropertyValue(projection, metaProperty.getName(), value);
			}
		}
		return projection;
	}
	
	Object compose(ProjectionProperty property, Sources sources) throws ProjectionError {
		
		final List<Value> values = new ArrayList<>();
		
		for (ValueProvider valueProvider : property.getProviders()) {
						
			final Object source = sources.get(valueProvider.getSourceClass(), valueProvider.getQualifier());
			
			if (source == null) {
				throw new ProjectionError("Source object " + valueProvider.getSourceClass() + ", qualifier=" + valueProvider.getQualifier() + ",  is no present.");
			}

//			MetaObject metaObject = metaObjects.get(source.getClass());
			
//			MetaObjectProperty metaObjectProperty = metaObject.getProperty(valueProvider.getSourcePropertyName());
			
			Object rawValue = getPropertyValue(source, valueProvider.getSourcePropertyName());
			
			logger.trace("have value {} of property {}, {}", rawValue, valueProvider.getSourcePropertyName(), valueProvider.getSourceClass());
			
			Value value = Value.of(rawValue, valueProvider.getValueId());
			
			final InvertibleFunction[] functions = valueProvider.getFunctions();
			
			if (functions != null) {
				for (InvertibleFunction converter : functions) {
					value.setObject(converter.compute(value));
				}
			}

			values.add(value);
		}
		
		Object value = values;

		final InvertibleFunction[] functions = property.getFunctions();
		
//		if (converters != null) {
////			for (InvertibleFunction converter : converters) {
////FIXME				value.setObject(converter.compute(value));
//			}
//		}

//		final Composer composer = property.getConverters();
//		
//		if (composer != null) {
//			value = composer.compose(values);
//			
//		} else
		if (values.size() == 1) {
			value = values.iterator().next().getObject();
		}
		
//		final Converter<Object, Object> converter = property.getConverters();
//		if (converter != null) {
//			return converter.compose(value);
//		}
		
		return value;
	}
	
	public Object[] decompose(Object projection) throws ProjectionError {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		logger.trace("decompose projection {}. ", projection);
		
		final Projection metaProjection = index.get(projection.getClass());

		if (metaProjection == null) {
			throw new ProjectionError("The projection for " + projection.getClass() + " is not present.");
		}
				
		final Sources sources = Sources.from(metaProjection);

		if (sources == null) {
			throw new IllegalStateException();
		}

		for (ProjectionProperty metaProperty : metaProjection.getProperties()) {
			logger.trace("process property {} of {}", metaProperty.getName(), metaProjection.getProjectionClass());

			final Object rawValue = getPropertyValue(projection, metaProperty.getName());
			
			if (rawValue == null) {
				continue;
			}
			
			decompose(metaProperty, sources, rawValue);
//			
//			logger.trace("set value {} to property {} of {}", value, metaProperty.getName(), metaProjection.getProjectionClass());
//			if (value != null) {
//				setPropertyValue(projection, metaProperty.getName(), value);
//			}
		}

		return sources.getValues();
	}

	void decompose(ProjectionProperty property, Sources sources, Object rawValue) throws ProjectionError {
		
		logger.trace("decompose {} of {} ", rawValue, property.getName());
		
		for (ValueProvider valueProvider : property.getProviders()) {
						
			final Object source = sources.get(valueProvider.getSourceClass(), valueProvider.getQualifier());
			
			if (source == null) {
				throw new ProjectionError("Source object " + valueProvider.getSourceClass() + ", qualifier=" + valueProvider.getQualifier() + ",  is no present.");
			}

			setPropertyValue(source, valueProvider.getSourcePropertyName(), rawValue);
			
		}		
	}
	
	protected static Object getPropertyValue(Object object, String property) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}
		
		try {
			
			final Field field = object.getClass().getDeclaredField(property);
			return field.get(object);
			
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not get property=" + property + " value of " + object.getClass() + ".", e);
		}
	}

	protected static void setPropertyValue(Object object, String property, Object value) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}

		try {
			final Field field = object.getClass().getDeclaredField(property);
			field.set(object, value);
			
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not set property=" + property + " value of " + object.getClass() + " to " + value + ".", e);
		}
	}
	
	protected static <T> T newInstance(Class<? extends T> clazz) throws ProjectionError {
		try {
			
			return clazz.getDeclaredConstructor().newInstance();
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			throw new ProjectionError("Can not instantiate " + clazz + ".", e);
		}
	}
}
