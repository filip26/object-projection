package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.setter.Setter;
import com.apicatalog.projection.property.target.CollectionComposer;
import com.apicatalog.projection.property.target.Composer;
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
		
	public Optional<Composer> build(ProjectionRegistry registry) {

		if (setter == null) {
			return Optional.empty();
		}

		if (reference) {
			if (setter.getType().isCollection()) {
				return Optional.of(new CollectionComposer(registry, setter.getType(), setter.getType().getComponentType()));
			}
			if (setter.getType().isArray()) {
				return Optional.of(new CollectionComposer(registry, setter.getType(), setter.getType().getType().getComponentType()));
			}

			return Optional.of(new ObjectComposer(registry, setter.getType()));
		}
		return Optional.empty();
	}
	
	public ComposerBuilder setter(Setter setter, boolean reference) {
		this.setter = setter;
		this.reference = reference;
		return this;
	}
}
