package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.adapter.TypeAdapters;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class ProjectionMappingImpl<P> implements ProjectionMapping<P> {

	final Logger logger = LoggerFactory.getLogger(ProjectionMappingImpl.class);
	
	final TypeAdapters adapters;
	
	final Class<P> projectionClass;
	
	final Collection<PropertyMapping> properties;

	public ProjectionMappingImpl(final Class<P> projectionClass, TypeAdapters adapters) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
		this.adapters = adapters;
	}

	@Override
	public P compose(final Object... values) throws ProjectionError {
		return compose(Path.create(), values);
	}

	@Override
	public P compose(final Path path, final Object... values) throws ProjectionError {

		logger.debug("Compose {} of {} object(s), path = {}", projectionClass.getSimpleName(), values.length, path.length());

		// check for cycles
		if (path.contains(projectionClass)) {
			logger.debug("  ignored because projection {} is in processing already", projectionClass.getSimpleName());
			return null;
		}
		
		path.push(projectionClass);
		
		if (logger.isTraceEnabled()) {
			Stream.of(values).forEach(v -> logger.trace("  {}", v.getClass().getSimpleName()));
		}
		
		final ContextObjects sources = 
				Optional.ofNullable(ContextObjects.of(values))
						.orElseThrow(IllegalStateException::new); 				

						
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		for (final PropertyMapping propertyMapping : properties) {

			// limit property visibility
			if (propertyMapping.isVisible(path.length() - 1)) {
			
				Optional<Object> value = Optional.ofNullable(propertyMapping.compose(path, sources));
				
				if (value.isPresent()) {
					logger.trace("  set {} to {}.{}: {}", value.get(), projectionClass.getSimpleName(), propertyMapping.getName(), propertyMapping.getTarget().getTargetClass().getSimpleName());
					
					ObjectUtils.setPropertyValue(projection, propertyMapping.getName(), adapters.convert(propertyMapping.getTarget().getTargetClass(), value.get()));
				}
			}
		}
		
		path.pop();
		
		return projection;
	}

	@Override
	public Object[] decompose(final P projection) throws ProjectionError {
		return decompose(Path.create(), projection);
	}
	
	@Override
	public Object[] decompose(final Path path, final P projection) throws ProjectionError {
		
		if (projection == null) {
			throw new IllegalArgumentException();
		}
	
		logger.debug("Decompose {}", projection.getClass().getCanonicalName());
						
		final ContextObjects sources = ContextObjects.of();
	
		if (sources == null) {
			throw new IllegalStateException();
		}
	
		for (PropertyMapping property : properties) {
	
			final Optional<Object> value = Optional.ofNullable(ObjectUtils.getPropertyValue(projection, property.getName()));

			if (value.isEmpty()) {
				continue;
			}
			
			property.decompose(path, value.get(), sources);
		}

		return sources.getValues();
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
