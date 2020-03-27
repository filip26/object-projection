package com.apicatalog.projection.mapper.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.mapping.PropertyMapping;
import com.apicatalog.projection.mapping.SourceMapping;
import com.apicatalog.projection.mapping.TargetMapping;
import com.apicatalog.projection.objects.ContextObjects;
import com.apicatalog.projection.objects.Path;

public class PropertyMappingImpl implements PropertyMapping {

	final Logger logger = LoggerFactory.getLogger(PropertyMappingImpl.class);

	String name;

	SourceMapping source;
		
	TargetMapping target;

	@Override
	public Object compose(Path path, ContextObjects context) throws ProjectionError {

		logger.debug("Compose property {}, path = {}", name, path);
		
		// get source value
		Object value = source.compose(path, context);

		if (value == null) {
			logger.trace("  value = null");
			return null;
		}
				
		logger.trace("  value = {}", value);
		
		return target.construct(path, value, context);
	}
	
	@Override
	public void decompose(final Path path, final Object object, ContextObjects sources) throws ProjectionError {

		logger.debug("Decompose property {} = {}", name, object);
		
		// get target value
		Object[] values = target.deconstruct(path, object);

		if (values == null || values.length == 0) {
			return;
		}

		source.decompose(path, values, sources);
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
