package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.target.CollectionComposer;
import com.apicatalog.projection.property.target.TargetComposer;
import com.apicatalog.projection.property.target.ObjectComposer;

public final class ComposerBuilder {

	final Logger logger = LoggerFactory.getLogger(ComposerBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 

	Setter setter;

	boolean reference;
		
	protected ComposerBuilder() {
	}
	
	public static final ComposerBuilder newInstance() {
		return new ComposerBuilder();
	}
		
	public Optional<TargetComposer> build(ProjectionRegistry registry) {

		if (setter == null) {
			return Optional.empty();
		}

		if (reference) {
			if (setter.getType().isCollection()) {
				
				return collection(setter.getType().getComponentType().getCanonicalName(), registry);
				
			}
			if (setter.getType().isArray()) {
				
				return collection(setter.getType().getType().getComponentType().getCanonicalName(), registry);
			}

			return object(setter.getType().getType().getCanonicalName(), registry);
		}
		return Optional.empty();
	}
	
	public ComposerBuilder setter(Setter setter, boolean reference) {
		this.setter = setter;
		this.reference = reference;
		return this;
	}
	
	final Optional<TargetComposer> collection(final String projectionName, final ProjectionRegistry registry) {
		
		final CollectionComposer composer = new CollectionComposer(setter.getType(), projectionName);
		
		registry.request(projectionName, composer::setProjection);
		
		return Optional.of(composer);		
	}
	
	final Optional<TargetComposer> object(final String projectionName, final ProjectionRegistry registry) {
		
		final ObjectComposer composer = new ObjectComposer(projectionName);
		
		registry.request(projectionName, composer::setProjection);
		
		return Optional.of(composer);		
	}
}
