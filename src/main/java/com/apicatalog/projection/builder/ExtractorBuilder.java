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
				return collection(getter.getType().getComponentType().getCanonicalName(), registry);
			}
			if (getter.getType().isArray()) {
				return collection(getter.getType().getType().getComponentType().getCanonicalName(), registry);
			}
			
			return object(getter.getType().getType().getCanonicalName(), registry);
		}

		return Optional.empty();
	}
	
	public ExtractorBuilder getter(Getter getter, boolean reference) {
		this.getter = getter;
		this.reference = reference;
		return this;
	}
	
	final Optional<Extractor> collection(final String projectionName, final ProjectionRegistry registry) {
		
		final CollectionExtractor extractor = new CollectionExtractor(getter.getType(), projectionName);
		
		registry.request(projectionName, extractor::setProjection);
		
		return Optional.of(extractor);		
	}

	final Optional<Extractor> object(final String projectionName, final ProjectionRegistry registry) {
		
		final ObjectExtractor extractor = new ObjectExtractor(projectionName);
		
		registry.request(projectionName, extractor::setProjection);
		
		return Optional.of(extractor);		
	}

}
