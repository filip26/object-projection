package com.apicatalog.projection.builder;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.Registry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.target.CollectionComposer;
import com.apicatalog.projection.property.target.ObjectComposer;
import com.apicatalog.projection.property.target.TargetComposer;

public final class ComposerBuilder {

	final Logger logger = LoggerFactory.getLogger(ComposerBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 

	Setter setter;

	String targetProjectionName;
		
	protected ComposerBuilder() {
	}
	
	public static final ComposerBuilder newInstance() {
		return new ComposerBuilder();
	}
		
	public Optional<TargetComposer> build(Registry registry) {

		if (setter == null) {
			return Optional.empty();
		}

		if (StringUtils.isNotBlank(targetProjectionName)) {			
			if (setter.getType().isCollection() || setter.getType().isArray()) {
				
				return collection(targetProjectionName, registry);
			}

			return object(targetProjectionName, registry);
		}
		return Optional.empty();
	}
	
	public ComposerBuilder setter(final Setter setter) {
		this.setter = setter;
		return this;
	}
	
	public ComposerBuilder targetProjection(final String targetProjectionName) {
		this.targetProjectionName = targetProjectionName;
		return this;
	}
	
	final Optional<TargetComposer> collection(final String projectionName, final Registry registry) {
		
		final CollectionComposer composer = new CollectionComposer(setter.getType(), projectionName);

		registry.request(projectionName, composer::setProjection);
		
		return Optional.of(composer);		
	}
	
	final Optional<TargetComposer> object(final String projectionName, final Registry registry) {
		
		final ObjectComposer composer = new ObjectComposer(projectionName);

		registry.request(projectionName, composer::setProjection);
		
		return Optional.of(composer);		
	}
}
