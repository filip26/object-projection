package com.apicatalog.projection.builder;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apicatalog.projection.ProjectionRegistry;
import com.apicatalog.projection.object.getter.Getter;
import com.apicatalog.projection.property.target.CollectionExtractor;
import com.apicatalog.projection.property.target.Extractor;
import com.apicatalog.projection.property.target.ObjectExtractor;

public final class ExtractorBuilder {

	final Logger logger = LoggerFactory.getLogger(ExtractorBuilder.class);
	
	static final String SOURCE_IS_MISSING = "Source is missing. Property {} is ignored."; 
	
	Getter getter;
	
	boolean reference;
		
	protected ExtractorBuilder() {
	}
	
	public static final ExtractorBuilder newInstance() {
		return new ExtractorBuilder();
	}
		
	public Optional<Extractor> build(ProjectionRegistry registry) {
		
		if (getter == null) {
			return Optional.empty();
		}

		if (reference) {
			if (getter.getType().isCollection()) {
				return Optional.of(new CollectionExtractor(registry, getter.getType(), getter.getType().getComponentType()));
			}
			if (getter.getType().isArray()) {
				return Optional.of(new CollectionExtractor(registry, getter.getType(), getter.getType().getType().getComponentType()));
			}
			
			return Optional.of(new ObjectExtractor(registry));
		}

		return Optional.empty();
	}
	
	public ExtractorBuilder getter(Getter getter, boolean reference) {
		this.getter = getter;
		this.reference = reference;
		return this;
	}
}
