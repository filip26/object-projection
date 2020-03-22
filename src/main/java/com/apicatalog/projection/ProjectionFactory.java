package com.apicatalog.projection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.Function;
import com.apicatalog.projection.fnc.ContextValue;
import com.apicatalog.projection.fnc.InvertibleFunction;
import com.apicatalog.projection.fnc.InvertibleFunctionError;
import com.apicatalog.projection.objects.ProjectedObjects;

public class ProjectionFactory {

	final Logger logger = LoggerFactory.getLogger(ProjectionFactory.class);
	
	final ProjectionIndex index;
	final TypeAdapters adapters;
	
	public ProjectionFactory(ProjectionIndex index) {
		this(index, new TypeAdapters());
	}
	
	public ProjectionFactory(ProjectionIndex index, TypeAdapters adapters) {
		this.index = index;
		this.adapters = adapters;
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
			
			Object value = null;
			
				 value = compose(metaProperty, sources);

			
			logger.trace("set value {} to property {} of {}", value, metaProperty.getName(), metaProjection.getProjectionClass());
			if (value != null) {

				if (metaProperty.isCollection()) {
					
					Collection<Object> collection = new ArrayList<Object>();
						 for (Object item : ((Collection<Object>)value)) {
								List<Object> o = new ArrayList<>();
								o.addAll(Arrays.asList(objects));	// ?!?!?! FIXME
								o.add(item);
							 
							 
							 value = compose(metaProperty.getItemClass(), o.toArray(new Object[0]));
							 collection.add(value);
						 }
							
					value = collection;

				// embedded projection?
				} else if (index.get(metaProperty.getTargetClass()) != null) {	//TODO better
					
					List<Object> o = new ArrayList<>();
					o.addAll(Arrays.asList(objects));	// ?!?!?! FIXME
					o.add(value);
					
					value = compose(metaProperty.getTargetClass(),  o.toArray(new Object[0]));
				}
				
				setPropertyValue(projection, metaProperty.getName(), value);	
			}
		}
		return projection;
	}
	
	Object compose(ProjectionProperty property, ProjectedObjects sources) throws ProjectionError, InvertibleFunctionError {
		
		final List<Object> values = new ArrayList<>();
		
		for (PropertyMapping mapping : property.getMapping()) {
						
			final Object source = sources.get(mapping.getObjectClass(), mapping.getQualifier());
			
			if (source == null) {
				if (mapping.isOptional()) {
					return null;
				}
				
				throw new ProjectionError("Source " + mapping.getObjectClass() + ", qualifier=" + mapping.getQualifier() + ",  is no present.");
			}
			
			String sourcePropertyName = mapping.getPropertyName();
			
			// same name property - no property() declaration
			if (StringUtils.isBlank(sourcePropertyName)) {
				// user projection's property name
				sourcePropertyName = property.getName();
			}

			Object value = getPropertyValue(source, sourcePropertyName);
			
			logger.trace("value {} of property {}, {}", value, sourcePropertyName, mapping.getObjectClass());
			
			final Function[] functions = mapping.getFunctions();
			
			if (functions != null) {
				for (Function fnc : functions) {
					
					final InvertibleFunction ifnc = newInstance(fnc.type());	//TODO re-use preconstructed instances
	
					ContextValue ctx = new ContextValue();
					ctx.setValues(fnc.value());
	
					ifnc.init(ctx);
					
					value = ifnc.compute(value);
				}
			}

			values.add(value);
		}
		
		Object value = values;

		if (values.size() == 1) {
			value = values.iterator().next();
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

			if (metaProperty.isCollection()) {
				
				Collection<Object> collection = new ArrayList<Object>();
				 for (Object item : ((Collection<Object>)value)) {
					 
					 
						Object[] v = decompose(item);
						value = v[0];			//FIXME hack!
//						if (v.length > 1) {	
//							sources.merge(v[1], null);	// ?!?!?!?
//						}

					 collection.add(value);
				 }
					
			value = collection;
 
				
				
			} else if (index.get(metaProperty.getTargetClass()) != null) {	//TODO better
				
//				List<Object> o = new ArrayList<>();
//				o.addAll(Arrays.asList(objects));	// ?!?!?! FIXME
//				o.add(value);
//				

				
				Object[] v = decompose(value);
				
				value = v[0];			//FIXME hack!
				if (v.length > 1) {	
					sources.merge(v[1], null);	// ?!?!?!?
				}
			}
			
			decompose(metaProperty, sources, value);
		}

		return sources.getValues();
	}

	void decompose(ProjectionProperty property, ProjectedObjects sources, Object value) throws ProjectionError, InvertibleFunctionError {
		
		logger.trace("decompose {} of {} ", value, property.getName());
		
		for (PropertyMapping mapping : property.getMapping()) {
						
			final Object source = sources.get(mapping.getObjectClass(), mapping.getQualifier());
			
			if (source == null) {
				throw new ProjectionError("Source " + mapping.getObjectClass() + ", qualifier=" + mapping.getQualifier() + ",  is no present.");
			}
			
			if (mapping.getFunctions() != null) {
				for (Function fnc : mapping.getFunctions()) {
					
					final InvertibleFunction ifnc = newInstance(fnc.type());
					
					ContextValue ctx = new ContextValue();
					ctx.setValues(fnc.value());
					
					ifnc.init(ctx);
					
					value = ifnc.inverse(value)[0];	//FIXME
				}
			}

			setPropertyValue(source, StringUtils.isBlank(mapping.getPropertyName()) ? property.getName() : mapping.getPropertyName(), value);
			
		}		
	}
	
	protected Object getPropertyValue(Object object, String property) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}
		
		try {
			
			final Field field = object.getClass().getDeclaredField(property);
			field.setAccessible(true);
			return field.get(object);
			
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not get property=" + property + " value of " + object.getClass() + ".", e);
		}
	}

	protected void setPropertyValue(Object object, String property, Object value) throws ProjectionError {
		if (object == null) {
			throw new IllegalArgumentException();
		}
		if (StringUtils.isBlank(property)) {
			throw new IllegalArgumentException();
		}
	
		try {
			final Field field = object.getClass().getDeclaredField(property);
			field.setAccessible(true);
			
			if (!field.getType().isInstance(value)) {
				value = adapt(field.getType(), value);
			}
			
			field.set(object, value);
			
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new ProjectionError("Can not set property=" + property + " value of " + object.getClass() + " to " + value + ".", e);
		}
	}
	
	public static <T> T newInstance(Class<? extends T> clazz) throws ProjectionError {
		try {
			
			return clazz.getDeclaredConstructor().newInstance();
					
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			
			throw new ProjectionError("Can not instantiate " + clazz + ".", e);
		}
	}
	
	protected <T> T adapt(Class<? extends T> targetClass, Object object) throws ProjectionError {
		
		final TypeAdapter<T, Object> adapter = adapters.get(targetClass, object.getClass());
		
		if (adapter == null) {
			throw new ProjectionError("Can not convert " + object.getClass() + " to " + targetClass + ".");
		}
		try {
			return adapter.convert(object);
		} catch (TypeAdapterError e) {
			throw new ProjectionError(e);
		}
	}
}
