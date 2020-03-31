package com.apicatalog.projection.mapper.mapping;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionError;
import com.apicatalog.projection.annotation.AccessMode;
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
	
	Set<Integer> visibleLevels;

	@Override
	public Object compose(Path path, ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Compose property {}, path = {}", name, path.length());
		
		// do nothing in a case read is not allowed
		if (AccessMode.WRITE_ONLY.equals(source.getAccessMode())) {
			logger.trace("Composition is nOt allowed mode = {}", source.getAccessMode());
			return null;
		}
		
		// get source value if exists
		final Optional<Object> value = Optional.ofNullable(source.compose(path, contextObjects));
		
		if (value.isEmpty()) {
			return null;
		}
		
		return target.construct(path, value.orElse(null), contextObjects);
	}
	
	@Override
	public void decompose(final Object object, final ContextObjects contextObjects) throws ProjectionError {

		logger.debug("Decompose property {} = {}", name, object);
		
		// do nothing in a case write is not allowed
		if (AccessMode.READ_ONLY.equals(source.getAccessMode())) {
			logger.trace("Decomposition is not allowed mode = {}", source.getAccessMode());
			return;
		}
		
		// get target value if exists
		final Optional<Object> value = Optional.ofNullable(target.deconstruct(object, contextObjects));

		if (value.isEmpty()) {
			return;
		}
		
		source.decompose(value.get(), contextObjects);		
	}

	@Override
	public String getName() {
		return name;
	}
	
	public PropertyMappingImpl setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public SourceMapping getSource() {
		return source;
	}

	public PropertyMappingImpl setSource(SourceMapping source) {
		this.source = source;
		return this;
	}

	@Override
	public TargetMapping getTarget() {
		return target;
	}

	public void setTarget(TargetMapping target) {
		this.target = target;
	}
	
	@Override
	public boolean isVisible(int level) {
		return visibleLevels == null || visibleLevels.isEmpty() || visibleLevels.contains(level);
	}

	public void setVisible(final Set<Integer> levels) {
		this.visibleLevels = levels;
	}
}