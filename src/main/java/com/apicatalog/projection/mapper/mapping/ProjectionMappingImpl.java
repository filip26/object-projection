package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.ObjectUtils;
import com.apicatalog.projection.objects.Path;

public class ProjectionMappingImpl<P> implements ProjectionMapping<P> {

	final Logger logger = LoggerFactory.getLogger(ProjectionMappingImpl.class);
	
	final Class<P> projectionClass;
	
	final Collection<PropertyMapping> properties;

	public ProjectionMappingImpl(final Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
	}

	@Override
	public P compose(final Object... objects) throws ProjectionError {
		return compose(Path.create(), objects);
	}

	@Override
	public P compose(final Path path, final Object... objects) throws ProjectionError {

		logger.debug("Compose {} of {} object(s), path = {}", projectionClass.getSimpleName(), objects.length, path.length());

		// check for cycles
		if (path.contains(projectionClass)) {
			logger.debug("  ignored because projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
				
		path.push(projectionClass);
		
		if (logger.isTraceEnabled()) {
			Stream.of(objects).forEach(v -> logger.trace("  {}", v.getClass().getSimpleName()));
		}
		
		final ContextObjects contextObjects = 
				Optional.ofNullable(ContextObjects.of(objects))
						.orElseThrow(IllegalStateException::new); 				

						
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		for (final PropertyMapping propertyMapping : properties) {
			
			logger.trace("Processing property = {}", propertyMapping.getName());
			
			// limit property visibility
			if (propertyMapping.isVisible(path.length() - 1)) {
			
				final Optional<Object> value = Optional.ofNullable(propertyMapping.compose(path, contextObjects));
				
				if (value.isPresent()) {
					logger.trace("  set {} to {}.{}: {}", value.get(), projectionClass.getSimpleName(), propertyMapping.getName(), propertyMapping.getTarget().getTargetClass().getSimpleName());
					
					ObjectUtils.setPropertyValue(
									projection, 
									propertyMapping.getName(),
									value.get()
									);
				}
			}
		}
		
		path.pop();
		
		return projection;
	}

	@Override
	public Object[] decompose(final P projection) throws ProjectionError {
		return decompose(projection, ContextObjects.of());
	}
	
	@Override
	public Object[] decompose(final P projection, final ContextObjects providedContext) throws ProjectionError {
		
		if (projection == null) {
			throw new IllegalArgumentException();
		}
	
		logger.debug("Decompose {}", projection.getClass().getCanonicalName());
				
		final ContextObjects contextObjects = new ContextObjects(providedContext);
		
		for (PropertyMapping propertyMapping : properties) {
	
			final Optional<Object> value = 
					Optional.ofNullable(
						ObjectUtils.getPropertyValue(
										projection, 
										propertyMapping.getName()
										)
						);

			if (value.isEmpty()) {
				continue;
			}
			
			logger.trace("  got {} from {}.{}: {}", value.get(), projectionClass.getSimpleName(), propertyMapping.getName(), propertyMapping.getTarget().getTargetClass().getSimpleName());
			
			propertyMapping.decompose(value.get(), contextObjects);
		}

		return contextObjects.getValues();
	}
	
	@Override
	public <S> S extract(Class<S> sourceObjectClass, String qualifier, P projection) throws ProjectionError {
		
		if (projection == null || sourceObjectClass == null) {
			throw new IllegalArgumentException();
		}
		
		logger.debug("Extract {} from {}", sourceObjectClass.getSimpleName(), projection.getClass().getSimpleName());

		//TODO optimize, don't use decompose
		Object[] objects = decompose(projection);
		
		if (objects == null || objects.length == 0) {
			return null;
		}
		
		return (S)Arrays
					.stream(objects)
					.filter(sourceObjectClass::isInstance)
					.findFirst()
					.orElse(null);
	}

	@Override
	public Collection<PropertyMapping> getProperties() {
		return properties;
	}

	@Override
	public Class<P> getProjectionClass() {
		return projectionClass;
	}

	public void add(PropertyMapping property) {
		this.properties.add(property);
	}

}