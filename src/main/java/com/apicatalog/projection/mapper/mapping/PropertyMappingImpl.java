package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.converter.ConvertorError;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.SourceObjects;

public class PropertyMappingImpl implements PropertyMapping {

	final Logger logger = LoggerFactory.getLogger(PropertyMappingImpl.class);

	String name;

	SourceMapping source;
		
	TargetMapping target;

	@Override
	public Object compose(int level, SourceObjects sources) throws ProjectionError, ConvertorError {

		logger.debug("Compose property {} value at level {}", name, level);
		
		// get source value
		Optional<Object> value = Optional.ofNullable(source.compose(sources));

		if (value.isEmpty()) {
			logger.trace("  value = null");
			return null;
		}
		
		final Object propertyValue = value.get();
		
		logger.trace("  value = {}", propertyValue);
		
		return target.construct(level, propertyValue, sources);
	}
	
	@Override
	public void decompose(final Object object, SourceObjects sources) throws ProjectionError, ConvertorError {

		logger.debug("Decompose {} = {}", name, object);
		
		// get target value
		Optional<Object[]> values = Optional.ofNullable(target.deconstruct(object));

		if (values.isEmpty() || values.get().length == 0) {
			return;
		}

		source.decompose(values.get(), sources);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SourceMapping getSource() {
		return source;
	}

	public void setSource(SourceMapping source) {
		this.source = source;
	}

	@Override
	public TargetMapping getTarget() {
		return target;
	}

	public void setTarget(TargetMapping target) {
		this.target = target;
	}
}
