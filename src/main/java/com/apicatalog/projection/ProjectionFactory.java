package com.apicatalog.projection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.adapter.TypeAdapter;
import com.apicatalog.projection.adapter.TypeAdapterError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.annotation.IFunction;
import com.apicatalog.projection.ifnc.ContextValue;
import com.apicatalog.projection.ifnc.InvertibleFunction;
import com.apicatalog.projection.ifnc.InvertibleFunctionError;
import com.apicatalog.projection.mapping.MappingIndex;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.objects.ProjectedObjects;

public class ProjectionFactory {

	final Logger logger = LoggerFactory.getLogger(ProjectionFactory.class);
	
	final MappingIndex index;
	final TypeAdapters adapters;
	
	public ProjectionFactory(MappingIndex index) {
		this(index, new TypeAdapters());
	}
	
	public ProjectionFactory(MappingIndex index, TypeAdapters adapters) {
		this.index = index;
		this.adapters = adapters;
	}
	
	public <P> P compose(Class<? extends P> projectionClass, Object...objects) throws ProjectionError, InvertibleFunctionError {
				
		final ProjectedObjects sources = 
				Optional.ofNullable(ProjectedObjects.of(objects))
						.orElseThrow(IllegalStateException::new); 				

		return compose(projectionClass, sources, 0);
	}

	<P> P compose(Class<? extends P> projectionClass, final ProjectedObjects sources, int depth) throws ProjectionError, InvertibleFunctionError {

//		if (depth > 1) {
//			return null;
//		}

		if (projectionClass == null) {
			throw new IllegalArgumentException();
		}
		
		logger.debug("Compose {} of {}", projectionClass.getCanonicalName(), sources);
		
		final ProjectionMapping projectionMapping = 
				Optional.ofNullable(index.get(projectionClass))
						.orElseThrow(() -> new ProjectionError("The projection for " + projectionClass.getCanonicalName() + " is not present."));
						
				
		final P projection = newInstance(projectionClass);
		
		for (final PropertyMapping propertyMapping : projectionMapping.getProperties()) {

			Optional<Object> value = Optional.ofNullable(composeProperty(propertyMapping, sources, depth));
			
			if (value.isPresent()) {
				logger.trace("  set {} to {}.{}: {}", value.get(), projectionMapping.getProjectionClass().getSimpleName(), propertyMapping.getName(), propertyMapping.getTarget().getTargetClass().getCanonicalName());
				setPropertyValue(projection, propertyMapping.getName(), value.get());	
			}
		}
		return projection;
	}

	
	Object composeProperty(final PropertyMapping propertyMapping, final ProjectedObjects sources, int depth) throws ProjectionError, InvertibleFunctionError {
		
		Object[] values = null;
		
		if (propertyMapping.getTarget().isCollection()) {
			
			values = compose(propertyMapping, sources);
			if (values == null) {
				return null;
			}

			Collection<Object> collection = new ArrayList<>();
			 for (Object item : (Collection)values[0]) {	//FIXME hacky hack

				 final ProjectedObjects itemSources = new ProjectedObjects(sources);
				 itemSources.addOrReplace(item);
				 
				 final Object value = compose(propertyMapping.getTarget().getItemClass(), itemSources, depth + 1);

				 collection.add(value);
			 }
					
			values = new Object[] {collection};

		// embedded projection?
		} else if (propertyMapping.getTarget().isReference()) {

			if (depth > 1) {
				return null;
			}
			
			if (propertyMapping.getSources() != null) { 
				
				values = compose(propertyMapping, sources);
				if (values == null) {
					return null;
				}

				final ProjectedObjects embeddedSources = new ProjectedObjects(sources);
				
				for (Object v : values) {
					embeddedSources.addOrReplace(v);
				}				
				
				values = new Object[] {compose(propertyMapping.getTarget().getTargetClass(), embeddedSources, depth + 1)};
				
			} else {
				values = new Object[] {compose(propertyMapping.getTarget().getTargetClass(), sources, depth + 1)};
			}
			
		} else {
			values = compose(propertyMapping, sources);
		}
		
		if (values == null) {
			return null;
		}

		Object value = values;
		
		// apply explicit conversions
		if (propertyMapping.getFunctions() != null && propertyMapping.getFunctions().length > 0) {
			value = applyFunctions(values, propertyMapping.getFunctions());
			
		} else if (values.length > 0) {
			value = values[0];
		}
		return value;
	}
	
	Object[] compose(PropertyMapping property, ProjectedObjects sources) throws ProjectionError, InvertibleFunctionError {
		
		logger.debug("Compose property {}: {}", property.getName(), property.getTarget().getTargetClass().getCanonicalName());
		
		final List<Object> values = new ArrayList<>();
		
		for (final SourceMapping sourceMapping : property.getSources()) {
						
			final Optional<Object> source = 
					Optional.ofNullable(
						sources.get(sourceMapping.getSourceClass(), sourceMapping.getQualifier())
					);
			
			if (source.isEmpty()) {
				if (!sourceMapping.isOptional()) {
					throw new ProjectionError("Source instance of " + sourceMapping.getSourceClass().getCanonicalName() + ", qualifier=" + sourceMapping.getQualifier() + ",  is not present.");
				}
				return null;
			}
			
			final String sourcePropertyName = sourceMapping.getPropertyName();

			Object value = getPropertyValue(source.get(), sourcePropertyName);

			// apply explicit conversions
			if (sourceMapping.getFunctions() != null && sourceMapping.getFunctions().length > 0) {
				value = applyFunctions(new Object[] {value}, sourceMapping.getFunctions());
			}

			logger.trace("  {}.{} = {}: {}", sourceMapping.getSourceClass().getSimpleName(), sourcePropertyName, value, (value != null ? value.getClass().getCanonicalName() : ""));
			
			if (value != null) {
				values.add(value);
			}			
		}
		
		return values.isEmpty() ? null : values.toArray(new Object[0]);
	}
	
	public Object[] decompose(final Object projection) throws ProjectionError, InvertibleFunctionError {
		
		if (projection == null) {
			throw new IllegalArgumentException();
		}
		
		logger.debug("Decompose {}", projection.getClass().getCanonicalName());
		
		final ProjectionMapping projectionMapping = index.get(projection.getClass());

		if (projectionMapping == null) {
			throw new ProjectionError("The projection for " + projection.getClass() + " is not present.");
		}
				
		final ProjectedObjects sources = ProjectedObjects.from(projectionMapping);

		if (sources == null) {
			throw new IllegalStateException();
		}

		for (PropertyMapping propertyMapping : projectionMapping.getProperties()) {

			Object value = getPropertyValue(projection, propertyMapping.getName());
			
			if (value == null) {
				continue;
			}

			if (propertyMapping.getTarget().isCollection()) {
				
				Collection<Object> collection = new ArrayList<>();
				for (Object item : ((Collection<Object>)value)) {
					Object[] v = decompose(item);
					value = v[0];			//FIXME hack!
					collection.add(value);
				}
					
				value = collection;
 				
			// detect embedded projection
			} else if (propertyMapping.getTarget().isReference()) {

				Object[] v = decompose(value);
				
				value = v[0];			//FIXME hack!
				if (v.length > 1) {	
					sources.addOrReplace(v[1]);
				}
			}
			decompose(propertyMapping, sources, value);
		}
		return sources.getValues();
	}

	void decompose(PropertyMapping propertyMapping, ProjectedObjects sources, Object value) throws ProjectionError, InvertibleFunctionError {
		
		logger.trace("Decompose {} = {}: {}", propertyMapping.getName(), value, propertyMapping.getTarget().getTargetClass());
		
		
		
		if (propertyMapping.getFunctions() != null && propertyMapping.getFunctions().length > 0) {
			
			// reverse order
			final ArrayList<IFunction> ifncs = new ArrayList<>(Arrays.asList(propertyMapping.getFunctions()));
			Collections.reverse(ifncs);
			
			for (IFunction fnc : ifncs) {	
				
				final InvertibleFunction<Object> ifnc = (InvertibleFunction<Object>) newInstance(fnc.type());
				if (!ifnc.isReverseable()) {
					return;
				}
				
				ContextValue ctx = new ContextValue();
				ctx.setValues(fnc.value());
				
				ifnc.init(ctx);
				
				value = ifnc.inverse(value)[0];	//FIXME
			}			
		}
		
		
		for (SourceMapping sourceMapping : propertyMapping.getSources()) {

			final Object source = 
					Optional.ofNullable(
						sources.get(sourceMapping.getSourceClass(), sourceMapping.getQualifier())
					)
					.orElseThrow(
						() -> new ProjectionError("Source instance of " + sourceMapping.getSourceClass().getCanonicalName() + ", qualifier=" + sourceMapping.getQualifier() + ",  is not present.")
					);
			
			if (sourceMapping.getFunctions() != null && sourceMapping.getFunctions().length > 0) {
				
				// reverse order
				final ArrayList<IFunction> ifncs = new ArrayList<>(Arrays.asList(sourceMapping.getFunctions()));
				Collections.reverse(ifncs);
				
				for (IFunction fnc : ifncs) { 
					
					final InvertibleFunction<Object> ifnc = (InvertibleFunction<Object>) newInstance(fnc.type());
					if (!ifnc.isReverseable()) {
						return;
					}
					
					ContextValue ctx = new ContextValue();
					ctx.setValues(fnc.value());
					
					ifnc.init(ctx);
					
					value = ifnc.inverse(value)[0];	//FIXME
				}
			}

			setPropertyValue(source, StringUtils.isBlank(sourceMapping.getPropertyName()) ? propertyMapping.getName() : sourceMapping.getPropertyName(), value);
			
		}		
	}
	
	Object applyFunctions(Object[] values, final IFunction[] functions) throws ProjectionError, InvertibleFunctionError {
		
		if (functions == null || functions.length == 0) {
			return values;
		}
		
		Object value = null;
		
		for (final IFunction fnc : functions) {
			
			final InvertibleFunction<?> ifnc = newInstance(fnc.type());	//TODO re-use preconstructed instances

			ContextValue ctx = new ContextValue();
			ctx.setValues(fnc.value());

			ifnc.init(ctx);
			
			if (value == null) {
				value = ifnc.compute(values);
				continue;
			} 
			
			value = ifnc.compute(value);
		}
		return value;
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
		
		final TypeAdapter<Object> adapter = adapters.get(object.getClass());
		
		if (adapter == null) {
			throw new ProjectionError("Can not convert " + object.getClass().getCanonicalName() + " to " + targetClass.getCanonicalName() + ".");
		}
		
		try {
			return adapter.convert(targetClass, object);
			
		} catch (TypeAdapterError e) {
			throw new ProjectionError(e);
		}
	}
}
