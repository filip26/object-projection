package com.apicatalog.projection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.annotation.Function;
import com.apicatalog.projection.annotation.Provider;
import com.apicatalog.projection.fnc.ContextValue;
import com.apicatalog.projection.fnc.InvertibleFunction;
import com.apicatalog.projection.fnc.InvertibleFunctionError;

public class ProjectionFactory {

	final Logger logger = LoggerFactory.getLogger(ProjectionFactory.class);
	
	final ProjectionIndex index;
	
	public ProjectionFactory(ProjectionIndex index) {
		this.index = index;
	}
	
	public <P> P compose(Class<? extends P> projectionClass, Object...objects) throws ProjectionError, InvertibleFunctionError {
		if (projectionClass == null) {
			throw new IllegalArgumentException();
		}
		logger.trace("compose projection {} of {} ", projectionClass, objects);
		
		final Projection metaProjection = index.get(projectionClass);
		
		if (metaProjection == null) {
			throw new ProjectionError("The projection for " + projectionClass + " is not present.");
		}
				
		final ProjectedObjects sources = ProjectedObjects.of(objects);
		if (sources == null) {
			throw new IllegalStateException();
		}

		P projection = newInstance(projectionClass);
		
		for (ProjectionProperty metaProperty : metaProjection.getProperties()) {
			logger.trace("process property {} of {}", metaProperty.getName(), metaProjection.getProjectionClass());
			
			Object value = compose(metaProperty, sources);
			
			logger.trace("set value {} to property {} of {}", value, metaProperty.getName(), metaProjection.getProjectionClass());
			if (value != null) {

				if (index.get(metaProperty.getTargetClass()) != null) {	//TODO better
					value = compose(metaProperty.getTargetClass(), value);
				}
				
				setPropertyValue(projection, metaProperty.getName(), value);	
			}
		}
		return projection;
	}
	
	Object compose(ProjectionProperty property, ProjectedObjects sources) throws ProjectionError, InvertibleFunctionError {
		
		final List<Value> values = new ArrayList<>();
		
		for (Provider valueProvider : property.getProviders()) {
						
			final Object source = sources.get(valueProvider.type(), valueProvider.qualifier());
			
			if (source == null) {
				throw new ProjectionError("Source " + valueProvider.type() + ", qualifier=" + valueProvider.qualifier() + ",  is no present.");
			}

			Object rawValue = getPropertyValue(source, valueProvider.property());
			
			logger.trace("value {} of property {}, {}", rawValue, valueProvider.property(), valueProvider.type());
			
			Value value = Value.of(rawValue, valueProvider.id());
			
			final Function[] functions = valueProvider.map();
			
			for (Function fnc : functions) {
				
				InvertibleFunction ifnc = newInstance(fnc.type());
				
				ContextValue ctx = new ContextValue();
				ctx.setValues(fnc.value());
				
				value.setObject(ifnc.compute(ctx, value));
			}

			values.add(value);
		}
		
		Object value = values;

		if (values.size() == 1) {
			value = values.iterator().next().getObject();
		}
		
		return value;
	}
	
	public Object[] decompose(Object projection) throws ProjectionError, InvertibleFunctionError {
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		logger.trace("decompose projection {}. ", projection);
		
		final Projection metaProjection = index.get(projection.getClass());

		if (metaProjection == null) {
			throw new ProjectionError("The projection for " + projection.getClass() + " is not present.");
		}
				
		final ProjectedObjects sources = ProjectedObjects.from(metaProjection);

		if (sources == null) {
			throw new IllegalStateException();
		}

		for (ProjectionProperty metaProperty : metaProjection.getProperties()) {
			logger.trace("process property {} of {}", metaProperty.getName(), metaProjection.getProjectionClass());

			Object value = getPropertyValue(projection, metaProperty.getName());
			
			if (value == null) {
				continue;
			}
			
			if (index.get(metaProperty.getTargetClass()) != null) {	//TODO better
				Object[] v = decompose(value);
				value = (v.length == 1) ? v[0] : v;
			}
			
			decompose(metaProperty, sources, value);
		}

		return sources.getValues();
	}

	void decompose(ProjectionProperty property, ProjectedObjects sources, Object rawValue) throws ProjectionError, InvertibleFunctionError {
		
		logger.trace("decompose {} of {} ", rawValue, property.getName());
		
		for (Provider provider : property.getProviders()) {
						
			final Object source = sources.get(provider.type(), provider.qualifier());
			
			if (source == null) {
				throw new ProjectionError("Source " + provider.type() + ", qualifier=" + provider.qualifier() + ",  is no present.");
			}
			
			Value value = Value.of(rawValue, null);
			
			for (Function fnc : provider.map()) {
				
				InvertibleFunction ifnc = newInstance(fnc.type());
				
				ContextValue ctx = new ContextValue();
				ctx.setValues(fnc.value());
				
				value.setObject(ifnc.inverse(ctx, value)[0]);	//FIXME
			}

			setPropertyValue(source, provider.property(), value.getObject());
			
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
