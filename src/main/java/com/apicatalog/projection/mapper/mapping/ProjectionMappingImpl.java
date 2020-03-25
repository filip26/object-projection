package com.apicatalog.projection.mapper.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ObjectUtils;
import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.ProjectionMapping;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class ProjectionMappingImpl<P> implements ProjectionMapping<P> {

	final Logger logger = LoggerFactory.getLogger(ProjectionMappingImpl.class);
	
	final Class<P> projectionClass;
	
	final Collection<PropertyMapping> properties;

	public ProjectionMappingImpl(final Class<P> projectionClass) {
		this.projectionClass = projectionClass;
		this.properties = new ArrayList<>();
	}

	@Override
	public P compose(final Object... values) throws ProjectionError, ConvertorError {
		return compose(0, values);
	}

	protected P compose(final int level, final Object... values) throws ProjectionError, ConvertorError {

		logger.debug("Compose {} of {} object(s)", projectionClass.getCanonicalName(), values.length);
		
		if (logger.isTraceEnabled()) {
			Stream.of(values).forEach(v -> logger.trace("  {}", v));
		}
		
		final SourceObjects sources = 
				Optional.ofNullable(SourceObjects.of(values))
						.orElseThrow(IllegalStateException::new); 				

						
		final P projection = ObjectUtils.newInstance(projectionClass);
		
		for (final PropertyMapping propertyMapping : properties) {

			Optional<Object> value = Optional.ofNullable(propertyMapping.compose(level + 1, sources));
			
			if (value.isPresent()) {
				logger.trace("  set {} to {}.{}: {}", value.get(), projectionClass.getSimpleName(), propertyMapping.getName(), propertyMapping.getTarget().getTargetClass().getSimpleName());
				
				ObjectUtils.setPropertyValue(projection, propertyMapping.getName(), value.get());
			}
		}
		return projection;
	}

	@Override
	public Object[] decompose(final P projection) throws ProjectionError, ConvertorError {
		
		if (projection == null) {
			throw new IllegalArgumentException();
		}
	
		logger.debug("Decompose {}", projection.getClass().getCanonicalName());
						
		final SourceObjects sources = SourceObjects.of();
	
		if (sources == null) {
			throw new IllegalStateException();
		}
	
		for (PropertyMapping property : properties) {
	
			final Optional<Object> value = Optional.ofNullable(ObjectUtils.getPropertyValue(projection, property.getName()));

			if (value.isEmpty()) {
				continue;
			}
			
			property.decompose(value.get(), sources);
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
